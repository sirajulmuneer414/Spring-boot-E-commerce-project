package com.sirajul.lenscraft.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public record SignupDto(

        @NotNull
        String firstName,

        String lastName,

        @Valid
        String password,

        @NotNull
        @Email
        String emailId,

        String phone
) {
}
