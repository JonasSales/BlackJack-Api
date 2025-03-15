package com.example.demo.auth.controller;

import com.example.demo.auth.dto.AuthRequest;
import com.example.demo.auth.dto.UserDTO;
import com.example.demo.auth.model.User;
import com.example.demo.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
		return userService.adicionar(user);
	}

	// Login de usuário
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
		return userService.login(authRequest, response);
	}

	// Método para pegar o perfil do usuário autenticado
	@GetMapping("/profile")
	public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
			return userService.getUserFromToken(request);
	}

	@GetMapping("/users")
	public ResponseEntity<List<UserDTO>> getAllUsers() {
			return userService.getAllUsers();
	}
}
