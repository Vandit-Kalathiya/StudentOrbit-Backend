package com.example.UserManagementModule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "black_listed_token")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlackListedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String token;

    private Date expiryDate;

}
