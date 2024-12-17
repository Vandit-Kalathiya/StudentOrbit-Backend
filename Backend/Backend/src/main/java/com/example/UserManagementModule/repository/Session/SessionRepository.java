package com.example.UserManagementModule.repository.Session;

import com.example.UserManagementModule.entity.Session.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {

    Optional<Session> findBySessionId(String sessionId);

    
}
