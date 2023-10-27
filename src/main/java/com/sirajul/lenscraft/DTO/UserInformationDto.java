package com.sirajul.lenscraft.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInformationDto {

    UUID id;

    String emailId;

    String firstName;

    String lastName;

    String role;

    String userStatus;

}
