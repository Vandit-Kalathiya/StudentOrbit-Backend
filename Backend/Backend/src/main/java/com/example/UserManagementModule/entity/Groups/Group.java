package com.example.UserManagementModule.entity.Groups;

import com.example.UserManagementModule.Helper.TaskStatus;
import com.example.UserManagementModule.entity.Batches.Batch;
import com.example.UserManagementModule.entity.Faculty.Faculty;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.entity.Weeks.Week;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "student_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String uniqueGroupId;

    private String groupName;

    private String groupDescription;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    @JsonManagedReference
    private Set<Student> students;

//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(name = "group_technologies", joinColumns = @JoinColumn(name = "group_id"))
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "group_technologies",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "technology_id")
    )
    private List<Technology> technologies;

    private String batchName;

    @ManyToOne
    @ToString.Exclude // Prevents recursion
    private Batch batch;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference // To manage the serialization of weeks
    @ToString.Exclude // Prevents recursion
    private List<Week> weeks = new ArrayList<>();

    private String groupLeader;

    private String projectStatus = TaskStatus.IN_PROGRESS.name();

    @ManyToOne
    private Faculty mentor;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private String startDate;

    public void addWeek(Week week) {
        week.setGroup(this);
        this.weeks.add(week);
    }
}
