package com.example.UserManagementModule.entity.Groups;

import com.example.UserManagementModule.Helper.TaskStatus;
import com.example.UserManagementModule.entity.Batches.Batch;
import com.example.UserManagementModule.entity.Chat.Message;
import com.example.UserManagementModule.entity.Faculty.Faculty;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.entity.Weeks.Week;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
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

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> students = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "group_technologies",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "technology_id")
    )
    private List<Technology> technologies = new ArrayList<>();

    private String batchName;

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JsonIgnore
    private Batch batch;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Week> weeks = new ArrayList<>();

    private String groupLeader;

    private String projectStatus = TaskStatus.IN_PROGRESS.name();

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    private Faculty mentor;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private String startDate;

    // Chat
//    @OneToMany()
//    List<Message> messages = new ArrayList<String>();

    public void addWeek(Week week) {
        weeks.add(week);
        week.setGroup(this);
    }

    public void removeWeek(Week week) {
        weeks.remove(week);
        week.setGroup(null);
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void removeStudent(Student student) {
        students.remove(student);
    }

    public void addTechnology(Technology technology) {
        technologies.add(technology);
    }

    public void removeTechnology(Technology technology) {
        technologies.remove(technology);
    }

    @PreRemove
    private void preRemove() {
        students.clear();
        if (batch != null) {
            batch.getGroups().remove(this);
        }
        if (mentor != null) {
            mentor.getGroups().remove(this);
        }
    }
}