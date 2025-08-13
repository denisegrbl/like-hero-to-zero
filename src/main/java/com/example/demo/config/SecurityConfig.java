package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/emissions", "/country/**",
                                "/webjars/**", "/css/**", "/js/**", "/images/**"
                        ).permitAll()
                        .requestMatchers("/manage/**").hasAnyRole("SCIENTIST","ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.loginPage("/login").permitAll()) // eigene Login-Seite (optional)
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
