package com.taller.inventario.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.taller.inventario.model.Rol;
import com.taller.inventario.model.Usuario;
import com.taller.inventario.repository.UsuarioRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsuarios(UsuarioRepository usuarioRepo,
                                   PasswordEncoder passwordEncoder) {

        return args -> {

            // ===== USUARIO OFICINA =====
            if (usuarioRepo.findByNombre("oficina").isEmpty()) {

                Usuario oficina = new Usuario();

                oficina.setNombre("oficina");

                oficina.setPassword(
                        passwordEncoder.encode("1234")
                );

                oficina.setRol(Rol.OFICINA);

                usuarioRepo.save(oficina);
            }

            // ===== USUARIO MECANICO =====
            if (usuarioRepo.findByNombre("mecanico").isEmpty()) {

                Usuario mecanico = new Usuario();

                mecanico.setNombre("mecanico");

                mecanico.setPassword(
                        passwordEncoder.encode("1234")
                );

                mecanico.setRol(Rol.MECANICO);

                usuarioRepo.save(mecanico);
            }

        };
    }

}