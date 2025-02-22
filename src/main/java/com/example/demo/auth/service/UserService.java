package com.example.demo.auth.service;

import com.example.demo.auth.dto.AuthRequest;
import com.example.demo.auth.dto.UserDTO;
import com.example.demo.auth.model.User;
import com.example.demo.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;
    private final AuthenticationService authenticationService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repository, AuthenticationService authenticationService) {
        this.repository = repository;
        this.authenticationService = authenticationService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Adicionar um novo usuário
    public UserDTO adicionar(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return new UserDTO(repository.save(user));
    }

    // Listar todos os usuários
    public List<UserDTO> listarTodos() {
        List<UserDTO> list = new ArrayList<>();
        for (User user : repository.findAll()) {
            list.add(new UserDTO(user));
        }
        return list;
    }

    // Fazer login (autenticação) e gerar o token JWT
    public String login(AuthRequest authRequest, HttpServletResponse response) {
        Optional<User> user = Optional.ofNullable(repository.findByEmail(authRequest.getEmail()));

        if (user.isPresent() && passwordEncoder.matches(authRequest.getPassword(), user.get().getPassword())) {
            // Usar a injeção do AuthenticationService
            return authenticationService.addToken(authRequest.getEmail(), response);
        }
        return null; // Retorna null se as credenciais estiverem incorretas
    }

    public UserDTO getUser(String email) {
        User user = repository.findByEmail(email);
        if (user != null) {
            return new UserDTO(user);
        }
        return null;
    }
}
