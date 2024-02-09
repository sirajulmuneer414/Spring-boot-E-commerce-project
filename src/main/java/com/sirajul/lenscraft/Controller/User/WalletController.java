package com.sirajul.lenscraft.Controller.User;

import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.entity.wallet.Transactions;
import com.sirajul.lenscraft.entity.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    UserService userService;

    @GetMapping
    public String getWallet(
            Model model,
            @RequestParam("userId") UUID userId
    ){

        UserInformation user = userService.findById(userId);

        Wallet wallet = user.getWallet();
        List<Transactions> transactions = new ArrayList<>();
        if(wallet == null){
            wallet = new Wallet();
            wallet.setUser(user);
            wallet.setBalance(0);
            user.setWallet(wallet);
            userService.save(user);
        }
        else{
            transactions = wallet.getTransactions();
        }


        model.addAttribute("user",user);

        model.addAttribute("wallet",wallet);

        model.addAttribute("transactions",transactions);

        return "user/wallet/get-wallet";
    }
}
