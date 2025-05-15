package com.studentOrbit.generate_report_app.Model;

import lombok.Data;

@Data
public class Criterion {
    private String abbreviation;
    private String name;

    public Criterion(String abbreviation, String name) {
        this.abbreviation = abbreviation;
        this.name = name;
    }
}