package com.example.UserManagementModule.entity.Token;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "jwt_token")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtToken implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String token;

    private String username;
}
