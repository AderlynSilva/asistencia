package com.proyecto.asistencia.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ProbarBCrypt {
    public static void main(String[] args) {
        String raw = "admin123";
        String hash = "$2a$10$ILi1J/Tf3HsWKxKyrqqZ8.Xbsey5VexCG2tjji.zx0.uggyP4lRFq";

        System.out.println(new BCryptPasswordEncoder().matches(raw, hash));
    }
}
