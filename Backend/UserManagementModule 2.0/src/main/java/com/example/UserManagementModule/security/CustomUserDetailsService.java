package com.example.UserManagementModule.security;

import com.example.UserManagementModule.entity.Faculty.Faculty;
import com.example.UserManagementModule.entity.Role;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.repository.Faculty.FacultyRepository;
import com.example.UserManagementModule.repository.Student.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private StudentRepository studentRepository;
	
	public CustomUserDetailsService(StudentRepository studentRepository)
	{
		this.studentRepository = studentRepository;
	}

	@Autowired
	FacultyRepository facultyRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		System.out.println("Student Security : " + username);

		Optional<Student> studentOpt = studentRepository.findByUsername(username);
//		System.out.println(studentOpt.get().getEmail());
		if (studentOpt.isPresent()) {
//			System.out.println("Security done using student...");
			return studentOpt.get();
		}

		Optional<Faculty> facultyOpt = facultyRepository.findByUsername(username);
//		System.out.println(facultyOpt.get().getEmail());
		if (facultyOpt.isPresent()) {
//			System.out.println("Security done using faculty...");
			return facultyOpt.get();
		}


		throw new UsernameNotFoundException("User not found");

//		return this.studentRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
	}

	private Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles) {
		return roles.stream()
				.map(role -> new SimpleGrantedAuthority(role.getRoleName()))  // Assuming role.getRoleName() returns a String
				.collect(Collectors.toSet());
	}
}
