package com.proyecto.asistencia.security;

import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
public class JwtUtil {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long expMinutes;

    public JwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.exp-min:240}") long expMinutes) {

        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).build();
        this.expMinutes = expMinutes;
    }

    public String generateToken(String username, String rol, Long idUsuario) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expMinutes * 60);

        return JWT.create()
                .withSubject(username)
                .withClaim("rol", rol)
                .withClaim("idUsuario", idUsuario)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(exp))
                .sign(algorithm);
    }

    public DecodedJWT verify(String token) {
        return verifier.verify(token);
    }

    public String getUsername(String token) {
        return verify(token).getSubject();
    }

    public String getRol(String token) {
        return verify(token).getClaim("rol").asString();
    }

    public Long getIdUsuario(String token) {
        return verify(token).getClaim("idUsuario").asLong();
    }
}
