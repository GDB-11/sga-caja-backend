package com.cibertec.sga.common.config;

import com.cibertec.sga.common.security.IJwtService;
import com.cibertec.sga.common.security.JwtAuthenticationFilter;
import com.cibertec.sga.common.web.RestAccessDeniedHandler;
import com.cibertec.sga.common.web.RestAuthenticationEntryPoint;
import com.cibertec.sga.user.domain.repository.IUserRepository;
import com.cibertec.sga.user.infrastructure.persistence.UserJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * API sin estado (sin sesión, sin CSRF): autenticación por JWT Bearer (RNF-01), autorización
 * fina por rol vía {@code @PreAuthorize} en cada controller según la matriz RBAC del plan
 * (habilitado por {@code @EnableMethodSecurity}).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
        IJwtService jwtService, IUserRepository userRepository, UserJpaRepository userJpaRepository
    ) {
        return new JwtAuthenticationFilter(jwtService, userRepository, userJpaRepository);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtAuthenticationFilter jwtAuthenticationFilter,
        RestAuthenticationEntryPoint authenticationEntryPoint,
        RestAccessDeniedHandler accessDeniedHandler
    ) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {
            })
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/auth/refresh", "/api/auth/logout").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(handling -> handling
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
