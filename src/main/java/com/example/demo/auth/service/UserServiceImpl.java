package com.example.demo.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.auth.dto.SignupRequestDto;
import com.example.demo.auth.dto.UserResponseDto;
import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public UserResponseDto signup(SignupRequestDto request) {
		//중복 체크
		if(userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"이미 존재하는 사용자입니다.");
		}
		
		//비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(request.getPassword());
		
		User user = User.builder()
					.email(request.getEmail())
					.password(encodedPassword)
					.role("ROLE_USER")
					.build();
					
		
		User savedUser = userRepository.save(user);
		
		return new UserResponseDto(
	            savedUser.getId(),
	            savedUser.getEmail(),
	            savedUser.getRole()
	    );
	}
	
}
