package com.proyecto.asistencia.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "USUARIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_usuario")
    @SequenceGenerator(
            name = "seq_usuario",
            sequenceName = "SEQ_USUARIO",
            allocationSize = 1
    )
    @Column(name = "ID_USUARIO")
    private Long idUsuario;

    @Column(name = "NOMBRES", nullable = false, length = 100)
    private String nombres;

    @Column(name = "APELLIDOS", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "USERNAME", nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "EMAIL", nullable = false, length = 150, unique = true)
    private String email;

    @Column(name = "PASSWORD_HASH", nullable = false, length = 200)
    private String passwordHash;

    @Column(name = "ROL", nullable = false, length = 20)
    private String rol; // ADMIN / EMPLEADO

    @Column(name = "ESTADO", length = 10)
    private String estado; // ACTIVO / INACTIVO

    @Column(name = "FEC_CREACION")
    private java.time.LocalDateTime fecCreacion;
}
