package com.studentOrbit.generate_report_app.entity.Batches;

import java.io.Serializable;
import java.util.Objects;

public class BatchId implements Serializable {

    private String batchName;
    private Integer semester;

    // Default constructor
    public BatchId() {}

    // Constructor with fields
    public BatchId(String batchName, Integer semester) {
        this.batchName = batchName;
        this.semester = semester;
    }

    // Getters, setters, equals, and hashCode
    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BatchId that = (BatchId) o;
        return Objects.equals(batchName, that.batchName) &&
                Objects.equals(semester, that.semester);
    }

    @Override
    public int hashCode() {
        return Objects.hash(batchName, semester);
    }
}
