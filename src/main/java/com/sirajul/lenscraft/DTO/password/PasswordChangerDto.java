package com.sirajul.lenscraft.DTO.password;

import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PasswordChangerDto {

    String emailId;

    String otp;

    LocalDateTime otpGeneratedTime;

}
