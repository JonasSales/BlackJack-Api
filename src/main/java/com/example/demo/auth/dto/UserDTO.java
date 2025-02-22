package com.example.demo.auth.dto;

import com.example.demo.auth.model.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {

    private Long id;
    private String username;
    private String email;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getName();
        this.email = user.getEmail();
    }

    public String getName() {
        return username;
    }

    // Getters e setters

}
