package com.example.demo.auth.service;

import com.example.demo.auth.exceptions.AuthExceptions;
import com.example.demo.auth.dto.AuthRequest;
import com.example.demo.auth.dto.UserDTO;
import com.example.demo.auth.model.Status;
import com.example.demo.auth.model.User;
import com.example.demo.auth.repository.StatusRepository;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.blackjack.model.Player;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;


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
    public ResponseEntity<Object> adicionar(User user) {
        // Verifica se o e-mail já está cadastrado
        if (repository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Usuário já cadastrado");
        }
        // Criptografa a senha antes de salvar
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Salva o usuário no banco de dados
        User savedUser = repository.save(user);
        // Retorna o UserDTO
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserDTO(savedUser));
    }

    // Fazer login (autenticação) e gerar o token JWT
    public ResponseEntity<String> login(AuthRequest authRequest, HttpServletResponse response) {
        // Busca o usuário pelo e-mail
        User user = repository.findByEmail(authRequest.getEmail());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Verifica se a senha está correta
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            // Retorna 401 Unauthorized se a senha estiver incorreta
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        // Retorna 200 OK com o token no corpo da resposta
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.generateToken(authRequest.getEmail(), response));
    }

    // Obter usuário pelo e-mail
    public ResponseEntity<UserDTO> getUser(String email) {
        User user = repository.findByEmail(email);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
    }

    // Obter usuário a partir do token
    public ResponseEntity<UserDTO> getUserFromToken(HttpServletRequest request) {
        String token = extractToken(request);

        if (!StringUtils.hasText(token)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            Authentication authentication = authenticationService.getAuthentication(token);
            if (authentication != null) {
                return getUser(authentication.getName());
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (SignatureException | ExpiredJwtException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
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

        new UserDTO(user);
    }

    public void atualizarStatus(Player jogador) {


        // Busca o usuário no banco de dados
        User user = repository.findById(jogador.getUser().getId())
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

    public ResponseEntity<List<UserDTO>> getAllUsers(){
        return new ResponseEntity<>(repository.findAll().stream().map(UserDTO::new).collect(Collectors.toList()), HttpStatus.OK);
    }
}