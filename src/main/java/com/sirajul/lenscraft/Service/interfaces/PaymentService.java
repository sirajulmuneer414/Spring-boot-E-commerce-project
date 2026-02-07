package com.sirajul.lenscraft.Service.interfaces;

import com.sirajul.lenscraft.entity.user.Order;
import com.sirajul.lenscraft.entity.user.UserInformation;

/**
 * Service interface for handling payment processing logic.
 */
public interface PaymentService {

    /**
     * Process a wallet + UPI payment after Razorpay payment succeeds.
     * Deducts wallet balance, creates transaction, and stores Razorpay details.
     *
     * @param order                The order to update
     * @param user                 The user making the payment
     * @param walletAmountToDeduct Amount to deduct from wallet
     * @param razorpayPaymentId    Razorpay payment ID
     * @param razorpayOrderId      Razorpay order ID
     * @param razorpaySignature    Razorpay signature
     * @return Updated order with payment details
     */
    Order processWalletPlusUpiPayment(Order order, UserInformation user, Integer walletAmountToDeduct,
            String razorpayPaymentId, String razorpayOrderId, String razorpaySignature);

    /**
     * Process a full wallet payment (no Razorpay).
     *
     * @param order The order to update
     * @param user  The user making the payment
     * @return Updated order with payment details
     */
    Order processFullWalletPayment(Order order, UserInformation user);

    /**
     * Process a UPI-only payment (no wallet).
     *
     * @param order             The order to update
     * @param razorpayPaymentId Razorpay payment ID
     * @param razorpayOrderId   Razorpay order ID
     * @param razorpaySignature Razorpay signature
     * @return Updated order with payment details
     */
    Order processUpiPayment(Order order, String razorpayPaymentId, String razorpayOrderId, String razorpaySignature);

}
