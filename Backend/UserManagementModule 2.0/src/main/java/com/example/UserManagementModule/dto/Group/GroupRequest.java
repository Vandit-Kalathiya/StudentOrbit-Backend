package com.example.UserManagementModule.dto.Group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupRequest {

    private String groupName;
    private String description;
    private Set<String> students;
    private Set<String> technologies;
    private String batchName;
    private String groupLeaderId;
}
