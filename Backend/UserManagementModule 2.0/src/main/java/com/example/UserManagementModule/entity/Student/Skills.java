package com.example.UserManagementModule.entity.Student;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "skills")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Skills {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    public Skills(String skill) {
        this.name = skill;
    }
}
