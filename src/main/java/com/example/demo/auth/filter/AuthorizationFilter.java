package com.example.demo.auth.filter;

import java.io.IOException;

import com.example.demo.auth.service.AuthenticationService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthorizationFilter implements Filter {

    private final AuthenticationService authenticationService;

    // Construtor para injeção do AuthenticationService
    public AuthorizationFilter(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String token = ((HttpServletRequest) servletRequest).getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7); // Extrair token JWT

            // Agora usando AuthenticationService para validar e obter o Authentication
            Authentication authentication = authenticationService.getAuthentication(jwtToken);

            if (authentication != null) {
                // Se o token for válido, configurar o contexto de autenticação
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse); // Continuar a cadeia de filtros
    }
}
