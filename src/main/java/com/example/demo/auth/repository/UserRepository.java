package com.example.demo.auth.repository;

import com.example.demo.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// 조회 및 저장
public interface UserRepository extends JpaRepository<User, Long> {

	// Email 로 확인하기
    Optional<User> findByEmail(String email);

    // 이미 존재하는 Email 인지 
    boolean existsByEmail(String email);
}
