package com.taller.inventario.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.taller.inventario.model.Usuario;
import com.taller.inventario.repository.UsuarioRepository;

@Service
public class UserConfig implements UserDetailsService {

    private final UsuarioRepository usuarioRepo;

    public UserConfig(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario user = usuarioRepo.findAll()
                .stream()
                .filter(u -> u.getNombre().equals(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.builder()
                .username(user.getNombre())
                .password(user.getPassword())
                .roles(user.getRol().name()) // OFICINA / MECANICO
                .build();
    }
}