    package com.example.UserManagementModule.entity.Batches;

    import com.example.UserManagementModule.entity.Batches.CustomAnnotations.ValidSemester;
    import com.example.UserManagementModule.entity.Batches.CustomAnnotations.ValidYear;
    import com.example.UserManagementModule.entity.Groups.Group;
    import jakarta.persistence.*;
    import lombok.*;
    import org.springframework.data.annotation.CreatedDate;

    import javax.validation.constraints.Max;
    import javax.validation.constraints.Min;
    import javax.validation.constraints.Pattern;
    import java.io.Serializable;
    import java.time.LocalDateTime;
    import java.util.Date;
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

        @Column()
        private String batchName;

        @Min(value = 1, message = "Year must be at least 1")
        @Max(value = 4, message = "Year must be at most 4")
        @ValidYear(message = "Year is invalid")
        private Integer year;

        @Min(value = 1, message = "Semester must be at least 1")
        @Max(value = 8, message = "Semester must be at most 8")
        @ValidSemester(message = "Semester is invalid")
        private Integer semester;

        @Pattern(regexp = "^\\d{2}CE(0[1-9]|[1-9][0-9]|1[0-8][0-9]|190)$", message = "Invalid start Id.")
        private String startId;

        @Pattern(regexp = "^\\d{2}CE(0[1-9]|[1-9][0-9]|1[0-8][0-9]|190)$", message = "Invalid end Id.")
        private String endId;

        @OneToMany(mappedBy = "batchName", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
        private Set<Group> groups;  // Adjusted to handle multiple groups within a batch

        @CreatedDate
        @Column(updatable = false)
        private LocalDateTime createdAt;

        private String academicYear = LocalDateTime.now().getYear() + "";

//        @Override
//        public int hashCode() {
//            // Exclude groups to avoid circular reference
//            return Objects.hash(id, batchName, year, semester, startId, endId);
//        }
//
//        @Override
//        public boolean equals(Object obj) {
//            if (this == obj) return true;
//            if (obj == null || getClass() != obj.getClass()) return false;
//            Batch batch = (Batch) obj;
//            return Objects.equals(id, batch.id);
//        }
    }
