package com.sirajul.lenscraft.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupDto{

        @NotNull
        String firstName;

        String lastName;

        @Valid
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
