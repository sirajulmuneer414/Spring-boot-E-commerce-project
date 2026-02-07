package com.sirajul.lenscraft.Service.User;

import com.sirajul.lenscraft.Repository.WalletRepository;
import com.sirajul.lenscraft.Service.interfaces.PaymentService;
import com.sirajul.lenscraft.entity.user.Order;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.entity.user.enums.PaymentType;
import com.sirajul.lenscraft.entity.wallet.Transactions;
import com.sirajul.lenscraft.entity.wallet.Wallet;
import com.sirajul.lenscraft.entity.wallet.enums.TransactionStatus;
import com.sirajul.lenscraft.entity.wallet.enums.TypeOfTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PaymentServiceImp implements PaymentService {

    @Autowired
    private WalletRepository walletRepository;

    @Override
    @Transactional
    public Order processWalletPlusUpiPayment(Order order, UserInformation user, Integer walletAmountToDeduct,
            String razorpayPaymentId, String razorpayOrderId, String razorpaySignature) {

        // Deduct from wallet and create transaction
        if (walletAmountToDeduct > 0) {
            deductFromWallet(user, walletAmountToDeduct);
            order.setWalletAmount((double) walletAmountToDeduct);
        }

        // Set payment type and Razorpay details
        order.getPaymentDetails().setPaymentType(PaymentType.WALLET_PLUS_UPI);
        order.getPaymentDetails().setPaymentId(razorpayPaymentId);
        order.getPaymentDetails().setRazorpayOrderId(razorpayOrderId);
        order.getPaymentDetails().setRazorpaySignature(razorpaySignature);

        log.info("Processed WALLET_PLUS_UPI payment for order. Wallet deducted: {}, Razorpay ID: {}",
                walletAmountToDeduct, razorpayPaymentId);

        return order;
    }

    @Override
    @Transactional
    public Order processFullWalletPayment(Order order, UserInformation user) {
        Integer totalAmount = order.getTotalAmount();

        // Deduct full amount from wallet
        deductFromWallet(user, totalAmount);
        order.setWalletAmount((double) totalAmount);

        // Set payment type
        order.getPaymentDetails().setPaymentType(PaymentType.WALLET);

        log.info("Processed full WALLET payment for order. Amount: {}", totalAmount);

        return order;
    }

    @Override
    @Transactional
    public Order processUpiPayment(Order order, String razorpayPaymentId, String razorpayOrderId,
            String razorpaySignature) {
        // Set payment type and Razorpay details
        order.getPaymentDetails().setPaymentType(PaymentType.UPI_PAYMENT);
        order.getPaymentDetails().setPaymentId(razorpayPaymentId);
        order.getPaymentDetails().setRazorpayOrderId(razorpayOrderId);
        order.getPaymentDetails().setRazorpaySignature(razorpaySignature);

        log.info("Processed UPI payment for order. Razorpay ID: {}", razorpayPaymentId);

        return order;
    }

    /**
     * Helper method to deduct amount from user's wallet and create a debit
     * transaction.
     */
    private void deductFromWallet(UserInformation user, Integer amount) {
        Wallet wallet = user.getWallet();

        if (wallet == null) {
            log.warn("User has no wallet, cannot deduct amount: {}", amount);
            return;
        }

        if (wallet.getBalance() < amount) {
            log.warn("Insufficient wallet balance. Available: {}, Required: {}", wallet.getBalance(), amount);
            throw new IllegalStateException("Insufficient wallet balance");
        }

        // Deduct balance
        wallet.setBalance(wallet.getBalance() - amount);

        // Create transaction
        Transactions transaction = new Transactions();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType(TypeOfTransaction.ORDER);
        transaction.setTransactionStatus(TransactionStatus.DEBIT);
        transaction.setTransactionTime(LocalDateTime.now());

        List<Transactions> transactions = wallet.getTransactions();
        if (transactions == null) {
            transactions = new ArrayList<>();
            wallet.setTransactions(transactions);
        }
        transactions.add(transaction);

        // Save wallet
        walletRepository.save(wallet);

        log.info("Deducted {} from wallet. New balance: {}", amount, wallet.getBalance());
    }
}
