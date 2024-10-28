package com.example.UserManagementModule.entity.Groups;

import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.entity.Weeks.Week;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "group_description")
    private String groupDescription;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> students;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "group_technologies", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "technology")
    private Set<String> technologies;

    private String batchName;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JsonManagedReference // To manage the serialization of weeks
    private List<Week> weeks = new ArrayList<>();

    private String groupLeader;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public void addWeek(Week week) {
        week.setGroup(this);
        this.weeks.add(week);
    }
}
