package com.event.ems.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // ✅ Plain text password encoder (dev only)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence raw) {
                return raw.toString();
            }
            @Override
            public boolean matches(CharSequence raw, String encoded) {
                return raw.toString().equals(encoded);
            }
        };
    }

    // ✅ CORS config
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000"
        ));
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT",
                "DELETE", "OPTIONS", "PATCH"
        ));
        config.setAllowedHeaders(List.of(
                "Authorization", "Content-Type",
                "Accept", "Origin", "X-Requested-With"
        ));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(
                        corsConfigurationSource()))
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ✅ CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        // ✅ Public auth
                        .requestMatchers("/api/auth/**")
                        .permitAll()

                        // ✅ Public event browsing
                        .requestMatchers(HttpMethod.GET,
                                "/api/events/approved")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/events/{id}")
                        .permitAll()

                        // ✅ FIXED — use hasAuthority (consistent)
                        .requestMatchers("/api/admin/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/organizer/**")
                        .hasAuthority("ROLE_ORGANIZER")
                        .requestMatchers("/api/user/**")
                        .hasAuthority("ROLE_USER")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}