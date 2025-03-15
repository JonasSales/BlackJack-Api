package com.example.demo.config;

import com.example.demo.auth.filter.AuthorizationFilter;
import com.example.demo.auth.service.AuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationService authenticationService;

    public SecurityConfig(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Criar e adicionar o AuthorizationFilter
        AuthorizationFilter authorizationFilter = new AuthorizationFilter(authenticationService);

        http
                .csrf(AbstractHttpConfigurer::disable) // Desativa CSRF (se estiver usando apenas APIs REST)
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new org.springframework.web.cors.CorsConfiguration();
                    config.setAllowedOrigins(java.util.List.of(
                            "http://localhost:3000",
                            "http://localhost:5500",
                            "http://localhost:5501",
                            "http://127.0.0.1:5501",
                            "http://127.0.0.1:5500")); // Domínios permitidos
                    config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE"));
                    config.setAllowCredentials(true); // Permitir envio de cookies
                    config.setAllowedHeaders(java.util.List.of("Authorization", "Cache-Control", "Content-Type"));
                    return config;
                }))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/auth/**").permitAll()
                                .requestMatchers("blackjack/status").permitAll()
                                .requestMatchers("blackjack/money").permitAll()
                                .requestMatchers("/blackjack/criarMesa").permitAll()
                                .requestMatchers("/blackjack/mesas").permitAll()
                                .requestMatchers("/blackjack/mesas/{mesaId}").permitAll()
                                .requestMatchers("/blackjack/mesas/{mesaId}/**").permitAll()
                                //.requestMatchers("/blackjack/mesas/").authenticated()
                                .anyRequest().authenticated() // Requer autenticação para qualquer outra requisição
                )
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class); // Registra o AuthorizationFilter

        return http.build(); // Necessário para construir a configuração
    }
}
