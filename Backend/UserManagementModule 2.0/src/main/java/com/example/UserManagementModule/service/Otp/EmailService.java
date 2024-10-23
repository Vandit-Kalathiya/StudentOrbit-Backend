package com.example.UserManagementModule.service.Otp;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOTP(String toEmail, String otp) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        String htmlContent = "<h1>Your OTP code is: " + otp + "</h1>"
                + "<p>Please use this code to verify your email.</p>";

        helper.setText(htmlContent, true); // Set to true for HTML content
        helper.setTo(toEmail);
        helper.setSubject("Your OTP Code");
        helper.setFrom("vanditkaj266@gmail.com");

        mailSender.send(mimeMessage);
    }
}
