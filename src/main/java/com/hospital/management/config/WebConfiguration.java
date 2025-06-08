package com.hospital.management.config;

import com.hospital.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Collection;

@Configuration
@EnableAutoConfiguration
public class WebConfiguration {

    @Autowired
    private UserService userService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/RegisterPatient", "/RegisterDoctor", "/patient/register", "/doctor/register", "/success").permitAll()
                        .requestMatchers("/patient").hasAuthority("PATIENT") // Only patients can access
                        .requestMatchers("/doctor").hasAuthority("DOCTOR") // Only doctors can access
                        .requestMatchers("/admin").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // Points to your controller
                        .successHandler(customAuthenticationSuccessHandler()) // Handles role-based redirection
                        .loginProcessingUrl("/login")
                        .failureUrl("/login?error=true") // Matches login form action
                        .permitAll()
                )
                .logout(logout -> logout.permitAll())
                .exceptionHandling(configure->        configure.accessDeniedPage("/access-denied"));
        ;

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userService)  // Ensure your service correctly loads user details
                .passwordEncoder(passwordEncoder()); // This uses NoOpPasswordEncoder

        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // use this to hash passwords
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            String role = "";
            for (GrantedAuthority authority : authorities) {
                role = authority.getAuthority(); // Extracts the correct role
            }
            System.out.println("role is"+role);
            if (role.equals("PATIENT")) {
                response.sendRedirect("/patient/dashboard");
            } else if (role.equals("DOCTOR")) {
                response.sendRedirect("/doctorDashboard");
            } else if (role.equals("ADMIN")) {
                response.sendRedirect("/admin");
            } else {
                response.sendRedirect("/login"); // Fallback option
            }
        };
    }
}

