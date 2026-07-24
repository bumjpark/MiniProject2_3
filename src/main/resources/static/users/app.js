(function () {
  "use strict";

  const STORAGE_PREFIX = "planly.";
  const LEGACY_STORAGE_PREFIX = ["mini", "9."].join("");
  const ACCESS_KEY = STORAGE_PREFIX + "accessToken";
  const REFRESH_KEY = STORAGE_PREFIX + "refreshToken";
  const CALENDAR_KEY = STORAGE_PREFIX + "calendarId";

  function migrateLegacyStorage() {
    ["accessToken", "refreshToken", "calendarId"].forEach((name) => {
      const legacyKey = LEGACY_STORAGE_PREFIX + name;
      const currentKey = STORAGE_PREFIX + name;
      const legacyValue = localStorage.getItem(legacyKey);
      if (!localStorage.getItem(currentKey) && legacyValue) {
        localStorage.setItem(currentKey, legacyValue);
      }
      localStorage.removeItem(legacyKey);
    });
  }

  migrateLegacyStorage();
  const REFRESH_MARGIN_MS = 30 * 1000;
  let refreshRequest = null;

  function accessToken() {
    return localStorage.getItem(ACCESS_KEY);
  }

  function refreshToken() {
    return localStorage.getItem(REFRESH_KEY);
  }

  function setTokens(token, refreshToken) {
    if (token) localStorage.setItem(ACCESS_KEY, token);
    if (refreshToken) localStorage.setItem(REFRESH_KEY, refreshToken);
  }

  function clearTokens() {
    localStorage.removeItem(ACCESS_KEY);
    localStorage.removeItem(REFRESH_KEY);
    localStorage.removeItem(CALENDAR_KEY);
  }

  function decodeJwt(token) {
    if (!token) return null;
    try {
      const payload = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
      const json = decodeURIComponent(
        atob(payload)
          .split("")
          .map((char) => "%" + ("00" + char.charCodeAt(0).toString(16)).slice(-2))
          .join("")
      );
      return JSON.parse(json);
    } catch (_) {
      return null;
    }
  }

  function decodeToken() {
    return decodeJwt(accessToken());
  }

  function tokenNeedsRefresh(token) {
    const payload = decodeJwt(token);
    if (!payload || typeof payload.exp !== "number") return true;
    return payload.exp * 1000 <= Date.now() + REFRESH_MARGIN_MS;
  }

  function currentIdentity() {
    const payload = decodeToken();
    return payload?.sub || payload?.email || "로그인됨";
  }

  function safeNext(value) {
    if (!value) return "todo.html";
    try {
      const url = new URL(value, location.origin);
      if (url.origin !== location.origin || !url.pathname.endsWith(".html")) {
        return "todo.html";
      }
      return url.pathname.split("/").pop() + url.search;
    } catch (_) {
      return "todo.html";
    }
  }

  function requireAuth() {
    if (accessToken() || refreshToken()) return true;
    const next = encodeURIComponent(location.pathname.split("/").pop() + location.search);
    location.replace("login.html?next=" + next);
    return false;
  }

  function isPublicAuthRequest(path) {
    const pathname = new URL(path, location.origin).pathname;
    return ["/users/login", "/users/signup", "/users/refresh"].includes(pathname);
  }

  async function sendRequest(path, options, token) {
    const headers = new Headers(options.headers || {});
    if (options.body && !headers.has("Content-Type")) {
      headers.set("Content-Type", "application/json");
    }
    if (token) headers.set("Authorization", "Bearer " + token);
    return fetch(path, { ...options, headers });
  }

  async function refreshAccessToken() {
    if (refreshRequest) return refreshRequest;

    const storedRefreshToken = refreshToken();
    if (!storedRefreshToken) return null;

    refreshRequest = (async () => {
      try {
        const response = await fetch("/users/refresh", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ refreshToken: storedRefreshToken }),
        });
        if (!response.ok) return null;

        const data = await response.json();
        if (!data.accessToken) return null;

        localStorage.setItem(ACCESS_KEY, data.accessToken);
        return data.accessToken;
      } catch (_) {
        return null;
      }
    })();

    try {
      return await refreshRequest;
    } finally {
      refreshRequest = null;
    }
  }

  function redirectToLogin() {
    clearTokens();
    const next = encodeURIComponent(location.pathname.split("/").pop() + location.search);
    location.replace("login.html?next=" + next);
  }

  async function api(path, options = {}) {
    const publicRequest = isPublicAuthRequest(path);
    let token = accessToken();
    let refreshAttempted = false;

    if (!publicRequest && refreshToken() && (!token || tokenNeedsRefresh(token))) {
      refreshAttempted = true;
      const refreshedToken = await refreshAccessToken();
      if (refreshedToken) token = refreshedToken;
    }

    let response;
    try {
      response = await sendRequest(path, options, publicRequest ? null : token);
    } catch (_) {
      throw new Error("서버에 연결할 수 없습니다. 서버 실행 상태를 확인해주세요.");
    }

    if (
      !publicRequest &&
      !refreshAttempted &&
      refreshToken() &&
      (response.status === 401 || response.status === 403)
    ) {
      refreshAttempted = true;
      const refreshedToken = await refreshAccessToken();
      if (refreshedToken) {
        token = refreshedToken;
        try {
          response = await sendRequest(path, options, token);
        } catch (_) {
          throw new Error("서버에 연결할 수 없습니다. 서버 실행 상태를 확인해주세요.");
        }
      }
    }

    if (
      !publicRequest &&
      (response.status === 401 ||
        (response.status === 403 && (!token || tokenNeedsRefresh(token))))
    ) {
      redirectToLogin();
      throw new Error("로그인이 만료되었습니다.");
    }

    if (!response.ok) {
      const contentType = response.headers.get("content-type") || "";
      let message = `요청을 처리하지 못했습니다. (${response.status})`;
      try {
        if (contentType.includes("json")) {
          const data = await response.json();
          message = data.message || data.detail || data.error || message;
        } else {
          const text = await response.text();
          if (text && text.length < 240) message = text;
        }
      } catch (_) {
        // Keep the status-based fallback message.
      }
      throw new Error(message);
    }

    if (response.status === 204) return null;
    const contentType = response.headers.get("content-type") || "";
    return contentType.includes("json") ? response.json() : response.text();
  }

  function escapeHtml(value) {
    return String(value ?? "")
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&#039;");
  }

  function formatDate(value, empty = "날짜 없음") {
    if (!value) return empty;
    const date = new Date(value + (String(value).length === 10 ? "T00:00:00" : ""));
    if (Number.isNaN(date.getTime())) return value;
    return new Intl.DateTimeFormat("ko-KR", {
      year: "numeric",
      month: "short",
      day: "numeric",
    }).format(date);
  }

  function formatDateTime(value, empty = "시간 미정") {
    if (!value) return empty;
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return value;
    return new Intl.DateTimeFormat("ko-KR", {
      month: "short",
      day: "numeric",
      weekday: "short",
      hour: "2-digit",
      minute: "2-digit",
    }).format(date);
  }

  function localDateTimeValue(value) {
    if (!value) return "";
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return "";
    const local = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
    return local.toISOString().slice(0, 16);
  }

  function todayValue() {
    const date = new Date();
    const local = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
    return local.toISOString().slice(0, 10);
  }

  function toast(message, type = "info") {
    let region = document.querySelector(".toast-region");
    if (!region) {
      region = document.createElement("div");
      region.className = "toast-region";
      region.setAttribute("aria-live", "polite");
      document.body.appendChild(region);
    }
    const item = document.createElement("div");
    item.className = "toast" + (type === "error" ? " toast--error" : "");
    item.textContent = message;
    region.appendChild(item);
    window.setTimeout(() => item.remove(), 3600);
  }

  function showStatus(element, message, type = "info") {
    if (!element) return;
    element.hidden = !message;
    element.textContent = message || "";
    element.className =
      "status-strip" +
      (type === "error"
        ? " status-strip--error"
        : type === "success"
          ? " status-strip--success"
          : "");
  }

  function confirmAction({
    title = "계속할까요?",
    message = "이 작업을 진행합니다.",
    confirmText = "확인",
    danger = false,
  } = {}) {
    return new Promise((resolve) => {
      const dialog = document.createElement("dialog");
      dialog.className = "dialog";
      dialog.innerHTML = `
        <div class="dialog__body">
          <h2>${escapeHtml(title)}</h2>
          <p class="muted" style="margin-bottom:0">${escapeHtml(message)}</p>
        </div>
        <div class="dialog__actions">
          <button class="btn btn--compact btn--quiet" value="cancel">취소</button>
          <button class="btn btn--compact ${danger ? "btn--danger" : ""}" value="confirm">
            ${escapeHtml(confirmText)}
          </button>
        </div>`;
      document.body.appendChild(dialog);
      dialog.addEventListener("click", (event) => {
        if (event.target === dialog) dialog.close("cancel");
      });
      dialog.addEventListener(
        "close",
        () => {
          resolve(dialog.returnValue === "confirm");
          dialog.remove();
        },
        { once: true }
      );
      dialog.querySelectorAll("button").forEach((button) => {
        button.addEventListener("click", () => dialog.close(button.value));
      });
      dialog.showModal();
    });
  }

  async function logout({ redirect = true } = {}) {
    if (accessToken() || refreshToken()) {
      try {
        await api("/users/logout", { method: "POST" });
      } catch (_) {
        // Local logout still completes when the server token is already invalid.
      }
    }
    clearTokens();
    if (redirect) location.replace("home.html");
  }

  function activePage() {
    return location.pathname.split("/").pop() || "home.html";
  }

  function renderHeader() {
    const target = document.querySelector("[data-site-header]");
    if (!target) return;
    const loggedIn = Boolean(accessToken() || refreshToken());
    const page = activePage();
    const navItems = [
      ["todo.html", "할 일"],
      ["todolist.html", "목록"],
      ["calender.html", "캘린더"],
      ["scadule.html", "일정"],
    ];
    const links = navItems
      .map(
        ([href, label]) =>
          `<a href="${href}" ${page === href ? 'aria-current="page"' : ""}>${label}</a>`
      )
      .join("");
    target.innerHTML = `
      <header class="site-header">
        <div class="site-header__inner">
          <a class="brand" href="home.html" aria-label="Planly 홈">
            <span class="brand__mark">P</span><span>Planly</span>
          </a>
          ${loggedIn ? `<nav class="site-nav" aria-label="주요 메뉴">${links}</nav>` : ""}
          <div class="site-header__actions">
            ${
              loggedIn
                ? `<span class="session-label">${escapeHtml(currentIdentity())}</span>
                   <a class="btn btn--compact btn--quiet" href="logout.html">로그아웃</a>`
                : `<a class="btn btn--compact btn--text" href="login.html">로그인</a>
                   <a class="btn btn--compact" href="join.html">시작하기</a>`
            }
          </div>
        </div>
        ${loggedIn ? `<nav class="mobile-nav" aria-label="모바일 메뉴">${links}</nav>` : ""}
      </header>`;
  }

  function selectedCalendarId() {
    const query = new URLSearchParams(location.search).get("calendarId");
    return query || localStorage.getItem(CALENDAR_KEY) || "";
  }

  function setSelectedCalendarId(id) {
    if (id) localStorage.setItem(CALENDAR_KEY, String(id));
    else localStorage.removeItem(CALENDAR_KEY);
  }

  function params(object) {
    const result = new URLSearchParams();
    Object.entries(object).forEach(([key, value]) => {
      if (value !== "" && value !== null && value !== undefined) {
        result.set(key, value);
      }
    });
    return result;
  }

  document.addEventListener("DOMContentLoaded", renderHeader);

  window.Planly = {
    accessToken,
    api,
    clearTokens,
    confirmAction,
    currentIdentity,
    escapeHtml,
    formatDate,
    formatDateTime,
    localDateTimeValue,
    logout,
    params,
    refreshAccessToken,
    refreshToken,
    requireAuth,
    safeNext,
    selectedCalendarId,
    setSelectedCalendarId,
    setTokens,
    showStatus,
    todayValue,
    toast,
  };
})();
