package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)   // <— WICHTIG
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // öffentlich
                        .requestMatchers("/", "/emissions", "/country/**").permitAll()
                        .requestMatchers("/webjars/**", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        .requestMatchers("/login", "/logout").permitAll()
                        // WICHTIG: erst die spezifische Manage-Edit-Regel,
                        // damit Admin UND Scientist hierdurch dürfen:
                        .requestMatchers("/manage/emissions/**").hasAnyRole("SCIENTIST","ADMIN")

                        // übrige manage-Routen nur für Scientist
                        .requestMatchers("/manage/**").hasRole("SCIENTIST")

                        // Admin-Bereich
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .logout(l -> l.logoutSuccessUrl("/").permitAll());
        return http.build();
    }

    @Bean
    UserDetailsService users() {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin").password("{noop}admin123").roles("ADMIN").build(),
                User.withUsername("sci").password("{noop}sci123").roles("SCIENTIST").build()
        );
    }
}
