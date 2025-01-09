    package com.example.UserManagementModule.entity.Batches;

    import com.example.UserManagementModule.entity.Batches.CustomAnnotations.ValidSemester;
    import com.example.UserManagementModule.entity.Batches.CustomAnnotations.ValidYear;
    import com.example.UserManagementModule.entity.Faculty.Faculty;
    import com.example.UserManagementModule.entity.Groups.Group;
    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;
    import lombok.*;
    import org.springframework.data.annotation.CreatedDate;

    import javax.validation.constraints.Max;
    import javax.validation.constraints.Min;
    import javax.validation.constraints.Pattern;
    import java.io.Serializable;
    import java.time.LocalDateTime;
    import java.util.Date;
    import java.util.List;
    import java.util.Objects;
    import java.util.Set;

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

        @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
        @JsonIgnore
        @ToString.Exclude // Prevents recursion
        private List<Group> groups;

        @CreatedDate
        @Column(updatable = false)
        private LocalDateTime createdAt;

        private String academicYear = LocalDateTime.now().getYear() + "";

        @ManyToOne
        @JoinColumn(name = "faculty_id")
        private Faculty assignedFaculty;

        public void addGroup(Group group) {
            group.setBatch(this);
            this.groups.add(group);
        }
    }

