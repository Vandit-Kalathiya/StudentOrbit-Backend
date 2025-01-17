package com.example.spring.boot.file.upload.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "attachment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    private String fileName;
    private String fileType;
    private String taskId;

    @Lob
    private byte[] data;

    public Attachment(String fileName, String fileType, byte[] data, String taskId) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
        this.taskId = taskId;
    }

    private LocalDate createDate;

    private LocalTime createTime;
}

