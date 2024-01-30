package com.sirajul.lenscraft.entity.wallet;

import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.entity.wallet.embedable.Transactions;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @OneToOne
    UserInformation user;

    @Column(
            columnDefinition = "integer default 0"
    )
    Integer balance = 0;

    @Embedded
    @ElementCollection
    List<Transactions> transactions;
}
