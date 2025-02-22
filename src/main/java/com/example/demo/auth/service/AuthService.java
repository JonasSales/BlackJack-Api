package com.example.demo.auth.service;

import com.example.demo.auth.dto.AuthRequest;
import com.example.demo.auth.dto.UserDTO;
import com.example.demo.auth.model.User;
import com.example.demo.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO login(AuthRequest authRequest) {
        Optional<User> user = userRepository.findByEmailAndPassword(authRequest.getEmail(), authRequest.getPassword());
        return user.map(UserDTO::new).orElse(null);
    }
}
