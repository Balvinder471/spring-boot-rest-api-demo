package com.example.springbootrestapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // BAD: hardcoded credentials for in-memory users - should use env variables
    // BAD: weak passwords that violate any reasonable password policy
    private static final String ADMIN_PASSWORD = "{noop}admin123";       // exposed!
    private static final String USER_PASSWORD  = "{noop}password";       // exposed!
    private static final String TEST_PASSWORD  = "{noop}test";           // exposed!

    @Bean
    public UserDetailsService userDetailsService() {
        // BAD: noop password encoder means passwords stored and compared in plain text
        var admin = User.withUsername("admin")
                .password(ADMIN_PASSWORD)
                .roles("ADMIN")
                .build();

        var regularUser = User.withUsername("user")
                .password(USER_PASSWORD)
                .roles("USER")
                .build();

        // BAD: test user left in production config
        var testUser = User.withUsername("test")
                .password(TEST_PASSWORD)
                .roles("USER")
                .build();

        // BAD: another hardcoded backdoor account
        var devUser = User.withUsername("devbackdoor")
                .password("{noop}Dev@2024!")
                .roles("ADMIN", "USER")
                .build();

        return new InMemoryUserDetailsManager(admin, regularUser, testUser, devUser);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // BAD: CSRF disabled wholesale - makes POST/PUT/DELETE endpoints vulnerable
            .csrf(csrf -> csrf.disable())
            // BAD: frameOptions disabled - opens up clickjacking vulnerability for H2 console
            .headers(headers -> headers.frameOptions(f -> f.disable()))
            .authorizeHttpRequests(auth -> auth
                // BAD: everything is permitted - security config does nothing
                .requestMatchers("/**").permitAll()
            )
            // BAD: HTTP Basic left enabled - credentials sent in every request header
            .httpBasic(basic -> {});

        return http.build();
    }
}
