package com.example.UserManagementModule.service.Student;

import com.example.UserManagementModule.dto.Student.ProfileUpdateRequest;
import com.example.UserManagementModule.dto.Student.StudentRequest;
import com.example.UserManagementModule.dto.Student.StudentResgisterRequest;
import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Student.Skills;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.repository.Group.GroupRepository;
import com.example.UserManagementModule.repository.Skills.SkillsRepository;
import com.example.UserManagementModule.repository.Student.StudentRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final SkillsRepository skillsRepository;

    public StudentService(StudentRepository studentRepository, GroupRepository groupRepository, SkillsRepository skillsRepository) {
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
        this.skillsRepository = skillsRepository;
    }

    @CacheEvict(value = "students", allEntries = true)
    public void createStudent(StudentResgisterRequest studentRequest) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = bCryptPasswordEncoder.encode(studentRequest.getPassword());
        if (studentRepository.findByUsername(studentRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Student already exists with username : " + studentRequest.getUsername());
        }
        Student student = new Student();
        student.setUsername(studentRequest.getUsername().toUpperCase());
        student.setPassword(encodedPassword);
        student.setEmail(studentRequest.getEmail());
        student.setEnabled(true);
        student.setEmailVerified(true);
        student.setCreatedAt(LocalDateTime.now());
        student.setRoles(new HashSet<>());

        studentRepository.save(student);
    }

    public Student updateStudent(String studentId, StudentRequest studentDTO) {
        Optional<Student> optionalStudent = studentRepository.findById(studentId);
        if (optionalStudent.isPresent()) {
            Student existingStudent = optionalStudent.get();

            // Update only the fields that are not null in the DTO
            if (studentDTO.getUsername() != null) {
                existingStudent.setUsername(studentDTO.getUsername());
            }
            if (studentDTO.getPassword() != null) {
                existingStudent.setPassword(studentDTO.getPassword());
            }
            if (studentDTO.getEmail() != null) {
                existingStudent.setEmail(studentDTO.getEmail());
            }
            existingStudent.setEnabled(studentDTO.isEnabled());
            existingStudent.setEmailVerified(studentDTO.isEmailVerified());
            if (studentDTO.getProviders() != null) {
                existingStudent.setProviders(studentDTO.getProviders());
            }
            if (studentDTO.getGitHubUrl() != null) {
                existingStudent.setGitHubUrl(studentDTO.getGitHubUrl());
            }
            if (studentDTO.getLinkedInUrl() != null) {
                existingStudent.setLinkedInUrl(studentDTO.getLinkedInUrl());
            }
            if (studentDTO.getSkills() != null) {
                Set<Skills> skillsList = existingStudent.getSkills();
                Set<Skills> tempList = existingStudent.getSkills();
                if (skillsList == null) {
                    skillsList = new HashSet<>();
                }
                studentDTO.getSkills().forEach(skill -> {
                    if (skillsRepository.findByName(skill) == null) {
                        Skills newSkill = new Skills(skill.toLowerCase());
                        Skills savedSkill = skillsRepository.save(newSkill);
                        tempList.add(savedSkill);
                    } else {
                        Skills getSavedSkill = skillsRepository.findByName(skill);
                        tempList.add(getSavedSkill);
                    }
                });
                if (tempList != null) {
                    skillsList.addAll(tempList);
                }
                existingStudent.setSkills(skillsList);
            }

            return studentRepository.save(existingStudent);
        } else {
            // Handle the case where the student does not exist
            throw new RuntimeException("Student not found with id: " + studentId);
        }
    }

    @Cacheable(value = "students")
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Cacheable(value = "student", key = "#id")
    public Optional<Student> getStudentById(String id) {
        return studentRepository.findById(id);
    }

    @CachePut(value = "students", key = "#student.id")
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    @CacheEvict(value = "students", key = "#id")
    public void deleteStudent(String id) {
        studentRepository.deleteById(id);
    }

    @Cacheable(value = "student", key = "#username")
    public Optional<Student> getStudentByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    @Cacheable(value = "student", key = "#email")
    public Optional<Student> getStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

//    @Cacheable(value = "studentGroups",key = "#sid")
    public List<Group> getStudentGroups(String sid) {
        return groupRepository.findGroupsByStudentId(sid.toLowerCase());
    }

    @CacheEvict(value = "students", key = "#id")
    public Student addStudentSkill(String id, List<String> skills) {
        if (!this.getStudentByUsername(id.toUpperCase()).isPresent()) {
            throw new RuntimeException("Student not found for id : " + id.toUpperCase());
        }
        Student student = this.getStudentByUsername(id.toUpperCase()).get();

        Set<Skills> skillsList = student.getSkills();
        Set<Skills> tempList = student.getSkills();
        if (skillsList == null) {
            skillsList = new HashSet<>();
        }
        skills.forEach(skill -> {
            if (skillsRepository.findByName(skill) == null) {
                Skills newSkill = new Skills(skill.toLowerCase());
                Skills savedSkill = skillsRepository.save(newSkill);
                tempList.add(savedSkill);
            } else {
                Skills getSavedSkill = skillsRepository.findByName(skill);
                tempList.add(getSavedSkill);
            }
        });
        if (tempList != null) {
            skillsList.addAll(tempList);
        }
        student.setSkills(skillsList);

        return studentRepository.save(student);
    }

    @Cacheable(value = "studentSkills",key = "#username")
    public Set<Skills> getStudentSkills(String username) {
        return studentRepository.findByUsername(username).get().getSkills();
    }

    @CacheEvict(value = "students", key = "#id")
    public Set<Skills> deleteStudentSkill(String id, String skill) {
        Student student = studentRepository.findByUsername(id).get();
        Set<Skills> skills = student.getSkills();
        Skills savedSkills = skillsRepository.findByName(skill.toLowerCase());
        skills.remove(savedSkills);
        Student savedStudent = studentRepository.save(student);
        return savedStudent.getSkills();
    }

    @CacheEvict(value = "students", key = "#username")
    public Student updateStudentProfile(String username, ProfileUpdateRequest profileUpdateRequest) {
        Student student = studentRepository.findByUsername(username).get();
        student.setGitHubUrl(profileUpdateRequest.getGitHubUrl());
        student.setLinkedInUrl(profileUpdateRequest.getLinkedInUrl());
        saveStudent(student);
        return student;
    }
}
