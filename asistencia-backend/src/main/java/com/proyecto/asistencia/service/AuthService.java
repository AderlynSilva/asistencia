package com.proyecto.asistencia.service;

import com.proyecto.asistencia.dto.LoginRequestDto;
import com.proyecto.asistencia.dto.LoginResponseDto;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto request);
}
