package com.sirajul.lenscraft.entity.user;

import com.sirajul.lenscraft.entity.user.enums.Role;
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
import org.hibernate.validator.constraints.Range;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "user_info"
)
public class UserInformation implements UserDetails {
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
    Role role;

    @Column(
            name = "user_created",
            nullable = false
    )
    @CreationTimestamp
    LocalDateTime userCreated;

    @Column(
            name = "active"
    )
    Boolean isActive = false;

    String profilePic;

    @Column(
            name = "phone_number",
            columnDefinition = "VARCHAR(10)"
    )
    String phone;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(new SimpleGrantedAuthority(getRole().name()));
    }

    @Override
    public String getUsername() {
        return emailId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
