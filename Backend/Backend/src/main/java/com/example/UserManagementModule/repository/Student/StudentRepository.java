package com.example.UserManagementModule.repository.Student;


import com.example.UserManagementModule.entity.Student.Skills;
import com.example.UserManagementModule.entity.Student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

	Optional<Student> findByUsername(String username);

	Optional<Student> findByEmail(String email);

//	List<Student> findAllByUsername(List<String> username);

}
