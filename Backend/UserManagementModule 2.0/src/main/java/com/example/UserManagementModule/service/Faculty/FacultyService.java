package com.example.UserManagementModule.service.Faculty;

import com.example.UserManagementModule.dto.Faculty.FacultyRegisterRequest;
import com.example.UserManagementModule.entity.Faculty.Faculty;
import com.example.UserManagementModule.repository.Faculty.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FacultyService {

    @Autowired
    private FacultyRepository facultyRepository;

    public List<Faculty> findAllFaculties() {
        return facultyRepository.findAll();
    }

    public Faculty findFacultyById(String id) {
        return facultyRepository.findById(id).get();
    }

    public Faculty findFacultyByFacultyName(String facultyName) {return facultyRepository.findByUsername(facultyName).get();}

    public Faculty saveFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty updateFaculty(Faculty faculty) {
        BCryptPasswordEncoder bCryptPasswordEncoder= new BCryptPasswordEncoder();
        String encodedPassword = bCryptPasswordEncoder.encode(faculty.getPassword());

        Faculty newfaculty = facultyRepository.findById(faculty.getId()).get();
        newfaculty.setEmail(faculty.getEmail());
        newfaculty.setUsername(faculty.getUsername());
        newfaculty.setPassword(encodedPassword);

        return facultyRepository.save(newfaculty);
    }

    public void createFaculty(FacultyRegisterRequest facultyRequest) {

        BCryptPasswordEncoder bCryptPasswordEncoder= new BCryptPasswordEncoder();
        String encodedPassword = bCryptPasswordEncoder.encode(facultyRequest.getPassword());

        Faculty faculty = new Faculty();
        faculty.setUsername(facultyRequest.getUsername());
        faculty.setPassword(encodedPassword);
        faculty.setEmail(facultyRequest.getEmail());
        faculty.setEnabled(true);
        faculty.setEmailVerified(true);
        faculty.setCreatedAt(LocalDateTime.now());

        facultyRepository.save(faculty);
    }
}
