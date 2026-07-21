package com.example.demo.auth.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Void> home(Authentication authentication) {
        boolean loggedIn = authentication != null
                && authentication.isAuthenticated()
                && !(authentication
                        instanceof AnonymousAuthenticationToken);

        URI destination = URI.create(
                loggedIn ? "/api/auth/me" : "/login"
        );

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(destination)
                .build();
    }
}
