package com.example.UserManagementModule.service.Faculty;

import com.example.UserManagementModule.dto.Faculty.FacultyRegisterRequest;
import com.example.UserManagementModule.entity.Batches.Batch;
import com.example.UserManagementModule.entity.Faculty.Faculty;
import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.repository.Faculty.FacultyRepository;
import com.example.UserManagementModule.repository.Group.GroupRepository;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FacultyService {

    @Autowired
    private FacultyRepository facultyRepository;
    @Autowired
    private GroupRepository groupRepository;

//    @Cacheable(value = "facultyList")
    public List<Faculty> findAllFaculties() {
        return facultyRepository.findAll();
    }

//    @Cacheable(value = "faculty", key = "#id")
    public Faculty findFacultyById(String id) {
        return facultyRepository.findById(id).orElse(null);
    }

//    @Cacheable(value = "faculty", key = "#facultyName")
    public Faculty findFacultyByFacultyName(String facultyName) {
        System.out.println("------------------------------------------"+facultyName+"-----------------------------------");
        return facultyRepository.findByUsername(facultyName).get();
    }

//    @CachePut(value = "faculty", key = "#faculty.id")
    public Faculty saveFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    @CachePut(value = "faculty", key = "#faculty.id")
    public Faculty updateFaculty(Faculty faculty) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = bCryptPasswordEncoder.encode(faculty.getPassword());

        Faculty newFaculty = facultyRepository.findById(faculty.getId()).orElse(null);
        if (newFaculty != null) {
            newFaculty.setEmail(faculty.getEmail());
            newFaculty.setUsername(faculty.getUsername());
            newFaculty.setPassword(encodedPassword);
            return facultyRepository.save(newFaculty);
        }
        return null;
    }

    @CacheEvict(value = "facultyList", allEntries = true)
    public void createFaculty(FacultyRegisterRequest facultyRequest) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = bCryptPasswordEncoder.encode(facultyRequest.getPassword());

        Faculty faculty = new Faculty();
        faculty.setUsername(facultyRequest.getUsername());
        faculty.setName(facultyRequest.getFacultyName());
        faculty.setPassword(encodedPassword);
        faculty.setEmail(facultyRequest.getEmail());
        faculty.setEnabled(true);
        faculty.setEmailVerified(true);
        faculty.setCreatedAt(LocalDateTime.now());

        this.saveFaculty(faculty);
    }

    public Group selectMentor(String facultyName, String groupId) {
        Group group = groupRepository.findById(groupId).orElse(null);
        Faculty faculty = facultyRepository.findByName(facultyName).orElse(null);
        if(group == null){
            throw new NotFoundException("Group not found for group id : "+ groupId);
        }
        if(faculty == null){
            throw new NotFoundException("Faculty not found for faculty id : "+ facultyName);
        }
        group.setMentor(faculty);
        faculty.addGroup(group);

        this.saveFaculty(faculty);

        return group;
    }
}
