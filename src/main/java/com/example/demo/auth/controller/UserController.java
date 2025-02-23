package com.example.demo.auth.controller;

import com.example.demo.auth.dto.AuthRequest;
import com.example.demo.auth.dto.UserDTO;
import com.example.demo.auth.model.User;
import com.example.demo.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.demo.auth.exceptions.AuthExceptions;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/auth")
public class UserController {

	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	// Registro de novo usuário
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody User user) {
		try {
			UserDTO userDTO = userService.adicionar(user);
			return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
		} catch (AuthExceptions.UserAlreadyExistsException e) {
			// Retorna um JSON com a mensagem de erro
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		}
	}

	// Login de usuário
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
		try {
			String token = userService.login(authRequest, response);
			return ResponseEntity.ok(token); // Retorna o token em caso de sucesso
		} catch (AuthExceptions.InvalidCredentialsException e) {
			// Retorna um JSON com a mensagem de erro
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
		}
	}



	// Método para pegar o perfil do usuário autenticado
	@GetMapping("/profile")
	public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
		try {
			UserDTO userDTO = userService.getUserFromToken(request);
			return ResponseEntity.ok(userDTO);
		} catch (AuthExceptions.InvalidTokenException | AuthExceptions.UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}
	}
}
