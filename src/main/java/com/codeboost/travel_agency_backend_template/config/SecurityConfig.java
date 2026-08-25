package com.codeboost.travel_agency_backend_template.config;

import com.codeboost.travel_agency_backend_template.security.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final String rememberMeKey;

    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            @Value("${app.security.remember-me-key}")
            String rememberMeKey
    ) {
        this.userDetailsService = userDetailsService;
        this.rememberMeKey = rememberMeKey;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new AuthenticationSuccessHandler() {

            @Override
            public void onAuthenticationSuccess(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    Authentication authentication
            ) throws IOException {

                String email =
                        authentication.getName();

                String nombre =
                        extractNombre(email);

                String encoded =
                        URLEncoder.encode(
                                nombre,
                                StandardCharsets.UTF_8
                        );

                response.sendRedirect(
                        "/?welcome=" + encoded
                );
            }

            private String extractNombre(String email) {
                if (email == null || email.isBlank()) {
                    return "Usuario";
                }

                String local =
                        email.contains("@")
                                ? email.substring(
                                        0,
                                        email.indexOf("@")
                                )
                                : email;

                if (local.isBlank()) {
                    return "Usuario";
                }

                return local.substring(0, 1).toUpperCase()
                        + local.substring(1);
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .authenticationProvider(authProvider())

                .authorizeHttpRequests(authorize -> authorize

                        /*
                         * Páginas públicas
                         */
                        .requestMatchers(
                                "/",
                                "/login",
                                "/registro",
                                "/privacidad",
                                "/error"
                        ).permitAll()

                        /*
                         * Recursos del frontend.
                         *
                         * Esto permite:
                         * /assets/css/**
                         * /assets/js/**
                         * /assets/images/**
                         * /assets/videos/**
                         */
                        .requestMatchers(
                                "/assets/**"
                        ).permitAll()

                        /*
                         * Compatibilidad si todavía existen recursos
                         * en las carpetas anteriores.
                         */
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/images/**",
                                "/videos/**"
                        ).permitAll()

                        /*
                         * Archivos públicos generales
                         */
                        .requestMatchers(
                                "/favicon.ico",
                                "/robots.txt",
                                "/sitemap.xml"
                        ).permitAll()

                        /*
                         * Acceso administrativo
                         */
                        .requestMatchers(
                                "/dashboard/**"
                        ).hasRole("ADMIN")

                        /*
                         * Cualquier otra ruta requiere autenticación
                         */
                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(successHandler())
                        .failureUrl("/login?error")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies(
                                "JSESSIONID",
                                "remember-me"
                        )
                        .permitAll()
                )

                .rememberMe(remember -> remember
                        .key(rememberMeKey)
                        .tokenValiditySeconds(86_400 * 7)
                        .userDetailsService(userDetailsService)
                        .rememberMeParameter("remember-me")
                );

        return http.build();
    }
}
