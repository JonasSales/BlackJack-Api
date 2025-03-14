package com.example.demo.auth.service;

import com.example.demo.auth.exceptions.AuthExceptions;
import com.example.demo.auth.dto.AuthRequest;
import com.example.demo.auth.dto.UserDTO;
import com.example.demo.auth.model.Status;
import com.example.demo.auth.model.User;
import com.example.demo.auth.repository.StatusRepository;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.blackjack.model.Player;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class UserService {

    private final UserRepository repository;
    private final StatusRepository statusRepository;
    private final AuthenticationService authenticationService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repository, StatusRepository statusRepository, AuthenticationService authenticationService) {
        this.repository = repository;
        this.statusRepository = statusRepository;
        this.authenticationService = authenticationService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Adicionar um novo usuário
    public UserDTO adicionar(User user) {
        // Verifica se o e-mail já está cadastrado
        if (repository.findByEmail(user.getEmail()) != null) {
            throw new AuthExceptions.UserAlreadyExistsException("E-mail já cadastrado");
        }

        // Criptografa a senha antes de salvar
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Salva o usuário no banco de dados
        User savedUser = repository.save(user);

        // Retorna o UserDTO
        return new UserDTO(savedUser);
    }

    // Fazer login (autenticação) e gerar o token JWT
    public String login(AuthRequest authRequest, HttpServletResponse response) {
        // Busca o usuário pelo e-mail
        User user = repository.findByEmail(authRequest.getEmail());

        if (user == null) {
            throw new AuthExceptions.InvalidCredentialsException("E-mail não encontrado");
        }

        // Verifica se a senha está correta
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new AuthExceptions.InvalidCredentialsException("Senha incorreta");
        }

        // Gera o token JWT
        return authenticationService.generateToken(authRequest.getEmail(), response);
    }

    // Obter usuário pelo e-mail
    public UserDTO getUser(String email) {
        User user = repository.findByEmail(email);

        if (user == null) {
            throw new AuthExceptions.UserNotFoundException("Usuário não encontrado");
        }

        return new UserDTO(user);
    }

    // Obter usuário a partir do token
    public UserDTO getUserFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            throw new AuthExceptions.InvalidTokenException("Token inválido ou ausente");
        }

        // Remove o prefixo "Bearer " do token
        token = token.substring(7);

        // Verifica a autenticação
        Authentication authentication = authenticationService.getAuthentication(token);

        if (authentication == null) {
            throw new AuthExceptions.InvalidTokenException("Token inválido ou expirado");
        }

        // Obtém o e-mail do usuário a partir do token
        String username = authentication.getName();

        return getUser(username);
    }

    public void jogadorVencedor(Player jogador) {
        // Obtém o usuário a partir do token
        UserDTO userDTO = jogador.getUser();

        // Busca o usuário no banco de dados
        User user = repository.findById(userDTO.getId())
                .orElseThrow(() -> new AuthExceptions.UserNotFoundException("Usuário não encontrado"));

        Status status = user.getStatus();
        if (status == null) {
            throw new AuthExceptions.StatusNotFoundException("Status não encontrado para o usuário");
        }

        status.setMoney(userDTO.getMoney() + 200);
        status.setPartidasGanhas(userDTO.getPartidasGanhas() + 1);


        statusRepository.save(status);

        // Retorna o UserDTO atualizado
        new UserDTO(user);
    }

    public void atualizarStatus(Player jogador) {
        // Obtém o usuário a partir do token
        UserDTO userDTO = jogador.getUser();

        // Busca o usuário no banco de dados
        User user = repository.findById(userDTO.getId())
                .orElseThrow(() -> new AuthExceptions.UserNotFoundException("Usuário não encontrado"));

        // Busca o status do usuário
        Status status = user.getStatus();
        if (status == null) {
            throw new AuthExceptions.StatusNotFoundException("Status não encontrado para o usuário");
        }

        // Verifica se o usuário tem dinheiro suficiente para subtrair
        if (status.getMoney() < 100) {
            status.setMoney(0);
        }

        status.setMoney(status.getMoney() - 100);
        status.setPartidasJogadas(status.getPartidasJogadas() + 1);

        // Salva o status atualizado no banco de dados
        statusRepository.save(status);

        // Retorna o UserDTO atualizado
        new UserDTO(user);
    }
}