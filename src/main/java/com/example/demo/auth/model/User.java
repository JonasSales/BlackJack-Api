package com.example.demo.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@Entity
@Table (name="tb_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank(message = "Nome não pode ser nulo")
    String name;

    @Column(unique = true)
    @NotBlank(message = "Email não pode ser nulo")
    @Email(message = "O email deve ser válido")
    String email;
    @NotBlank(message = "Uma senha precisa ser definida")
    @Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres")
    String password;

    @NotBlank
    String role;

    public User() {

    }

}

