package com.studentOrbit.generate_report_app.Controller;

import com.studentOrbit.generate_report_app.Helper.MarksReportGenerateRequest;
import com.studentOrbit.generate_report_app.Helper.PdfGenerateRequest;
import com.studentOrbit.generate_report_app.Model.WeekData;
import com.studentOrbit.generate_report_app.Service.PdfService;
import com.studentOrbit.generate_report_app.Service.StudentPerformanceReport;
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

    @Autowired
    private StudentPerformanceReport studentPerformanceReport;

    @PostMapping("/create")
    public ResponseEntity<InputStreamResource> createPdf(@RequestBody PdfGenerateRequest pdfGenerateRequest, HttpServletRequest request) {
        System.out.println("Pdf : "+pdfGenerateRequest);
        List<WeekData> weekDataList = pdfService.fetchWeekData(pdfGenerateRequest, request);
        System.out.println(weekDataList);
        ByteArrayInputStream byteArrayInputStream = pdfService.createPdf(pdfGenerateRequest.getIdentifier(), weekDataList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=StudentOrbit.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(byteArrayInputStream));
    }

    @PostMapping("/marks")
    public ResponseEntity<InputStreamResource> createMarks(@RequestBody MarksReportGenerateRequest marksRepostGenerateRequest, HttpServletRequest request) {
        System.out.println(marksRepostGenerateRequest);

//        StudentPerformanceReport studentPerformanceReport = new StudentPerformanceReport();
        ByteArrayInputStream byteArrayInputStream = studentPerformanceReport.main(marksRepostGenerateRequest, request);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=StudentOrbit.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(byteArrayInputStream));
    }
}
