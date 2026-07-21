package com.example.demo.auth.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter{

	private final JwtUtil jwtUtil;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader("Authorization");
		
		if(header != null && header.startsWith("Bearer ")) {
			String token = header.substring(7);
			
			if(jwtUtil.validateToken(token)) {
				Authentication authentication = jwtUtil.getAuthentication(token);
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
				
				
			}
		}
		filterChain.doFilter(request, response);
		
	}
	
}
