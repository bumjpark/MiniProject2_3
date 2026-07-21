package com.example.demo.auth.repository;

public interface AuthUserProjection {

    Long getUserId();

    String getEmail();

    String getPassword();

    String getRole();
}
