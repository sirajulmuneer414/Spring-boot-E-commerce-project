package com.sirajul.lenscraft.entity.wallet;

import com.sirajul.lenscraft.entity.user.UserInformation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @Column(name = "user_id")
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private UserInformation user;

    @Column(columnDefinition = "integer default 0")
    Integer balance = 0;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wallet")
    List<Transactions> transactions;

    public Wallet() {
        transactions = new ArrayList<>();
    }
}
