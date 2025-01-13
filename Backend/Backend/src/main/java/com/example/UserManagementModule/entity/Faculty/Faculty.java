package com.example.UserManagementModule.entity.Faculty;

import com.example.UserManagementModule.entity.Batches.Batch;
import com.example.UserManagementModule.entity.Comment.Comment;
import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Providers;
import com.example.UserManagementModule.entity.Role;
import com.example.UserManagementModule.entity.Student.Student;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "faculties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Faculty implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    private String email;

    @Column(nullable = false)
    private boolean enabled = false;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Student> students = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Providers providers = Providers.SELF;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "faculty_role",
            joinColumns = @JoinColumn(name = "faculties_id"),
            inverseJoinColumns = @JoinColumn(name = "roles_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "mentor", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Group> groups = new ArrayList<>();

    @OneToMany(mappedBy = "assignedFaculty", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch  = FetchType.EAGER)
    @JsonIgnore
    private List<Batch> batches = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setFaculty(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setFaculty(null);
    }

    public void addStudent(Student student) {
        students.add(student);
        student.setMentor(this);
    }

    public void removeStudent(Student student) {
        students.remove(student);
        student.setMentor(null);
    }

    public void addGroup(Group group) {
        groups.add(group);
        group.setMentor(this);
    }

    public void removeGroup(Group group) {
        groups.remove(group);
        group.setMentor(null);
    }

    public void addBatch(Batch batch) {
        batches.add(batch);
        batch.setAssignedFaculty(this);
    }

    public void removeBatch(Batch batch) {
        batches.remove(batch);
        batch.setAssignedFaculty(null);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faculty faculty = (Faculty) o;
        return Objects.equals(id, faculty.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}