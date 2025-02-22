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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
                .csrf(AbstractHttpConfigurer::disable) // Desativa a proteção CSRF
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/auth/**").permitAll()  // Permite acesso sem autenticação
                                .anyRequest().authenticated() // Requer autenticação para qualquer outra requisição
                )
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class); // Registra o AuthorizationFilter

        return http.build(); // Necessário para construir a configuração
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Configura CORS para permitir acesso do domínio 'localhost:5500'
                registry.addMapping("/**")
                        .allowedOrigins("http://127.0.0.1:5500", "http://localhost:5500") // Permite requisições de localhost:5500
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Permite os métodos
                        .allowedHeaders("*")
                        .allowCredentials(true);  // Permite o envio de cookies/autenticação;// Permite todos os headers
            }
        };
    }
}
