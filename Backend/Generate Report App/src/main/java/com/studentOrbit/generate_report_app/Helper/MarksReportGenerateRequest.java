package com.studentOrbit.generate_report_app.Helper;

import lombok.Data;

@Data
public class MarksReportGenerateRequest {
    private String reportType;
    private String name;
    private String projectId;
}
