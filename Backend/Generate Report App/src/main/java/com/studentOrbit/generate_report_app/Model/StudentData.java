package com.studentOrbit.generate_report_app.Model;

import lombok.Data;

import java.util.List;

@Data
public class StudentData {
    private String studentName;
    private String studentId;
    private String projectName;
    private int overallPercentage;
    private List<Criterion> criteria;
    private List<Week> weeks;
    private int totalMarksEarned;
    private int totalPossibleMarks;
}