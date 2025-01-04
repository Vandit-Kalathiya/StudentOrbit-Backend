package com.studentOrbit.generate_report_app.Controller;

import com.studentOrbit.generate_report_app.Helper.PdfGenerateRequest;
import com.studentOrbit.generate_report_app.Service.PdfService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/pdf")
@CrossOrigin(origins = "http://localhost:5173", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowedHeaders = "*")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @PostMapping("/create")
    public ResponseEntity<InputStreamResource> createPdf(@RequestBody PdfGenerateRequest pdfGenerateRequest, HttpServletRequest request) {
//        List<PdfService.TaskData> week1Tasks = List.of(
//                new PdfService.TaskData(
//                        "Implementation of user authentication",
//                        "Demo task",
//                        Arrays.asList("22CE047","22CE049"),
//                        new Date(),
//                        "In Progress"
//                ),
//                new PdfService.TaskData(
//                        "Database schema design",
//                        "Designed DB schema",
//                        List.of("22CE047"),
//                        new Date(),
//                        "Completed"
//                )
//        );
//
//        List<PdfService.TaskData> week2Tasks = List.of(
//                new PdfService.TaskData(
//                        "Implementation of user authentication",
//                        "Demo task",
//                        Arrays.asList("22CE047","22CE049"),
//                        new Date(),
//                        "In Progress"
//                ),
//                new PdfService.TaskData(
//                        "Database schema design",
//                        "Designed DB schema",
//                        List.of("22CE047"),
//                        new Date(),
//                        "Completed"
//                )
//        );
//
//        List<PdfService.TaskData> week3Tasks = List.of(
//                new PdfService.TaskData(
//                        "Implementation of user authentication",
//                        "Demo task",
//                        Arrays.asList("22CE047","22CE049"),
//                        new Date(),
//                        "In Progress"
//                ),
//                new PdfService.TaskData(
//                        "Database schema design",
//                        "Designed DB schema",
//                        List.of("22CE047"),
//                        new Date(),
//                        "Completed"
//                )
//        );

//        List<PdfService.WeekData> weekDataList = List.of(
//                new PdfService.WeekData(1, week1Tasks),
//                new PdfService.WeekData(2, week2Tasks),
//                new PdfService.WeekData(3, week3Tasks)
//        );

        System.out.println("Pdf : "+pdfGenerateRequest);
        List<PdfService.WeekData> weekDataList = pdfService.fetchWeekData(pdfGenerateRequest, request);
        System.out.println(weekDataList);
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
