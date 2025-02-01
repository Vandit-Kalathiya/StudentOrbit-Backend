package com.studentOrbit.generate_report_app.entity.Batches;

import com.studentOrbit.generate_report_app.entity.Batches.CustomAnnotations.ValidSemester;
import com.studentOrbit.generate_report_app.entity.Batches.CustomAnnotations.ValidYear;
import com.studentOrbit.generate_report_app.entity.Faculty.Faculty;
import com.studentOrbit.generate_report_app.entity.Groups.Group;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "batches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Batch implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String batchName;

    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 4, message = "Year must be at most 4")
    @ValidYear(message = "Year is invalid")
    private Integer year;

    @Min(value = 1, message = "Semester must be at least 1")
    @Max(value = 8, message = "Semester is invalid")
    @ValidSemester(message = "Semester is invalid")
    private Integer semester;

    @Pattern(regexp = "^\\d{2}CE(0[1-9]|[1-9][0-9]|1[0-8][0-9]|190)$", message = "Invalid start Id.")
    private String startId;

    @Pattern(regexp = "^\\d{2}CE(0[1-9]|[1-9][0-9]|1[0-8][0-9]|190)$", message = "Invalid end Id.")
    private String endId;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude
    private List<Group> groups = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private String academicYear = LocalDateTime.now().getYear() + "";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id")
    @ToString.Exclude
    private Faculty assignedFaculty;

    public void addGroup(Group group) {
        groups.add(group);
        group.setBatch(this);
    }

    public void removeGroup(Group group) {
        groups.remove(group);
        group.setBatch(null);
    }
}