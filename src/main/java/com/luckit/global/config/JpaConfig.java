package com.luckit.global.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // Auditing 기능 활성화
public class JpaConfig {
}
