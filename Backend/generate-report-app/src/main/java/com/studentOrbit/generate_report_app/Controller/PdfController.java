package com.studentOrbit.generate_report_app.Controller;

import com.studentOrbit.generate_report_app.Service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/pdf")
@CrossOrigin(origins = "http://localhost:5173", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowedHeaders = "*")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @GetMapping("/create/{username}")
    public ResponseEntity<InputStreamResource> createPdf(@PathVariable String username) {
        List<PdfService.TaskData> week1Tasks = List.of(
                new PdfService.TaskData(
                        "Implementation of user authentication",
                        "John Doe",
                        new Date(),
                        "In Progress"
                ),
                new PdfService.TaskData(
                        "Database schema design",
                        "John Doe",
                        new Date(),
                        "Completed"
                )
        );

        List<PdfService.WeekData> weekDataList = List.of(
                new PdfService.WeekData(1, week1Tasks)
        );

        ByteArrayInputStream byteArrayInputStream = pdfService.createPdf("21CE121", weekDataList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=StudentOrbit.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(byteArrayInputStream));
    }
}
