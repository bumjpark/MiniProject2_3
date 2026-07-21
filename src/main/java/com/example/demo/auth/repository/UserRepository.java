package com.example.demo.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.auth.entity.User;

public interface UserRepository  extends JpaRepository<User, Long>	{
	Optional<User> findByEmail(String email);

    @Query(value = """
            select
                user_id as userId,
                email,
                password,
                role
            from user
            where email = :email
            limit 1
            """, nativeQuery = true)
    Optional<AuthUserProjection> findAuthByEmail(
            @Param("email") String email
    );
}
