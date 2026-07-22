package com.example.demo.auth.repository;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.auth.entity.User;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // 테스트 완료 후 자동으로 롤백됩니다.
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("이메일로 사용자 조회 - 성공")
    void findByEmail_success() {
        User user = User.builder()
                .email("test@example.com")
                .password("1234")
                .name("홍길동")
                .build();
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.get().getName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("존재하지 않는 이메일 조회 시 Empty 반환")
    void findByEmail_notFound() {
        Optional<User> foundUser = userRepository.findByEmail("notfound@example.com");

        assertThat(foundUser).isEmpty();
    }
}
