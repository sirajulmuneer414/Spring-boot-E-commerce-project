package com.sirajul.lenscraft.entity.user;

import com.sirajul.lenscraft.entity.user.enums.Role;
import com.sirajul.lenscraft.entity.user.enums.UserStatus;
import com.sirajul.lenscraft.entity.wallet.Wallet;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Component
@Entity
@Setter
@Getter
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
            name = "email_id",
            columnDefinition = "VARCHAR(100)"
    )
    String emailId;

    @Valid
    @NotEmpty
    String firstName;

    @Nullable
    String lastName;

    @NotNull
    @Column(
            columnDefinition = "VARCHAR(100)"
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

    String referralCode;

    @OneToOne(cascade = CascadeType.ALL)
    Wishlist wishlist;

    @OneToOne(cascade = CascadeType.ALL)
    Cart cart;

    @OneToOne(mappedBy = "user")
    Wallet wallet;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    List<Address> addresses;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    List<Order> orders;

}
