package com.example.demo.auth.repository;

import com.example.demo.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {


    User findByEmail(String email);
    Optional<User> findByEmailAndPassword(String email, String password);
    List<User> findAll();
}
