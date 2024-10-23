package com.example.UserManagementModule.controller.Otp;

import com.example.UserManagementModule.entity.Otp.EmailVerifyRequest;
import com.example.UserManagementModule.entity.Otp.OtpSentReq;
import com.example.UserManagementModule.service.Otp.EmailService;
import com.example.UserManagementModule.service.Student.StudentService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/otp")
public class OtpController {

    @Autowired
    private EmailService emailService;

    private static   Map<String, String> otpStore = new HashMap<>();
    @Autowired
    private StudentService studentService;

    @PostMapping("/send")
    public ResponseEntity<String> sendOTP(@RequestBody OtpSentReq otpSentReq) throws MessagingException {

        if(studentService.getStudentByEmail(otpSentReq.getEmail()).isPresent()) {
            throw new MessagingException("User Already Exists...");
        }

        String email = otpSentReq.getEmail();
        String otp = String.format("%05d", new Random().nextInt(99999));
        otpStore.put(email, otp);

//        System.out.println(otp);
        emailService.sendOTP(email, otp);

        return ResponseEntity.ok("OTP sent successfully to " + email);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOTP(@RequestBody EmailVerifyRequest emailVerifyRequest) {
        String email = emailVerifyRequest.getEmail();
        String otp = emailVerifyRequest.getOtp();

        String storedOTP = otpStore.get(email);

        System.out.println(storedOTP);

        if (storedOTP != null && storedOTP.equals(otp)) {
            otpStore.remove(email);
            return ResponseEntity.ok("OTP verified successfully.");
        } else {
            return ResponseEntity.status(400).body("Invalid OTP.");
        }
    }
}
