package com.sirajul.lenscraft.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SignupDto{

        @NotNull
        String firstName;

        String lastName;

        String password;

        @NotNull
        @Email
        String emailId;

        String phone;

        String otp;

        String profilePic;

        @DateTimeFormat
        LocalDateTime otpGeneratedTime;

}
