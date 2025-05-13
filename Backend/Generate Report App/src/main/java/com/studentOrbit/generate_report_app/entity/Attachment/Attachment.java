package com.studentOrbit.generate_report_app.entity.Attachment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    private String fileName;
    private String fileType;
    private Long size;
    private String taskId;
    private String downloadUrl;
    private String taskReviewLink;
    private String taskDescription;

    @Lob
    private byte[] data;

    public Attachment(Long size,String fileName, String fileType, byte[] data, String taskId, LocalDate createDate, LocalTime createTime) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
        this.taskId = taskId;
        this.createDate = createDate;
        this.createTime = createTime;
        this.size = size;
//        this.downloadUrl = downloadUrl;
    }

    private LocalDate createDate;

    private LocalTime createTime;
}


