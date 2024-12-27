package com.studentOrbit.generate_report_app.entity.Faculty;

import com.studentOrbit.generate_report_app.entity.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.studentOrbit.generate_report_app.entity.Batches.Batch;
import com.studentOrbit.generate_report_app.entity.Comment.Comment;
import com.studentOrbit.generate_report_app.entity.Groups.Group;
import com.studentOrbit.generate_report_app.entity.Student.Student;
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

//@Entity
//@Table(name = "faculties")
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

//    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean enabled = false;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
//    @JsonManagedReference
    @JsonIgnore
    private List<Comment> comments;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Student> students;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Providers providers = Providers.SELF;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "faculty_role",
            joinColumns = @JoinColumn(name = "faculties_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "roles_id", referencedColumnName = "id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Group> groups;

    @OneToMany(mappedBy = "assignedFaculty", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Batch> batches;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void addComment(Comment comment) {
        comment.setFaculty(this);
        this.comments.add(comment);
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

    public void addGroup(Group group) {
        group.setMentor(this);
        this.groups.add(group);
    }

    public void addBatch(Batch batch) {
        batch.setAssignedFaculty(this);
        this.batches.add(batch);
    }
}

