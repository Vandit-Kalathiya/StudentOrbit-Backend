package com.example.UserManagementModule.controller.Otp;

import com.example.UserManagementModule.entity.Otp.EmailVerifyRequest;
import com.example.UserManagementModule.entity.Otp.OtpSentReq;
import com.example.UserManagementModule.repository.Student.StudentRepository;
import com.example.UserManagementModule.service.Otp.EmailService;
import com.example.UserManagementModule.service.Student.StudentService;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/otp")
public class OtpController {

    private static final Logger logger = LoggerFactory.getLogger(OtpController.class);

    private static final Map<String, String> otpStore = new HashMap<>();

    @Autowired
    private EmailService emailService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/send")
    public ResponseEntity<?> sendOTP(@RequestBody OtpSentReq otpSentReq) {
        try {
            if (otpSentReq == null || otpSentReq.getEmail() == null || otpSentReq.getEmail().trim().isEmpty()) {
                logger.warn("Invalid OTP request: {}", otpSentReq);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("OTP request cannot be null or have empty email");
            }
            if (otpSentReq.getUsername() == null || otpSentReq.getUsername().trim().isEmpty()) {
                logger.warn("Invalid username in OTP request: {}", otpSentReq.getUsername());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Username cannot be null or empty");
            }

            if (studentRepository.findByUsername(otpSentReq.getUsername().toUpperCase()).isPresent()) {
                logger.warn("Student already exists with username: {}", otpSentReq.getUsername());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Student already exists with username: " + otpSentReq.getUsername());
            }
            if (studentRepository.findByEmail(otpSentReq.getEmail()).isPresent()) {
                logger.warn("Student already exists with email: {}", otpSentReq.getEmail());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Student already exists with email: " + otpSentReq.getEmail());
            }

            String email = otpSentReq.getEmail();
            String otp = String.format("%05d", new Random().nextInt(99999));
            otpStore.put(email, otp);

            emailService.sendOTP(email, otp);
            logger.info("OTP sent successfully to: {}", email);
            return ResponseEntity.ok("OTP sent successfully to " + email);
        } catch (MessagingException e) {
            logger.error("Error sending OTP to {}: {}", otpSentReq.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send OTP: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing OTP request for {}: {}", otpSentReq.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process OTP request: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOTP(@RequestBody EmailVerifyRequest emailVerifyRequest) {
        try {
            if (emailVerifyRequest == null || emailVerifyRequest.getEmail() == null || emailVerifyRequest.getEmail().trim().isEmpty()) {
                logger.warn("Invalid verify request: {}", emailVerifyRequest);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Verify request cannot be null or have empty email");
            }
            if (emailVerifyRequest.getOtp() == null || emailVerifyRequest.getOtp().trim().isEmpty()) {
                logger.warn("Invalid OTP in verify request: {}", emailVerifyRequest.getOtp());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("OTP cannot be null or empty");
            }

            String email = emailVerifyRequest.getEmail();
            String otp = emailVerifyRequest.getOtp();
            String storedOTP = otpStore.get(email);

            if (storedOTP == null) {
                logger.warn("No OTP found for email: {}", email);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No OTP found for email: " + email);
            }
            if (!storedOTP.equals(otp)) {
                logger.warn("Invalid OTP for email: {}", email);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid OTP");
            }

            otpStore.remove(email);
            logger.info("OTP verified successfully for: {}", email);
            return ResponseEntity.ok("OTP verified successfully");
        } catch (Exception e) {
            assert emailVerifyRequest != null;
            logger.error("Error verifying OTP for {}: {}", emailVerifyRequest.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to verify OTP: " + e.getMessage());
        }
    }
}