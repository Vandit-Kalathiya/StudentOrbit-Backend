package com.studentOrbit.generate_report_app.Model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Week {
    private int weekNumber;
    private List<Task> tasks;

    public Week(int weekNumber) {
        this.weekNumber = weekNumber;
        this.tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }
}