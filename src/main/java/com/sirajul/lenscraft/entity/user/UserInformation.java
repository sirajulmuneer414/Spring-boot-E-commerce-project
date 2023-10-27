package com.sirajul.lenscraft.entity.user;

import com.sirajul.lenscraft.entity.user.enums.Role;
import com.sirajul.lenscraft.entity.user.enums.UserStatus;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "user_info"
)
public class UserInformation{
    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    @Column(name = "user_id")
    UUID userId;


    @Email
    @Column(
            unique = true,
            name = "email_id"
    )
    String emailId;

    @Valid
    @NotEmpty
    String firstName;

    @Nullable
    String lastName;

    @NotNull
    @Column(
            columnDefinition = "VARCHAR(30)"
    )
    String password;

    @Column(
            nullable = false
    )
    @Enumerated(EnumType.STRING)
    Role role = Role.USER;

    @Column(
            name = "user_created",
            nullable = false
    )
    @CreationTimestamp
    LocalDateTime userCreated;

    @Column(
            name = "user_status"
    )
    @Enumerated(EnumType.STRING)
    UserStatus userStatus = UserStatus.ACTIVE;

    String profilePic;

    @Column(
            name = "phone_number",
            columnDefinition = "VARCHAR(10)"
    )
    String phone;

}
