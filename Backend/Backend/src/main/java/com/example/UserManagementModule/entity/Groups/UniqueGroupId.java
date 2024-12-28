package com.example.UserManagementModule.entity.Groups;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "unique_group_id")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniqueGroupId {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(unique = true)
    String uniqueGroupId;

    public UniqueGroupId(String uniqueGroupId) {
        this.uniqueGroupId = uniqueGroupId;
    }
}
