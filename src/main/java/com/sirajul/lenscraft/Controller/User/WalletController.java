package com.sirajul.lenscraft.Controller.User;

import com.razorpay.RazorpayClient;
import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.entity.wallet.Transactions;
import com.sirajul.lenscraft.entity.wallet.Wallet;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    UserService userService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @GetMapping
    public String getWallet(
            Model model,
            @RequestParam("userId") UUID userId) {

        UserInformation user = userService.findById(userId);

        Wallet wallet = user.getWallet();
        List<Transactions> transactions = new ArrayList<>();
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUser(user);
            wallet.setBalance(0);
            user.setWallet(wallet);
            userService.save(user);
        } else {
            transactions = wallet.getTransactions();
        }

        model.addAttribute("user", user);

        model.addAttribute("wallet", wallet);

        model.addAttribute("transactions", transactions);

        return "user/wallet/get-wallet";
    }

    @PostMapping("/createOrder")
    @ResponseBody
    public ResponseEntity<String> createOrder(@RequestParam("amount") int amount) throws Exception {
        try {
            var client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100); // amount in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "wallet_txn_" + System.currentTimeMillis());

            com.razorpay.Order order = client.orders.create(orderRequest);

            // Add the Razorpay key and status to the response
            JSONObject response = new JSONObject(order.toString());
            response.put("key", razorpayKeyId);
            response.put("status", "created");

            return new ResponseEntity<>(response.toString(), HttpStatus.OK);
        } catch (Exception e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
