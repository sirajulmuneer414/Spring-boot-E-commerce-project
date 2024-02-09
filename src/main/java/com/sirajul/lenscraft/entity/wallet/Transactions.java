package com.sirajul.lenscraft.entity.wallet;


import com.sirajul.lenscraft.entity.wallet.enums.TransactionStatus;
import com.sirajul.lenscraft.entity.wallet.enums.TypeOfTransaction;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long transactionId;

    private Integer amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    private TransactionStatus transactionStatus;

    private LocalDateTime transactionTime;

    private TypeOfTransaction transactionType;
}
