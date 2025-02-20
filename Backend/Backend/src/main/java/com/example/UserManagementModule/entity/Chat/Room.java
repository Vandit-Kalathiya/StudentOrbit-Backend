package com.example.UserManagementModule.entity.Chat;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
//    @Id
    private String id;
    private String roomId;
    private List<Message> messages = new ArrayList<>();
}
