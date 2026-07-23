package com.example.demo.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.auth.dto.SignupRequestDto;
import com.example.demo.auth.dto.TokenRefreshResponseDto;
import com.example.demo.auth.dto.UserResponseDto;
import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.auth.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Override
	public UserResponseDto signup(SignupRequestDto request) {
		// 중복 체크
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 존재하는 사용자입니다.");
		}

		// 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		User user = User.builder()
				.email(request.getEmail())
				.password(encodedPassword)
				.name(request.getName())
				.build();

		User savedUser = userRepository.save(user);

		return new UserResponseDto(
				savedUser.getId(),
				savedUser.getEmail(),
				savedUser.getName());
	}

	@Override
	public void logout(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
		user.setRefreshToken(null);
		userRepository.save(user);
	}

	@Override
	public TokenRefreshResponseDto refreshAccessToken(String refreshToken) {
		// 1. Refresh Token JWT 서명/만료 검증
		if (!jwtUtil.validateToken(refreshToken)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh Token이 유효하지 않습니다.");
		}

		// 2. Refresh Token에서 이메일 파싱
		String email = jwtUtil.getEmailFromToken(refreshToken);

		// 3. DB에서 사용자 조회
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

		// 4. DB에 저장된 Refresh Token과 요청 토큰 비교
		if (!refreshToken.equals(user.getRefreshToken())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh Token이 일치하지 않습니다.");
		}

		// 5. 새 Access Token 생성 후 반환
		String newAccessToken = jwtUtil.createAccessToken(email);
		return new TokenRefreshResponseDto(newAccessToken);
	}

}
