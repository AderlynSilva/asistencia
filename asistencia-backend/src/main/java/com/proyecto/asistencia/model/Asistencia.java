package com.proyecto.asistencia.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ASISTENCIA")
@Getter
@Setter
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_asistencia")
    @SequenceGenerator(
            name = "seq_asistencia",
            sequenceName = "SEQ_ASISTENCIA",
            allocationSize = 1
    )
    @Column(name = "ID_ASISTENCIA")
    private Long idAsistencia;

    @Column(name = "ID_USUARIO", nullable = false)
    private Long idUsuario;

    @Column(name = "FECHA", nullable = false)
    private LocalDate fecha;

    @Column(name = "HORA_CHECKIN")
    private LocalDateTime horaCheckin;

    @Column(name = "HORA_CHECKOUT")
    private LocalDateTime horaCheckout;

    @Column(name = "ESTADO")
    private String estado;

    @Column(name = "OBSERVACION")
    private String observacion;

    @Column(name = "FEC_CREACION")
    private LocalDateTime fecCreacion;
}
