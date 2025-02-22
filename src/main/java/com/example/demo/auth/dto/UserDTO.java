package com.example.demo.auth.dto;

import com.example.demo.auth.model.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {

    private Long id;
    private String email;

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
    }

    // Getters e setters

}
