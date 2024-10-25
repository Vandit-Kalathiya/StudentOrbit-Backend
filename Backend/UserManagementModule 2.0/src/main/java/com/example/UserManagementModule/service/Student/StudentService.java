package com.example.UserManagementModule.service.Student;


import com.example.UserManagementModule.dto.Student.ProfileUpdateRequest;
import com.example.UserManagementModule.dto.Student.StudentResgisterRequest;
import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Student.Skills;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.repository.Group.GroupRepository;
import com.example.UserManagementModule.repository.Skills.SkillsRepository;
import com.example.UserManagementModule.repository.Student.StudentRepository;
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

    public void createStudent(StudentResgisterRequest studentRequest) {

        BCryptPasswordEncoder bCryptPasswordEncoder= new BCryptPasswordEncoder();
        String encodedPassword = bCryptPasswordEncoder.encode(studentRequest.getPassword());
        if(studentRepository.findByUsername(studentRequest.getUsername()).isPresent()){
            throw new RuntimeException("Student already exists with username : " + studentRequest.getUsername());
        }
        Student student = new Student();
        student.setUsername(studentRequest.getUsername().toUpperCase());
        student.setPassword(encodedPassword);
        student.setEmail(studentRequest.getEmail());
        student.setEnabled(true);
        student.setEmailVerified(true);
        student.setCreatedAt(LocalDateTime.now());
        studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(String id) {
        return studentRepository.findById(id);
    }

    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    public void deleteStudent(String id) {
        studentRepository.deleteById(id);
    }

    public Optional<Student> getStudentByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    public Optional<Student> getStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    public List<Group> getStudentGroups(String sid) {
        return groupRepository.findGroupsByStudentId(sid.toLowerCase());
    }

    public Student addStudentSkill(String id, List<String> skills) {
        if (!this.getStudentByUsername(id.toUpperCase()).isPresent()) {
            throw new RuntimeException("Student not found for id : "+id.toUpperCase());
        }
        Student student = this.getStudentByUsername(id.toUpperCase()).get();

        Set<Skills> skillsList = student.getSkills();
        Set<Skills> tempList = student.getSkills();
        if(skillsList == null) {
            skillsList = new HashSet<>();
        }
        skills.forEach(skill -> {
            if(skillsRepository.findByName(skill) == null) {
                Skills newSkill = new Skills(skill.toLowerCase());
                Skills savedSkill = skillsRepository.save(newSkill);
                tempList.add(savedSkill);
            }
            else{
                Skills getSavedSkill = skillsRepository.findByName(skill);
                tempList.add(getSavedSkill);
            }
        });
        if(tempList != null) {
            skillsList.addAll(tempList);
        }
        student.setSkills(skillsList);

        return studentRepository.save(student);
    }

    public Set<Skills> getStudentSkills(String username) {
        return studentRepository.findByUsername(username).get().getSkills();
    }

    public Set<Skills> deleteStudentSkill(String id, String skill) {
        Student student = studentRepository.findByUsername(id).get();
        Set<Skills> skills = student.getSkills();
        Skills savedSkills = skillsRepository.findByName(skill.toLowerCase());
        skills.remove(savedSkills);
        Student savedStudent = studentRepository.save(student);
        return savedStudent.getSkills();
    }

    public Student updateStudentProfile(String username, ProfileUpdateRequest profileUpdateRequest) {
        Student student = studentRepository.findByUsername(username).get();
        student.setGitHubUrl(profileUpdateRequest.getGitHubUrl());
        student.setLinkedInUrl(profileUpdateRequest.getLinkedInUrl());
        saveStudent(student);
        return student;
    }
}
