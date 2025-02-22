package com.example.demo.auth.controller;

import com.example.demo.auth.dto.AuthRequest;
import com.example.demo.auth.dto.UserDTO;
import com.example.demo.auth.model.User;
import com.example.demo.auth.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;




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
	public UserDTO register(@RequestBody User user) {
		return userService.adicionar(user);
	}

	// Login de usuário
	@PostMapping("/login")
	public String login(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
		String token = userService.login(authRequest, response);
		if (token != null) {
			return token; // Retorna o JWT
		} else {
			return "Credenciais inválidas";
		}
	}
}
