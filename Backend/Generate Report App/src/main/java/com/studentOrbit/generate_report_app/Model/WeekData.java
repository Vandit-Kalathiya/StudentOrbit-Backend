package com.studentOrbit.generate_report_app.Model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class WeekData {
    private int weekNumber;
    private List<TaskData> tasks;
    private LocalDate startDate;
    private LocalDate endDate;

    public WeekData(int weekNumber, List<TaskData> tasks) {
        this.weekNumber = weekNumber;
        this.tasks = tasks;
    }
}
