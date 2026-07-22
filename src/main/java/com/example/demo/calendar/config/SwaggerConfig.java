package com.example.demo.calendar.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

// Swagger UI에 JWT를 입력할 수 있는 Authorize 버튼을 추가하고 그룹 태그 정렬을 설정하기 위한 클래스
@Configuration
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class SwaggerConfig {

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            List<Tag> orderedTags = Arrays.asList(
                    new Tag().name("사용자 API").description("회원가입 등 사용자 관련 API"),
                    new Tag().name("인증 API").description("로그인 및 로그아웃 관련 API"),
                    new Tag().name("Calendar").description("공유 캘린더 CRUD API"),
                    new Tag().name("Schedule").description("캘린더 일정 CRUD API"),
                    new Tag().name("Category").description("Todo 카테고리 조회 및 생성 API"),
                    new Tag().name("TodoList").description("Todo 리스트 CRUD API"),
                    new Tag().name("Todo").description("Todo 항목 CRUD API"));
            openApi.setTags(orderedTags);
        };
    }
}
