package com.example.demo.calendar.service;

import com.example.demo.auth.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// JWT로 인증된 현재 요청자의 사용자 id를 꺼내주는 헬퍼
@Component
public class CurrentUserResolver {

    public Long getCurrentUserId() {
        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser().getId();
    }
}
