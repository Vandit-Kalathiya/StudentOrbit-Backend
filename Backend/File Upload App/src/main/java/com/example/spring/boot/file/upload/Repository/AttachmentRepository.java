package com.example.spring.boot.file.upload.Repository;

import com.example.spring.boot.file.upload.Entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, String> {

    public List<Attachment> findByTaskId(String taskId);
}

