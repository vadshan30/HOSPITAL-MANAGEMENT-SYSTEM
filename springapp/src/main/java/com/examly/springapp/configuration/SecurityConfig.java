package com.examly.springapp.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.examly.springapp.security.AppUserDetailsService;
import com.examly.springapp.security.JwtAuthFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile({"default", "security-test"})
public class SecurityConfig {

    @Autowired
    private AppUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationEntryPoint unauthorizedHandler =
                (request, response, ex) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        http
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                ).permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/doctors/**").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/doctors/**").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/doctors/**").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/doctors/**").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                .requestMatchers(HttpMethod.POST, "/patients/**").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/patients/**").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                .requestMatchers(HttpMethod.DELETE, "/patients/**").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/patients/**").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                .requestMatchers(HttpMethod.POST, "/appointments/**").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                .requestMatchers(HttpMethod.PUT, "/appointments/**").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                .requestMatchers(HttpMethod.GET, "/appointments/**").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                .requestMatchers(HttpMethod.POST, "/medicalrecords/**").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                .requestMatchers(HttpMethod.PUT, "/medicalrecords/**").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                .requestMatchers(HttpMethod.GET, "/medicalrecords/**").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
