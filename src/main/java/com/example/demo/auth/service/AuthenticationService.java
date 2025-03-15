package com.example.demo.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;


@Service
public class AuthenticationService {

    private static final long EXPIRATION_TIME = 864_000_00; // 1 dia
    private static final String PREFIX = "Bearer"; // Prefixo do token
    private final SecretKey secretKey;

    public AuthenticationService() {
        this.secretKey = JwtUtil.getSigningKey();  // A chave secreta gerada na inicialização
    }

    // Gera e adiciona o token JWT no cabeçalho da resposta
    public String generateToken(String subject, HttpServletResponse res) {
        String jwtToken = Jwts.builder()
                .subject(subject)
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();

        if (res != null) {
            res.addHeader("Authorization", PREFIX + " " + jwtToken);
            res.addHeader("Access-Control-Expose-Headers", "Authorization");
        }

        return jwtToken;
    }

    public String generateToken(String subject) {
        return generateToken(subject, null);
    }


    // Verifica e valida o token JWT
    public Authentication getAuthentication(String token) {
        if (token == null || token.trim().isEmpty()) {
            return new UsernamePasswordAuthenticationToken("", "");
        }
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)  // Define a chave para validar o JWT
                    .build().parseSignedClaims(token).getPayload();  // Obtém os dados do corpo do JWT

            String subject = claims.getSubject();  // Extrai o 'subject' do token

            if (subject != null) {
                return new UsernamePasswordAuthenticationToken(subject, null, new ArrayList<>());
            }
        } catch (Exception ignored) {
        }
        return new UsernamePasswordAuthenticationToken(null, null, new ArrayList<>());
    }

    public static class JwtUtil {
        public static SecretKey getSigningKey() {
            // Gera uma chave de 512 bits para o algoritmo HS512
            return Keys.secretKeyFor(SignatureAlgorithm.HS512);
        }
    }
}