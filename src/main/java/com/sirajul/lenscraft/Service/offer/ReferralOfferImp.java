package com.sirajul.lenscraft.Service.offer;

import com.sirajul.lenscraft.Repository.UserRepository;
import com.sirajul.lenscraft.Repository.offer.ReferralOfferRepository;
import com.sirajul.lenscraft.Service.interfaces.ReferralOfferService;
import com.sirajul.lenscraft.entity.offer.ReferralOffer;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.entity.wallet.Transactions;
import com.sirajul.lenscraft.entity.wallet.Wallet;
import com.sirajul.lenscraft.entity.wallet.enums.TransactionStatus;
import com.sirajul.lenscraft.entity.wallet.enums.TypeOfTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ReferralOfferImp implements ReferralOfferService{
    @Autowired
    ReferralOfferRepository referralOfferRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean isOfferAlreadyEstablished() {
        boolean excists = referralOfferRepository.existsById(1);

        if(excists){

            if(referralOfferRepository.findById(1).get().getEndDate().isBefore(LocalDate.now())){
                referralOfferRepository.deleteById(1);
                excists = false;
            } else if (referralOfferRepository.findById(1).get().getStartDate().isAfter(LocalDate.now())) {
                excists = false;

            }

        }
        return excists;
    }

    @Override
    public void save(ReferralOffer offer) {
        referralOfferRepository.save(offer);
    }

    @Override
    public void assignMoneyToWallets(UserInformation userAvailed, UserInformation userReferred) {

        ReferralOffer offer = referralOfferRepository.findById(1).get();

        Wallet wallet1 = userAvailed.getWallet();
        if(wallet1 == null){
            wallet1 = new Wallet();
        }

        Wallet wallet2 = userReferred.getWallet();
        if(wallet2 == null){
            wallet2 = new Wallet();
        }


        Transactions transactions = new Transactions();

        transactions.setTransactionStatus(TransactionStatus.DEBIT);
        transactions.setWallet(wallet1);
        transactions.setTransactionTime(LocalDateTime.now());
        transactions.setTransactionType(TypeOfTransaction.REFERRAL_BONUS);
        transactions.setAmount(offer.getMoneyToWallet());

        wallet1.getTransactions().add(transactions);
        wallet1.setBalance(wallet2.getBalance()+transactions.getAmount());
        wallet1.setUser(userAvailed);

        userAvailed.setWallet(wallet1);

        userRepository.save(userAvailed);

        Transactions transactions1 = new Transactions();

        transactions1.setTransactionStatus(TransactionStatus.DEBIT);
        transactions1.setWallet(wallet1);
        transactions1.setTransactionTime(LocalDateTime.now());
        transactions1.setTransactionType(TypeOfTransaction.REFERRAL_BONUS);
        transactions1.setAmount(offer.getMoneyToReferred());

        wallet2.getTransactions().add(transactions1);
        wallet2.setBalance(wallet2.getBalance()+transactions1.getAmount());
        wallet2.setUser(userReferred);

        userReferred.setWallet(wallet2);
        userRepository.save(userReferred);

    }

    @Override
    public ReferralOffer getTheReferalOffer() {
        return referralOfferRepository.findById(1).get();
    }

    @Override
    public void delete() {
        referralOfferRepository.deleteById(1);
    }

    @Override
    public void edit(ReferralOffer refer) {

        ReferralOffer referToEdit = referralOfferRepository.findById(1).get();

        if(referToEdit.getMoneyToWallet() != refer.getMoneyToWallet()){
            referToEdit.setMoneyToWallet(refer.getMoneyToWallet());
        }
        if(referToEdit.getMoneyToReferred() != refer.getMoneyToReferred()){
            referToEdit.setMoneyToReferred(refer.getMoneyToReferred());
        }

        if(referToEdit.getStartDate().equals(refer.getStartDate())){
            referToEdit.setStartDate(refer.getStartDate());
        }
        if(referToEdit.getEndDate().equals(refer.getEndDate())){
            referToEdit.setEndDate(refer.getEndDate());
        }

        referralOfferRepository.save(referToEdit);

    }
}
