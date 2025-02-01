package com.studentOrbit.generate_report_app.entity.Otp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpSentReq {
    private String email;
    private String username;
}
