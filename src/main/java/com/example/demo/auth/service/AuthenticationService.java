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
    public String addToken(String email, HttpServletResponse res) {
        String jwtToken = Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey) // Usa a chave secreta gerada de forma segura
                .compact();

        // Adiciona o token JWT nos cabeçalhos da resposta
        res.addHeader("Authorization", PREFIX + " " + jwtToken);
        res.addHeader("Access-Control-Expose-Headers", "Authorization");

        return jwtToken;
    }

    // Verifica e valida o token JWT
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)  // Define a chave para validar o JWT
                .build()
                .parseClaimsJws(token)  // Parseia o token
                .getBody();  // Obtém os dados do corpo do JWT

        String email = claims.getSubject();  // Extrai o 'email' do token

        if (email != null) {
            return new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
        }
        return null;
    }

    public static class JwtUtil {
        public static SecretKey getSigningKey() {
            // Gera uma chave de 512 bits para o algoritmo HS512
            return Keys.secretKeyFor(SignatureAlgorithm.HS512);
        }
    }
}
