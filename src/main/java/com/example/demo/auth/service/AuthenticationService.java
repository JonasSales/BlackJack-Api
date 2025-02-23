package com.example.demo.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

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
                .subject(subject) // O subject pode ser um email ou um ID de mesa
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey) // Usa a chave secreta para assinar o token
                .compact();

        // Adiciona o token JWT nos cabeçalhos da resposta
        res.addHeader("Authorization", PREFIX + " " + jwtToken);
        res.addHeader("Access-Control-Expose-Headers", "Authorization");

        return jwtToken;
    }

    public UUID validateToken(String token) {
        try {
            // Remove o prefixo "Bearer " caso esteja presente
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Decodifica e valida o token
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Extrai o subject do token (que pode ser um email ou um ID de mesa)
            return UUID.fromString(claims.getSubject());

        } catch (ExpiredJwtException e) {
            System.out.println("Erro: Token expirado!");
        } catch (SignatureException e) {
            System.out.println("Erro: Assinatura do token inválida!");
        } catch (Exception e) {
            System.out.println("Erro: Token inválido!");
        }
        return null;
    }

    // Verifica e valida o token JWT
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)  // Define a chave para validar o JWT
                .build().parseSignedClaims(token).getPayload();  // Obtém os dados do corpo do JWT

        String subject = claims.getSubject();  // Extrai o 'subject' do token

        if (subject != null) {
            return new UsernamePasswordAuthenticationToken(subject, null, new ArrayList<>());
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