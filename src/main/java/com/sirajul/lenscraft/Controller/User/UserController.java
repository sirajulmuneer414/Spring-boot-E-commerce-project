package com.sirajul.lenscraft.Controller.User;

import com.sirajul.lenscraft.Service.interfaces.*;
import com.sirajul.lenscraft.comparator.CartedItemComparator;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.Variables;
import com.sirajul.lenscraft.entity.user.*;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controller handling user-related operations: wishlist, cart, and address
 * management.
 * Checkout and payment operations are in UserCheckoutController.
 */
@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @Autowired
    VariableService variableService;

    @Autowired
    AddressService addressService;

    @Autowired
    CartedItemsService cartedItemsService;

    @Autowired
    CartService cartService;

    @GetMapping("/wishlist")
    public String getWishlist(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        UserInformation user = userService.findByEmailId(auth.getName());

        List<Product> products = user.getWishlist().getProducts();

        model.addAttribute("products", products);

        model.addAttribute("username", auth.getName());

        model.addAttribute("userId", user.getUserId());

        return "user/Wishlist";
    }

    @GetMapping("/wishlist/add/{productId}/{variableId}")
    public String addToWishlist(
            @PathVariable("productId") Long productId,
            @PathVariable("variableId") Long variableId,
            @RequestParam("userId") UUID userId

    ) {

        UserInformation user = userService.findById(userId);

        Product product = productService.findProductById(productId);

        Wishlist wishlist = user.getWishlist();

        wishlist.getProducts().add(product);

        user.setWishlist(wishlist);

        userService.save(user);

        return "redirect:/shop/productView/" + productId + "/" + variableId;
    }

    @GetMapping("/wishlist/remove/{productId}/{variableId}")
    public String removeFromWishlist(@RequestParam(name = "fromProduct", required = false) String fromProduct,
            @PathVariable("productId") Long productId,
            @PathVariable("variableId") Long variableId,
            @RequestParam("userId") UUID userId) {

        UserInformation user = userService.findById(userId);

        Product product = productService.findProductById(productId);

        user.getWishlist().getProducts().remove(product);

        userService.save(user);

        if (fromProduct != null) {

            return "redirect:/shop/productView/" + productId + "/" + variableId;
        }

        return "redirect:/user/wishlist";
    }

    @GetMapping("/cart")
    public String getCart(
            Model model,
            HttpSession session) {

        String emailId = SecurityContextHolder.getContext().getAuthentication().getName();

        UserInformation user = userService.findByEmailId(emailId);

        List<CartedItems> cartedItems = user.getCart().getCartedItems();

        if (cartedItems == null) {

            model.addAttribute("cartedItems", new ArrayList<>());
        } else {
            cartedItems.sort(new CartedItemComparator());

            model.addAttribute("cartedItems", cartedItems);
        }

        model.addAttribute("username", emailId);

        model.addAttribute("userId", user.getUserId());

        session.setAttribute("discount", 0);

        session.setAttribute("couponId", null);
        Integer totalAmount = 0;

        for (CartedItems item : cartedItems) {
            // Sync price with product's current discounted price
            if (!item.getCurrentPrice().equals(item.getProduct().getDiscountedPrice() * item.getQuantity())) {
                item.setCurrentPrice(item.getProduct().getDiscountedPrice() * item.getQuantity());
                cartedItemsService.saveCartedItem(item);
            }
            totalAmount += item.getCurrentPrice();

        }

        model.addAttribute("totalAmount", totalAmount);

        return "user/Cart";
    }

    @GetMapping("/cart/add/{productId}/{variableId}")
    public String addToCart(@PathVariable("productId") Long productId,
            @PathVariable("variableId") Long variableId) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInformation user = userService.findByEmailId(username);

        Product product = productService.findProductById(productId);

        Variables variable = variableService.findVariableById(variableId);

        Cart cart = user.getCart();

        CartedItems cartedItem = new CartedItems();

        cartedItem.setProduct(product);
        cartedItem.setVariable(variable);
        cartedItem.setCart(cart);

        cartedItem.setCurrentPrice(product.getDiscountedPrice());

        cartedItemsService.saveCartedItem(cartedItem);

        cart.getCartedItems().add(cartedItem);

        cartService.save(cart);

        user.setCart(cart);

        userService.save(user);

        return "redirect:/shop/productView/" + productId + "/" + variableId;
    }

    @GetMapping("/cart/remove/{productId}/{variableId}")
    public String removeFromCart(
            @PathVariable("productId") Long productId,
            @PathVariable("variableId") Long variableId,
            @RequestParam(name = "fromProduct", required = false) String fromProduct,
            @RequestParam("userId") UUID userId,
            @RequestParam("cartedItemId") Long CartedItemId) {

        UserInformation user = userService.findById(userId);

        Cart cart = user.getCart();

        CartedItems cartedItem = cartedItemsService.findById(CartedItemId);

        Variables variable = cartedItem.getVariable();
        variable.setQuantity(variable.getQuantity() + cartedItem.getQuantity());
        variableService.saveVariable(variable);

        cart.getCartedItems().remove(cartedItem);

        cartedItemsService.deleteCartedItem(cartedItem);

        user.setCart(cart);

        userService.save(user);

        if (fromProduct != null) {

            return "redirect:/shop/productView/" + productId + "/" + variableId;
        }

        return "redirect:/user/cart";

    }

    @GetMapping("/cart/quantity/add/{cartedItemId}")
    public String addQuantityInCart(
            @PathVariable("cartedItemId") Long cartedItemId,
            @RequestParam("username") String emailId) {

        CartedItems cartedItem = cartedItemsService.findById(cartedItemId);

        cartedItem.setQuantity(cartedItem.getQuantity() + 1);
        cartedItem.setCurrentPrice(cartedItem.getProduct().getDiscountedPrice() * cartedItem.getQuantity());
        Variables variable = cartedItem.getVariable();
        variable.setQuantity(variable.getQuantity() - 1);

        cartedItemsService.saveCartedItem(cartedItem);

        return "redirect:/user/cart";
    }

    @GetMapping("/cart/quantity/sub/{cartedItemId}")
    public String subQuantityInCart(
            @PathVariable("cartedItemId") Long cartedItemId,
            @RequestParam("username") String emailId) {

        UserInformation user = userService.findByEmailId(emailId);

        CartedItems cartedItem = cartedItemsService.findById(cartedItemId);

        cartedItem.setQuantity(cartedItem.getQuantity() - 1);
        cartedItem.setCurrentPrice(cartedItem.getProduct().getDiscountedPrice() * cartedItem.getQuantity());
        Variables variable = cartedItem.getVariable();

        variable.setQuantity(variable.getQuantity() + 1);

        variableService.saveVariable(variable);

        if (cartedItem.getQuantity() == 0) {

            user.getCart().getCartedItems().remove(cartedItem);
            cartedItemsService.deleteCartedItem(cartedItem);
        } else {
            cartedItemsService.saveCartedItem(cartedItem);
        }

        return "redirect:/user/cart";
    }

    // @GetMapping("/address")
    // public String getAddress(
    // Model model,
    // @RequestParam("username")String emailId
    // ){
    //
    //
    //
    // UserInformation user = userService.findByEmailId(emailId);
    //
    // List<Address> addresses = user.getAddresses();
    //
    // model.addAttribute("addresses",addresses);
    //
    // model.addAttribute("user",user);
    //
    // model.addAttribute("username",emailId);
    //
    // return "user/address";
    //
    // }

    @GetMapping("/address/add")
    public String addAddress(Model model,
            @RequestParam(name = "fromChange", required = false, defaultValue = "no") String change,
            HttpSession session) {

        Address address = new Address();

        model.addAttribute("address", address);
        if (change.matches("yes")) {
            model.addAttribute("change", true);
        }

        return "user/add-address";

    }

    @PostMapping("/address/add/post")
    public String postAddAddress(
            @ModelAttribute Address address,
            @RequestParam(name = "fromChange", required = false, defaultValue = "no") String change,
            HttpSession session) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInformation user = userService.findByEmailId(auth.getName());

        address.setUser(user);

        List<Address> addresses = user.getAddresses();

        if (addresses == null) {
            addresses = new ArrayList<>();
        }

        addresses.add(addressService.saveAndReturn(address));

        user.setAddresses(addresses);

        userService.save(user);

        if (change.matches("yes")) {

            return "redirect:/user/cart/buy/" + user.getUserId();
        }

        return "redirect:/user/profile";
    }

    @GetMapping("/address/delete/{addressId}")
    public String deleteAddress(
            @PathVariable("addressId") Long addressId,
            @RequestParam("userId") UUID userId) {
        Address address = addressService.findById(addressId);

        UserInformation user = userService.findById(userId);

        user.getAddresses().remove(address);

        addressService.delete(address);

        userService.save(user);

        return "redirect:/user/profile";
    }

    @GetMapping("/address/edit/{addressId}")
    public String getEditAddress(
            Model model,
            @PathVariable("addressId") Long addressId) {
        Address address = addressService.findById(addressId);

        model.addAttribute("address", address);

        model.addAttribute("username", address.getUser().getEmailId());

        return "user/edit-address";
    }

    @PostMapping("/address/edit/{addressId}")
    public String postEditAddress(
            @PathVariable("addressId") Long addressId,
            @ModelAttribute Address address,
            @RequestParam("userId") UUID userId) {
        Address addressToChange = addressService.findById(addressId);

        if (Objects.nonNull(address.getBuyerName()) && "" != address.getBuyerName()) {
            addressToChange.setBuyerName(address.getBuyerName());
        }

        if (Objects.nonNull(address.getHouseAddress()) && "" != address.getHouseAddress()) {
            addressToChange.setHouseAddress(address.getHouseAddress());
        }
        if (Objects.nonNull(address.getDistrict()) && "" != address.getDistrict()) {
            addressToChange.setDistrict(address.getDistrict());
        }
        if (Objects.nonNull(address.getState()) && "" != address.getState()) {
            addressToChange.setState(address.getState());
        }
        if (Objects.nonNull(address.getMobileNumber())) {
            addressToChange.setMobileNumber(address.getState());
        }
        if (Objects.nonNull(address.getPincode())) {
            addressToChange.setPincode(address.getPincode());
        }

        addressService.save(addressToChange);

        return "redirect:/user/profile";

    }

}
