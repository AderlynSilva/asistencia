package com.proyecto.asistencia.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.proyecto.asistencia.entity.Usuario;
import com.proyecto.asistencia.repository.UsuarioRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario u = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        boolean enabled = u.getEstado() == null || "ACTIVO".equalsIgnoreCase(u.getEstado());

        return User.builder()
                .username(u.getUsername())
                .password(u.getPasswordHash()) // BCrypt
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + u.getRol())))
                .disabled(!enabled)
                .build();
    }
}
