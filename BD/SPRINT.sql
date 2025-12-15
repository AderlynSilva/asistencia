
-----------------------------------------------------------CREACCIÓN DE TABLAS-----------------------------------------------------------
-------------------------------------------------------------------TABLA USUARIO
SELECT * FROM asistencia;


CREATE TABLE usuario (
   --
   id_usuario       NUMBER(10)        NOT NULL,
   nombres          VARCHAR2(100)     NOT NULL,
   apellidos        VARCHAR2(100)     NOT NULL,
   username         VARCHAR2(50)      NOT NULL,
   email            VARCHAR2(150)     NOT NULL,
   password_hash    VARCHAR2(200)     NOT NULL,
   rol              VARCHAR2(20)      NOT NULL,         -- ADMIN / EMPLEADO
   estado           VARCHAR2(10)      DEFAULT 'ACTIVO', -- ACTIVO / INACTIVO
   fec_creacion     DATE              DEFAULT SYSDATE,
   --
   CONSTRAINT pk_usuario PRIMARY KEY (
      id_usuario
   ),
   CONSTRAINT uq_usuario_username UNIQUE (
      username
   ),
   CONSTRAINT uq_usuario_email UNIQUE (
      email
   ),
   CONSTRAINT chk_usuario_rol CHECK (
      rol IN ('ADMIN','EMPLEADO')
   ),
   CONSTRAINT chk_usuario_estado CHECK (
      estado IN ('ACTIVO','INACTIVO')
   )
);


----------------------------------------------------------------TABLA ASISTENCIA
CREATE TABLE asistencia (
   --
   id_asistencia     NUMBER(10)       NOT NULL,
   id_usuario        NUMBER(10)       NOT NULL,
   fecha             DATE             NOT NULL,
   hora_checkin      DATE             NULL,
   hora_checkout     DATE             NULL,
   estado            VARCHAR2(20)     NULL,        -- PRESENTE / TARDANZA / FALTA
   observacion       VARCHAR2(255)    NULL,
   fec_creacion      DATE             DEFAULT SYSDATE,
   --
   CONSTRAINT pk_asistencia PRIMARY KEY (
      id_asistencia
   ),
   CONSTRAINT fk_asistencia_usuario FOREIGN KEY (
      id_usuario
   ) REFERENCES usuario (
      id_usuario
   )
);
-------------------------------------------------------------TABLA JUSTIFICACION

CREATE TABLE justificacion (
   --
   id_justificacion  NUMBER(10)       NOT NULL,
   id_usuario        NUMBER(10)       NOT NULL,
   id_asistencia     NUMBER(10)       NULL,
   tipo              VARCHAR2(20)     NOT NULL,         -- TARDANZA / FALTA
   motivo            VARCHAR2(500)    NOT NULL,
   estado            VARCHAR2(20)     DEFAULT 'PENDIENTE',  
   fec_solicitud     DATE             DEFAULT SYSDATE,
   fec_revision      DATE             NULL,
   usr_revisa        VARCHAR2(50)     NULL,
   --
   CONSTRAINT pk_justificacion PRIMARY KEY (
      id_justificacion
   ),
   CONSTRAINT fk_just_usuario FOREIGN KEY (
      id_usuario
   ) REFERENCES usuario (
      id_usuario
   ),
   CONSTRAINT fk_just_asistencia FOREIGN KEY (
      id_asistencia
   ) REFERENCES asistencia (
      id_asistencia
   ),
   CONSTRAINT chk_just_tipo CHECK (
      tipo IN ('TARDANZA','FALTA')
   ),
   CONSTRAINT chk_just_estado CHECK (
      estado IN ('PENDIENTE','APROBADA','RECHAZADA')
   )
);


------------------------------------------------------------TABLA LOG_ASISTENCIA

CREATE TABLE log_asistencia (
   --
   id_log           NUMBER(10)        NOT NULL,
   id_asistencia    NUMBER(10)        NULL,
   id_usuario       NUMBER(10)        NULL,
   accion           VARCHAR2(30)      NOT NULL,   -- CHECKIN / CHECKOUT / UPDATE
   detalle          VARCHAR2(500)     NULL,
   fec_evento       DATE              DEFAULT SYSDATE,
   --
   CONSTRAINT pk_log_asistencia PRIMARY KEY (
      id_log
   )
);

---------------------------------------------------------TABLA LOG_JUSTIFICACION
CREATE TABLE log_justificacion (
   --
   id_log              NUMBER(10)      NOT NULL,
   id_justificacion    NUMBER(10)      NULL,
   id_usuario          NUMBER(10)      NULL,
   accion              VARCHAR2(30)    NOT NULL,  -- CREACION / APROBACION / RECHAZO
   detalle             VARCHAR2(500)   NULL,
   fec_evento          DATE            DEFAULT SYSDATE,
   --
   CONSTRAINT pk_log_justificacion PRIMARY KEY (
      id_log
   )
);


-----------------------------------------------------------INDICES-----------------------------------------------------------
CREATE INDEX idx_asistencia_fecha 
   ON asistencia (fecha);

CREATE INDEX idx_asistencia_usuario
   ON asistencia (id_usuario);

CREATE INDEX idx_justificacion_usuario
   ON justificacion (id_usuario);



-----------------------------------------------------------VISTA: JUSTIFICACIONES PENDIENTES-----------------------------------------------------------
CREATE OR REPLACE VIEW vw_justificaciones_pendientes AS
--
SELECT
   --
   j.id_justificacion,
   j.id_usuario,
   u.nombres,
   u.apellidos,
   j.tipo,
   j.motivo,
   j.estado,
   j.fec_solicitud
   --
FROM justificacion j
JOIN usuario u 
   ON u.id_usuario = j.id_usuario
WHERE j.estado = 'PENDIENTE';


-----------------------------------------------------------SECUENCIAS-----------------------------------------------------------
------------------------------------USUARIO
CREATE SEQUENCE seq_usuario
   START WITH 1
   INCREMENT BY 1
   NOCACHE;

------------------------------------ASISTENCIA
CREATE SEQUENCE seq_asistencia
   START WITH 1
   INCREMENT BY 1
   NOCACHE;

------------------------------------JUSTIFICACIÓN
CREATE SEQUENCE seq_justificacion
   START WITH 1
   INCREMENT BY 1
   NOCACHE;

------------------------------------LOG_ASISTENCIA
CREATE SEQUENCE seq_log_asistencia
   START WITH 1
   INCREMENT BY 1
   NOCACHE;

------------------------------------LOG_JUSTIFICACION
CREATE SEQUENCE seq_log_justificacion
   START WITH 1
   INCREMENT BY 1
   NOCACHE;

-----------------------------------------------------------PAQUETES-----------------------------------------------------------
------------------------------------PAQUETE: PKG_USUARIO

CREATE OR REPLACE PACKAGE pkg_usuario AS
   --
   -- Retorna 1 si el usuario está ACTIVO, 0 si está INACTIVO o no existe
   FUNCTION fn_usuario_activo (
      p_id_usuario   IN usuario.id_usuario%TYPE
   ) RETURN NUMBER;
   --
END pkg_usuario;
/

CREATE OR REPLACE PACKAGE BODY pkg_usuario AS

   FUNCTION fn_usuario_activo (
      p_id_usuario   IN usuario.id_usuario%TYPE
   ) RETURN NUMBER IS
      v_estado   usuario.estado%TYPE;
   BEGIN
      SELECT estado
        INTO v_estado
        FROM usuario
       WHERE id_usuario = p_id_usuario;

      IF v_estado = 'ACTIVO' THEN
         RETURN 1;
      ELSE
         RETURN 0;
      END IF;
   EXCEPTION
      WHEN NO_DATA_FOUND THEN
         RETURN 0;
   END fn_usuario_activo;

END pkg_usuario;
/

------------------------------------PAQUETE: PKG_ASISTENCIA

CREATE OR REPLACE PACKAGE pkg_asistencia AS
   --
   -- 1) Verificar si el usuario tiene un check-in abierto en la fecha
   FUNCTION fn_tiene_checkin_abierto (
      p_id_usuario   IN asistencia.id_usuario%TYPE,
      p_fecha        IN DATE
   ) RETURN NUMBER;
   --
   -- 2) Registrar CHECK-IN
   PROCEDURE pr_registrar_checkin (
      p_id_usuario      IN  asistencia.id_usuario%TYPE,
      p_tolerancia_min  IN  NUMBER DEFAULT 10,
      p_cod_result      OUT NUMBER,
      p_msg_result      OUT VARCHAR2
   );
   --
   -- 3) Registrar CHECK-OUT
   PROCEDURE pr_registrar_checkout (
      p_id_usuario   IN  asistencia.id_usuario%TYPE,
      p_cod_result   OUT NUMBER,
      p_msg_result   OUT VARCHAR2
   );
   --
END pkg_asistencia;
/



CREATE OR REPLACE PACKAGE BODY pkg_asistencia AS

   --------------------------------------------------------------------
   -- 1) Verificar si ya tiene un check-in abierto
   --------------------------------------------------------------------
   FUNCTION fn_tiene_checkin_abierto (
      p_id_usuario   IN asistencia.id_usuario%TYPE,
      p_fecha        IN DATE
   ) RETURN NUMBER IS
      v_count   NUMBER;
   BEGIN
      SELECT COUNT(*)
        INTO v_count
        FROM asistencia
       WHERE id_usuario   = p_id_usuario
         AND fecha        = TRUNC(p_fecha)
         AND hora_checkin IS NOT NULL
         AND hora_checkout IS NULL;

      IF v_count > 0 THEN
         RETURN 1;
      ELSE
         RETURN 0;
      END IF;
   END fn_tiene_checkin_abierto;
   --
   --------------------------------------------------------------------
   -- 2) Registrar CHECK-IN
   --------------------------------------------------------------------
   PROCEDURE pr_registrar_checkin (
      p_id_usuario      IN  asistencia.id_usuario%TYPE,
      p_tolerancia_min  IN  NUMBER,
      p_cod_result      OUT NUMBER,
      p_msg_result      OUT VARCHAR2
   ) IS
      v_activo           NUMBER;
      v_tiene_checkin    NUMBER;
      v_hora_actual      DATE := SYSDATE;
      v_hora_esperada    DATE;
      v_min_tarde        NUMBER;
      v_estado_asist     asistencia.estado%TYPE;
   BEGIN
      p_cod_result := 0;
      p_msg_result := NULL;

      SAVEPOINT sp_checkin;

      -- Validar que el usuario esté ACTIVO
      v_activo := pkg_usuario.fn_usuario_activo(p_id_usuario);

      IF v_activo = 0 THEN
         p_cod_result := 1;
         p_msg_result := 'Usuario inactivo o no existe';
         ROLLBACK TO sp_checkin;
         RETURN;
      END IF;

      -- Validar que NO tenga check-in abierto hoy (no doble asistencia)
      v_tiene_checkin := fn_tiene_checkin_abierto(p_id_usuario, v_hora_actual);

      IF v_tiene_checkin = 1 THEN
         p_cod_result := 2;
         p_msg_result := 'Ya existe un check-in abierto para hoy';
         ROLLBACK TO sp_checkin;
         RETURN;
      END IF;

      -- Calcular PRESENTE / TARDANZA según tolerancia (hora base 09:00)
      v_hora_esperada := TRUNC(v_hora_actual) + TO_DSINTERVAL('PT9H'); -- 09:00
      v_min_tarde := (v_hora_actual - v_hora_esperada) * 24 * 60;

      IF v_min_tarde <= p_tolerancia_min THEN
         v_estado_asist := 'PRESENTE';
      ELSE
         v_estado_asist := 'TARDANZA';
      END IF;

      -- Insertar registro de asistencia
      INSERT INTO asistencia (
         id_asistencia,
         id_usuario,
         fecha,
         hora_checkin,
         hora_checkout,
         estado,
         observacion,
         fec_creacion
      ) VALUES (
         seq_asistencia.NEXTVAL,
         p_id_usuario,
         TRUNC(v_hora_actual),
         v_hora_actual,
         NULL,
         v_estado_asist,
         NULL,
         SYSDATE
      );

      COMMIT;

      p_cod_result := 0;
      p_msg_result := 'Check-in registrado correctamente';
   EXCEPTION
      WHEN OTHERS THEN
         ROLLBACK TO sp_checkin;
         p_cod_result := -1;
         p_msg_result := 'Error en pr_registrar_checkin: ' || SQLERRM;
   END pr_registrar_checkin;
   --
   --------------------------------------------------------------------
   -- 3) Registrar CHECK-OUT
   --------------------------------------------------------------------
   PROCEDURE pr_registrar_checkout (
      p_id_usuario   IN  asistencia.id_usuario%TYPE,
      p_cod_result   OUT NUMBER,
      p_msg_result   OUT VARCHAR2
   ) IS
      v_id_asistencia   asistencia.id_asistencia%TYPE;
   BEGIN
      p_cod_result := 0;
      p_msg_result := NULL;

      SAVEPOINT sp_checkout;

      -- Buscar asistencia abierta hoy
      SELECT id_asistencia
        INTO v_id_asistencia
        FROM asistencia
       WHERE id_usuario   = p_id_usuario
         AND fecha        = TRUNC(SYSDATE)
         AND hora_checkin IS NOT NULL
         AND hora_checkout IS NULL;

      -- Actualizar hora de salida
      UPDATE asistencia
         SET hora_checkout = SYSDATE
       WHERE id_asistencia = v_id_asistencia;

      COMMIT;

      p_cod_result := 0;
      p_msg_result := 'Check-out registrado correctamente';
   EXCEPTION
      WHEN NO_DATA_FOUND THEN
         ROLLBACK TO sp_checkout;
         p_cod_result := 1;
         p_msg_result := 'No existe un check-in abierto para hoy';
      WHEN OTHERS THEN
         ROLLBACK TO sp_checkout;
         p_cod_result := -1;
         p_msg_result := 'Error en pr_registrar_checkout: ' || SQLERRM;
   END pr_registrar_checkout;

END pkg_asistencia;
/

------------------------------------PAQUETE: PKG_JUSTIFICACION

CREATE OR REPLACE PACKAGE pkg_justificacion AS
   --
   -- Registrar justificación de TARDANZA o FALTA
   PROCEDURE pr_registrar_justificacion (
      p_id_usuario     IN  justificacion.id_usuario%TYPE,
      p_id_asistencia  IN  justificacion.id_asistencia%TYPE,
      p_tipo           IN  justificacion.tipo%TYPE,     -- 'TARDANZA' o 'FALTA'
      p_motivo         IN  justificacion.motivo%TYPE,
      p_cod_result     OUT NUMBER,
      p_msg_result     OUT VARCHAR2
   );
   --
   -- Aprobar o rechazar justificación
   PROCEDURE pr_aprobar_justificacion (
      p_id_justificacion  IN  justificacion.id_justificacion%TYPE,
      p_nuevo_estado      IN  justificacion.estado%TYPE, -- 'APROBADA' / 'RECHAZADA'
      p_usr_revisa        IN  VARCHAR2,
      p_cod_result        OUT NUMBER,
      p_msg_result        OUT VARCHAR2
   );
   --
END pkg_justificacion;
/


CREATE OR REPLACE PACKAGE BODY pkg_justificacion AS

   --------------------------------------------------------------------
   -- Registrar JUSTIFICACIÓN
   --------------------------------------------------------------------
   PROCEDURE pr_registrar_justificacion (
      p_id_usuario     IN  justificacion.id_usuario%TYPE,
      p_id_asistencia  IN  justificacion.id_asistencia%TYPE,
      p_tipo           IN  justificacion.tipo%TYPE,
      p_motivo         IN  justificacion.motivo%TYPE,
      p_cod_result     OUT NUMBER,
      p_msg_result     OUT VARCHAR2
   ) IS
   BEGIN
      p_cod_result := 0;
      p_msg_result := NULL;

      SAVEPOINT sp_justif;

      INSERT INTO justificacion (
         id_justificacion,
         id_usuario,
         id_asistencia,
         tipo,
         motivo,
         estado,
         fec_solicitud,
         fec_revision,
         usr_revisa
      ) VALUES (
         seq_justificacion.NEXTVAL,
         p_id_usuario,
         p_id_asistencia,
         p_tipo,
         p_motivo,
         'PENDIENTE',
         SYSDATE,
         NULL,
         NULL
      );

      COMMIT;

      p_cod_result := 0;
      p_msg_result := 'Justificación registrada correctamente';
   EXCEPTION
      WHEN OTHERS THEN
         ROLLBACK TO sp_justif;
         p_cod_result := -1;
         p_msg_result := 'Error en pr_registrar_justificacion: ' || SQLERRM;
   END pr_registrar_justificacion;
   --
   --------------------------------------------------------------------
   -- Aprobar / rechazar JUSTIFICACIÓN
   --------------------------------------------------------------------
   PROCEDURE pr_aprobar_justificacion (
      p_id_justificacion  IN  justificacion.id_justificacion%TYPE,
      p_nuevo_estado      IN  justificacion.estado%TYPE,
      p_usr_revisa        IN  VARCHAR2,
      p_cod_result        OUT NUMBER,
      p_msg_result        OUT VARCHAR2
   ) IS
   BEGIN
      p_cod_result := 0;
      p_msg_result := NULL;

      SAVEPOINT sp_apr_justif;

      UPDATE justificacion
         SET estado       = p_nuevo_estado,
             fec_revision = SYSDATE,
             usr_revisa   = p_usr_revisa
       WHERE id_justificacion = p_id_justificacion;

      COMMIT;

      p_cod_result := 0;
      p_msg_result := 'Justificación actualizada correctamente';
   EXCEPTION
      WHEN OTHERS THEN
         ROLLBACK TO sp_apr_justif;
         p_cod_result := -1;
         p_msg_result := 'Error en pr_aprobar_justificacion: ' || SQLERRM;
   END pr_aprobar_justificacion;

END pkg_justificacion;
/

------------------------------------PAQUETE: PKG_REPORTES

CREATE OR REPLACE PACKAGE pkg_reportes AS
   --
   TYPE t_cursor IS REF CURSOR;
   --
   -- Reporte de asistencias por periodo (opcional por usuario)
   PROCEDURE pr_reporte_asistencia_periodo (
      p_fecha_ini    IN DATE,
      p_fecha_fin    IN DATE,
      p_id_usuario   IN usuario.id_usuario%TYPE,
      p_cursor       OUT t_cursor
   );
   --
   -- Reporte de puntualidad por empleado
   PROCEDURE pr_reporte_puntualidad_empleado (
      p_fecha_ini    IN DATE,
      p_fecha_fin    IN DATE,
      p_cursor       OUT t_cursor
   );
   --
END pkg_reportes;
/

CREATE OR REPLACE PACKAGE BODY pkg_reportes AS

   --------------------------------------------------------------------
   -- Asistencia por periodo
   --------------------------------------------------------------------
   PROCEDURE pr_reporte_asistencia_periodo (
      p_fecha_ini    IN DATE,
      p_fecha_fin    IN DATE,
      p_id_usuario   IN usuario.id_usuario%TYPE,
      p_cursor       OUT t_cursor
   ) IS
   BEGIN
      IF p_id_usuario IS NULL THEN
         OPEN p_cursor FOR
            SELECT a.id_asistencia,
                   a.id_usuario,
                   u.nombres,
                   u.apellidos,
                   a.fecha,
                   a.hora_checkin,
                   a.hora_checkout,
                   a.estado
              FROM asistencia a
              JOIN usuario u
                ON u.id_usuario = a.id_usuario
             WHERE a.fecha BETWEEN TRUNC(p_fecha_ini) AND TRUNC(p_fecha_fin)
             ORDER BY a.fecha, a.id_usuario;
      ELSE
         OPEN p_cursor FOR
            SELECT a.id_asistencia,
                   a.id_usuario,
                   u.nombres,
                   u.apellidos,
                   a.fecha,
                   a.hora_checkin,
                   a.hora_checkout,
                   a.estado
              FROM asistencia a
              JOIN usuario u
                ON u.id_usuario = a.id_usuario
             WHERE a.fecha BETWEEN TRUNC(p_fecha_ini) AND TRUNC(p_fecha_fin)
               AND a.id_usuario = p_id_usuario
             ORDER BY a.fecha;
      END IF;
   END pr_reporte_asistencia_periodo;
   --
   --------------------------------------------------------------------
   -- Puntualidad por empleado
   --------------------------------------------------------------------
   PROCEDURE pr_reporte_puntualidad_empleado (
      p_fecha_ini    IN DATE,
      p_fecha_fin    IN DATE,
      p_cursor       OUT t_cursor
   ) IS
   BEGIN
      OPEN p_cursor FOR
         SELECT u.id_usuario,
                u.nombres,
                u.apellidos,
                COUNT(*) AS total_registros,
                SUM(CASE WHEN a.estado = 'PRESENTE' THEN 1 ELSE 0 END) AS total_puntual,
                SUM(CASE WHEN a.estado = 'TARDANZA' THEN 1 ELSE 0 END) AS total_tardanza,
                ROUND(
                   (SUM(CASE WHEN a.estado = 'PRESENTE' THEN 1 ELSE 0 END) /
                   NULLIF(COUNT(*), 0)) * 100,
                   2
                ) AS porc_puntualidad
           FROM asistencia a
           JOIN usuario u
             ON u.id_usuario = a.id_usuario
          WHERE a.fecha BETWEEN TRUNC(p_fecha_ini) AND TRUNC(p_fecha_fin)
          GROUP BY u.id_usuario, u.nombres, u.apellidos
          ORDER BY porc_puntualidad DESC;
   END pr_reporte_puntualidad_empleado;

END pkg_reportes;
/


-----------------------------------------------------------TIGGERS-----------------------------------------------------------


CREATE OR REPLACE TRIGGER trg_log_asistencia
AFTER INSERT OR UPDATE ON asistencia
FOR EACH ROW
DECLARE
   v_accion   VARCHAR2(30);
   v_detalle  VARCHAR2(500);
BEGIN
   IF INSERTING THEN
      v_accion := 'CHECKIN';
      v_detalle := 'Registro de asistencia (check-in)';
   ELSIF UPDATING THEN
      IF :OLD.hora_checkout IS NULL AND :NEW.hora_checkout IS NOT NULL THEN
         v_accion := 'CHECKOUT';
         v_detalle := 'Registro de check-out';
      ELSE
         v_accion := 'UPDATE';
         v_detalle := 'Actualización de asistencia';
      END IF;
   END IF;

   INSERT INTO log_asistencia (
      id_log,
      id_asistencia,
      id_usuario,
      accion,
      detalle,
      fec_evento
   ) VALUES (
      seq_log_asistencia.NEXTVAL,
      :NEW.id_asistencia,
      :NEW.id_usuario,
      v_accion,
      v_detalle,
      SYSDATE
   );
END;
/

-------

CREATE OR REPLACE TRIGGER trg_log_justificacion
AFTER INSERT OR UPDATE ON justificacion
FOR EACH ROW
DECLARE
   v_accion   VARCHAR2(30);
   v_detalle  VARCHAR2(500);
BEGIN
   IF INSERTING THEN
      v_accion := 'CREACION';
      v_detalle := 'Creación de justificación (' || :NEW.tipo || ')';
   ELSIF UPDATING THEN
      IF :OLD.estado <> :NEW.estado THEN
         IF :NEW.estado = 'APROBADA' THEN
            v_accion := 'APROBACION';
         ELSIF :NEW.estado = 'RECHAZADA' THEN
            v_accion := 'RECHAZO';
         ELSE
            v_accion := 'CAMBIO_ESTADO';
         END IF;
         v_detalle := 'Justificación ' || :NEW.estado || ' por ' || :NEW.usr_revisa;
      ELSE
         v_accion := 'UPDATE';
         v_detalle := 'Actualización de justificación';
      END IF;
   END IF;

   INSERT INTO log_justificacion (
      id_log,
      id_justificacion,
      id_usuario,
      accion,
      detalle,
      fec_evento
   ) VALUES (
      seq_log_justificacion.NEXTVAL,
      :NEW.id_justificacion,
      :NEW.id_usuario,
      v_accion,
      v_detalle,
      SYSDATE
   );
END;
/




----------------------------------------------------------------------------------------------------------------------



 