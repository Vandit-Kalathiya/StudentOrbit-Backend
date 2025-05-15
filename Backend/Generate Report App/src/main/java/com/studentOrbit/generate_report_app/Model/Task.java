package com.studentOrbit.generate_report_app.Model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Task {
    private String name;
    private String description;
    private int score;
    private int pointsEarned;
    private int totalPoints;
    private Map<String, Integer> ratings;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.ratings = new HashMap<>();
    }
}