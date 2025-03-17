package com.example.demo.auth.controller;

import com.example.demo.auth.dto.AuthRequest;
import com.example.demo.auth.dto.UserDTO;
import com.example.demo.auth.model.User;
import com.example.demo.auth.service.UserReportService;
import com.example.demo.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@RestController
@RequestMapping("/auth")
public class UserController {

	private final UserService userService;
	private final UserReportService userReportService;

	@Autowired
	public UserController(UserService userService, UserReportService userReportService) {
		this.userService = userService;
        this.userReportService = userReportService;
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
	public ResponseEntity<UserDTO> getUserProfile(HttpServletRequest request) {
			return userService.getUserFromToken(request);
	}

	@GetMapping("/users")
	public ResponseEntity<List<UserDTO>> getAllUsers() {
			return userService.getAllUsers();
	}


	@GetMapping("/download")
	public ResponseEntity<?> getUserStatus(HttpServletRequest request) throws IOException {
		// Obtém o usuário a partir do token
		UserDTO user = userService.getUserFromToken(request).getBody();

		// Verifica se o usuário existe
		if (user == null) {
			return ResponseEntity.notFound().build();
		}

		// Gera o relatório
		File reportFile = userReportService.generateUserReport(user, System.getProperty("java.io.tmpdir"));

		// Converte o arquivo para um Resource
		Path path = Paths.get(reportFile.getAbsolutePath());
		Resource resource = new UrlResource(path.toUri());

		// Verifica se o arquivo existe
		if (!resource.exists() || !resource.isReadable()) {
			return ResponseEntity.internalServerError().body("Erro ao gerar o relatório.");
		}

		// Configura os headers para o download
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + reportFile.getName());
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);

		// Exclui o arquivo após o download
		reportFile.deleteOnExit();

		// Retorna o arquivo como resposta
		return ResponseEntity.ok()
				.headers(headers)
				.contentLength(reportFile.length())
				.body(resource);
	}
}
