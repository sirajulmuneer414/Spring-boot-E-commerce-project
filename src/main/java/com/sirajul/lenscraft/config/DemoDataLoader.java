package com.sirajul.lenscraft.config;

import com.sirajul.lenscraft.Repository.UserRepository;
import com.sirajul.lenscraft.entity.user.Cart;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.entity.user.Wishlist;
import com.sirajul.lenscraft.entity.user.enums.ActiveStatus;
import com.sirajul.lenscraft.entity.user.enums.Role;
import com.sirajul.lenscraft.entity.wallet.Wallet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Seeds demo accounts (customer + admin) on every startup if they don't exist.
 * These are used by the "For Testing" quick-login buttons on the login page.
 */
@Component
@Slf4j
public class DemoDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.demo.customer.email}")
    private String customerEmail;

    @Value("${app.demo.customer.password}")
    private String customerPassword;

    @Value("${app.demo.admin.email}")
    private String adminEmail;

    @Value("${app.demo.admin.password}")
    private String adminPassword;

    public DemoDataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedDemoUser(customerEmail, customerPassword, "Demo", "Customer", Role.USER);
        seedDemoUser(adminEmail, adminPassword, "Demo", "Admin", Role.ADMIN);
    }

    private void seedDemoUser(String email, String rawPassword, String firstName, String lastName, Role role) {
        if (userRepository.existsByEmailId(email)) {
            log.info("Demo {} account already exists — skipping seed.", role);
            return;
        }

        // Build a minimal Cart and Wishlist so associations are valid
        Cart cart = Cart.builder().build();
        Wishlist wishlist = Wishlist.builder().products(new ArrayList<>()).build();

        UserInformation user = UserInformation.builder()
                .emailId(email)
                .firstName(firstName)
                .lastName(lastName)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .activeStatus(ActiveStatus.ACTIVE)
                .cart(cart)
                .wishlist(wishlist)
                .build();

        UserInformation savedUser = userRepository.save(user);

        // Create wallet linked to the saved user
        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(0);
        // Wallet is mapped by user (CascadeType.ALL), so save via user
        savedUser.setWallet(wallet);
        userRepository.save(savedUser);

        log.info("✅ Demo {} account created: {}", role, email);
    }
}
