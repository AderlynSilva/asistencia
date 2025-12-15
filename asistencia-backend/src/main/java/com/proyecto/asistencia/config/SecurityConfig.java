package com.proyecto.asistencia.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.proyecto.asistencia.security.JwtAuthFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final SecurityExceptionHandlers securityExceptionHandlers;

    public SecurityConfig(
            JwtAuthFilter jwtAuthFilter,
            SecurityExceptionHandlers securityExceptionHandlers) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.securityExceptionHandlers = securityExceptionHandlers;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {}) 
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(securityExceptionHandlers) 
                .accessDeniedHandler(securityExceptionHandlers)      
            )

            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html"
                ).permitAll()

                .requestMatchers(HttpMethod.POST, "/api/asistencia/checkin").hasAnyRole("EMPLEADO", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/asistencia/checkout").hasAnyRole("EMPLEADO", "ADMIN")
                .requestMatchers(HttpMethod.GET,  "/api/asistencia/historial").hasAnyRole("EMPLEADO", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/justificaciones").hasAnyRole("EMPLEADO", "ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/justificaciones/*/estado").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/justificaciones/pendientes").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/reportes/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
