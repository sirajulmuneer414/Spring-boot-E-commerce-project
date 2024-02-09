package com.sirajul.lenscraft.entity.wallet;

import com.sirajul.lenscraft.entity.user.UserInformation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
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

    @OneToMany(cascade = CascadeType.ALL)
    List<Transactions> transactions;

    public Wallet(){
        transactions = new ArrayList<>();
    }
}
