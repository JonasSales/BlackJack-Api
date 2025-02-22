package com.example.demo.auth.controller;

import com.example.demo.auth.dto.AuthRequest;
import com.example.demo.auth.dto.UserDTO;
import com.example.demo.auth.model.User;
import com.example.demo.auth.service.UserService;
import com.example.demo.auth.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class UserController {

	private final UserService userService;
	private final AuthenticationService authenticationService;

	@Autowired
	public UserController(UserService userService, AuthenticationService authenticationService) {
		this.userService = userService;
		this.authenticationService = authenticationService;
	}

	// Registro de novo usuário
	@PostMapping("/register")
	public ResponseEntity<UserDTO> register(@RequestBody User user) {
		UserDTO userDTO = userService.adicionar(user);

		if (userDTO != null) {
			// Retorna o UserDTO com status 201 Created
			return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	// Login de usuário
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
		String token = userService.login(authRequest, response);

		if (token != null) {
			// Retorna o token com status 200 OK
			return ResponseEntity.ok(token);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Credenciais inválidas");
		}
	}

	// Método para pegar o perfil do usuário autenticado
	@GetMapping("/profile")
	public ResponseEntity<UserDTO> getUserProfile(HttpServletRequest request) {
		// Pega o token da requisição
		String token = request.getHeader("Authorization");

		if (token == null || !token.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}

		// Remove o prefixo "Bearer " do token
		token = token.substring(7);

		// Verifica a autenticação
		Authentication authentication = authenticationService.getAuthentication(token);

		if (authentication == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}

		// Pega o nome do usuário a partir do token
		String username = authentication.getName();

		// Busca o perfil do usuário a partir do nome de usuário
		UserDTO userDTO = userService.getUser(username);

		if (userDTO != null) {
			return ResponseEntity.ok(userDTO);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}
}
