package com.sirajul.lenscraft.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SignupDto(

        UUID userId,
        @NotNull
        String firstName,

        String lastName,

        @Valid
        String password,

        @NotNull
        @Email
        String emailId
) {
}
