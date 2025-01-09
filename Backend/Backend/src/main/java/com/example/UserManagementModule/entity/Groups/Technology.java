package com.example.UserManagementModule.entity.Groups;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "technology")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Technology {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

//    List<Group> groups;
}
