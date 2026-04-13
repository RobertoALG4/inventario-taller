package com.taller.inventario.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurity {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // SOLO OFICINA
                .requestMatchers("/inventario/**", "/movimiento/**").hasRole("OFICINA")
                .requestMatchers("/historial/**").hasRole("OFICINA")

                // INFORMES → ambos
                .requestMatchers("/informes/**").hasAnyRole("OFICINA", "MECANICO")

                // LOGIN libre
                .requestMatchers("/login").permitAll()

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/informes", true)
                .permitAll()
            )
            .logout(logout -> logout.permitAll());

        return http.build();
    }

    // 🔥 AÑADE ESTO AQUÍ (dentro de la clase, pero fuera del método de arriba)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}