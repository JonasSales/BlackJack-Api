package com.example.demo.blackjack.api.controller;


import com.example.demo.auth.dto.UserDTO;
import com.example.demo.auth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/blackjack")
public class StatusController {

    private final UserService userService;


    public StatusController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/status")
    public ResponseEntity<List<UserDTO>> getStatus() {
        return userService.maioresGanhadores();
    }

    @GetMapping("/money")
    public ResponseEntity<List<UserDTO>> getMoney() {
        return userService.maioresMagntas();
    }
}
