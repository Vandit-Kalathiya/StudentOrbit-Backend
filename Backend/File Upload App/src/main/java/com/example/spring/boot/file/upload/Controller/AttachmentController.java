package com.example.spring.boot.file.upload.Controller;

import com.example.spring.boot.file.upload.DTO.ResponseData;
import com.example.spring.boot.file.upload.Entity.Attachment;
import com.example.spring.boot.file.upload.Repository.AttachmentRepository;
import com.example.spring.boot.file.upload.Service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowedHeaders = "*")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private AttachmentRepository attachmentRepository;

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/upload/{taskId}")
    public ResponseData uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String taskId, @RequestParam("reviewLink") String reviewLink, @RequestParam("taskDescription") String taskDescription) throws Exception {
        Attachment attachment = null;
//        System.out.println(taskId);
        String downloadURl = "";

        attachment = attachmentService.saveAttachment(file, taskId);
        downloadURl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(attachment.getId())
                .toUriString();
        attachment.setTaskReviewLink(reviewLink);
        attachment.setTaskDescription(taskDescription);
        attachment.setDownloadUrl(downloadURl);


        attachmentRepository.save(attachment);

        return new ResponseData(attachment.getFileName(),
                downloadURl,
                file.getContentType(),
                file.getSize(),
                taskId);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) throws Exception {
        Attachment attachment = null;
        attachment = attachmentService.getAttachment(fileId);
        return  ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getFileName()
                                + "\"")
                .body(new ByteArrayResource(attachment.getData()));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<List<Attachment>> getAllAttachmentsOfTask(@PathVariable String taskId) {
        List<Attachment> attachments = null;
        attachments = attachmentService.getAllAttechmentsOfTask(taskId);
        return ResponseEntity.ok(attachments);
    }
}

