package com.proyecto.asistencia.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.proyecto.asistencia.dto.LoginRequestDto;
import com.proyecto.asistencia.dto.LoginResponseDto;
import com.proyecto.asistencia.entity.Usuario;
import com.proyecto.asistencia.repository.UsuarioRepository;
import com.proyecto.asistencia.security.JwtUtil;
import com.proyecto.asistencia.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponseDto login(LoginRequestDto request) {

        // 1) autenticar (usa CustomUserDetailsService + PasswordEncoder BCrypt)
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 2) traer datos extra del usuario (rol + id)
        Usuario u = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // 3) generar token con (username, rol, idUsuario)
        String token = jwtUtil.generateToken(u.getUsername(), u.getRol(), u.getIdUsuario());

        // 4) devolver respuesta con 4 campos
        return new LoginResponseDto(token, u.getUsername(), u.getRol(), u.getIdUsuario());
    }
}
