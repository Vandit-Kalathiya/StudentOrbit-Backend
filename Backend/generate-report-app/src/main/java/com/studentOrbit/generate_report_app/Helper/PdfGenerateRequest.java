package com.studentOrbit.generate_report_app.Helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdfGenerateRequest {
    private String reportType;
    private String projectName;
    private List<Integer> weeks;
    private String identifier;
}
