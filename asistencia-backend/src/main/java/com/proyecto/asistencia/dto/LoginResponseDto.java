package com.proyecto.asistencia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private String username;
    private String rol;
    private Long idUsuario;
}
