package com.sirajul.lenscraft.Controller.User;

import com.razorpay.RazorpayClient;
import com.sirajul.lenscraft.DTO.Product.CouponDto;
import com.sirajul.lenscraft.DTO.order.OrderDto;
import com.sirajul.lenscraft.Service.interfaces.*;
import com.sirajul.lenscraft.comparator.CartedItemComparator;
import com.sirajul.lenscraft.entity.user.*;
import com.sirajul.lenscraft.entity.user.enums.PaymentType;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

/**
 * Controller handling all checkout and payment related operations.
 */
@Controller
@RequestMapping("/user")
@Slf4j
public class UserCheckoutController {

    @Autowired
    private UserService userService;

    @Autowired
    private CartedItemsService cartedItemsService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PaymentService paymentService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    // ==================== COUPON ====================

    @PostMapping("/cart/offer/apply")
    public String applyOffer(
            @RequestParam("couponId") UUID couponId,
            @RequestParam("userId") UUID userId,
            HttpSession session) {

        Coupon coupon = couponService.findCouponById(couponId);
        Integer discount = coupon.getDiscountPercentage();

        session.setAttribute("discount", discount);
        session.setAttribute("couponId", couponId);

        return "redirect:/user/cart/buy/" + userId;
    }

    // ==================== ORDER SUMMARY ====================

    @GetMapping("/cart/buy/{userId}")
    public String buyFromCart(
            HttpSession session,
            Model model,
            @PathVariable("userId") UUID userId) {

        UserInformation user = userService.findById(userId);
        Cart cart = user.getCart();
        List<CartedItems> items = cart.getCartedItems();

        Integer discount = (Integer) session.getAttribute("discount");
        if (discount == null)
            discount = 0;

        model.addAttribute("delivery", 40);
        model.addAttribute("username", user.getEmailId());
        model.addAttribute("userId", userId);
        model.addAttribute("items", items);

        UUID couponIdDto = (UUID) session.getAttribute("couponId");

        OrderDto orderDto = new OrderDto();
        orderDto.setUserId(user.getUserId());
        orderDto.setItemsToBuy(items);
        Integer totalAmount = 0;

        for (CartedItems item : items) {
            // Sync price with product's current discounted price
            if (!item.getCurrentPrice().equals(item.getProduct().getDiscountedPrice() * item.getQuantity())) {
                item.setCurrentPrice(item.getProduct().getDiscountedPrice() * item.getQuantity());
                cartedItemsService.saveCartedItem(item);
            }
            totalAmount += item.getCurrentPrice();
        }
        totalAmount += 40; // Delivery charge

        int discountedPrice = 0;
        if (couponIdDto != null) {
            orderDto.setCouponId(couponIdDto);
            CouponDto coupon = couponService.findCouponByIdDto(couponIdDto);
            model.addAttribute("couponSelected", coupon);
            discountedPrice = (totalAmount * coupon.getDiscountPercentage()) / 100;
        } else {
            model.addAttribute("couponSelected", null);
        }

        totalAmount -= discountedPrice;
        model.addAttribute("discount", discountedPrice);

        orderDto.setTotalAmount(totalAmount);
        session.setAttribute("order", orderDto);

        List<CouponDto> couponDtos = couponService.findCouponsForUser(user, totalAmount);
        model.addAttribute("orderDto", orderDto);
        model.addAttribute("coupons", couponDtos);

        return "buy/order-summary";
    }

    // ==================== ADDRESS ====================

    @GetMapping("/buy/address/change")
    public String addAddressToOrder(Model model, HttpSession session) {
        OrderDto orderDto = (OrderDto) session.getAttribute("order");

        if (orderDto == null) {
            return "redirect:/user/cart";
        }

        UserInformation user = userService.findById(orderDto.getUserId());
        List<Address> addresses = user.getAddresses();

        List<Address> addressToShow = new ArrayList<>();
        for (Address add : addresses) {
            if (add.isActive()) {
                addressToShow.add(add);
            }
        }

        model.addAttribute("username", user.getEmailId());
        model.addAttribute("addresses", addressToShow);
        session.setAttribute("order", orderDto);

        return "buy/change-address";
    }

    @PostMapping("/buy/address/change")
    public String postChangeAddress(
            HttpSession session,
            @RequestParam(name = "addressId", required = false) Long addressId,
            @RequestParam("username") String username,
            Model model,
            RedirectAttributes redirectAttributes) {

        OrderDto orderDto = (OrderDto) session.getAttribute("order");
        if (orderDto == null) {
            return "redirect:/user/cart";
        }

        if (addressId == null) {
            redirectAttributes.addFlashAttribute("error", "Please select a delivery address or add a new one.");
            return "redirect:/user/buy/address/change";
        }

        orderDto.setAddressId(addressId);
        session.setAttribute("order", orderDto);

        return "redirect:/user/cart/buy/payment";
    }

    // ==================== PAYMENT ====================

    @GetMapping("/cart/buy/payment")
    public String getPayment(Model model, HttpSession session) {
        OrderDto dto = (OrderDto) session.getAttribute("order");
        if (dto == null) {
            return "redirect:/user/cart";
        }

        Integer totalAmount = dto.getTotalAmount();

        UserInformation user = userService.findById(dto.getUserId());
        if (user.getWallet() == null) {
            model.addAttribute("walletBalance", 0);
        } else {
            model.addAttribute("walletBalance", user.getWallet().getBalance());
        }

        model.addAttribute("totalAmount", totalAmount);
        session.setAttribute("order", dto);

        return "buy/payment";
    }

    @PostMapping("/cart/buy/payment")
    public String postPayment(
            @RequestParam(name = "paymentType", required = false) String paymentType,
            @RequestParam(name = "useWallet", defaultValue = "false") boolean useWallet,
            HttpSession session) {

        OrderDto orderDto = (OrderDto) session.getAttribute("order");
        if (orderDto == null) {
            return "redirect:/user/cart";
        }

        orderDto.setPaymentType(paymentType);
        orderDto.setUseWallet(useWallet);
        session.setAttribute("order", orderDto);

        return "redirect:/user/cart/buy/confirm";
    }

    // ==================== RAZORPAY ====================

    @PostMapping("/payment/razor")
    @ResponseBody
    public ResponseEntity<String> createRazorpayOrder(
            @RequestParam(name = "useWallet", defaultValue = "false") boolean useWallet,
            HttpSession session) throws Exception {

        OrderDto dto = (OrderDto) session.getAttribute("order");
        if (dto == null) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Session expired or invalid order.");
            return new ResponseEntity<>(errorResponse.toString(), HttpStatus.BAD_REQUEST);
        }

        int amount = dto.getTotalAmount();
        int walletDeduction = 0;

        if (useWallet) {
            UserInformation user = userService.findById(dto.getUserId());
            if (user.getWallet() != null && user.getWallet().getBalance() > 0) {
                walletDeduction = Math.min(user.getWallet().getBalance(), amount);
                amount = amount - walletDeduction;
            }
        }

        // Store wallet deduction in session for later use
        session.setAttribute("walletDeduction", walletDeduction);
        session.setAttribute("order", dto);

        var client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // Razorpay expects amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "order_" + System.currentTimeMillis());
        com.razorpay.Order order = client.orders.create(orderRequest);

        // Add the Razorpay key to the response
        JSONObject response = new JSONObject(order.toString());
        response.put("key", razorpayKeyId);
        response.put("walletDeduction", walletDeduction);

        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }

    // ==================== CONFIRM PAYMENT ====================

    @PostMapping("/cart/buy/confirm")
    @ResponseBody
    public ResponseEntity<String> confirmRazorpayPayment(
            @RequestParam(name = "razorpay_payment_id", required = false) String paymentId,
            @RequestParam(name = "razorpay_order_id", required = false) String orderId,
            @RequestParam(name = "razorpay_signature", required = false) String signature,
            @RequestParam(name = "paymentType") String paymentType,
            HttpSession session) {

        OrderDto orderDto = (OrderDto) session.getAttribute("order");
        if (orderDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order not found in session");
        }

        // Update payment type
        orderDto.setPaymentType(paymentType);

        // Store Razorpay details in session
        session.setAttribute("order", orderDto);
        session.setAttribute("razorpay_payment_id", paymentId);
        session.setAttribute("razorpay_order_id", orderId);
        session.setAttribute("razorpay_signature", signature);

        return ResponseEntity.ok("Success");
    }

    @GetMapping("/cart/buy/confirm")
    public String confirmOrder(Model model, HttpSession session) {
        OrderDto orderDto = (OrderDto) session.getAttribute("order");
        if (orderDto == null) {
            return "redirect:/user/cart";
        }

        log.info("Confirming order - Address: {}, PaymentType: {}, TotalAmount: {}",
                orderDto.getAddressId(), orderDto.getPaymentType(), orderDto.getTotalAmount());

        // Create the order first
        Order order = orderService.saveAndReturn(orderDto);

        // Handle payment based on type
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInformation user = userService.findByEmailId(auth.getName());

        String paymentType = orderDto.getPaymentType();
        String razorpayPaymentId = (String) session.getAttribute("razorpay_payment_id");
        String razorpayOrderId = (String) session.getAttribute("razorpay_order_id");
        String razorpaySignature = (String) session.getAttribute("razorpay_signature");
        Integer walletDeduction = (Integer) session.getAttribute("walletDeduction");

        if ("WALLET_PLUS_UPI".equals(paymentType) && walletDeduction != null && walletDeduction > 0) {
            // Wallet + UPI payment
            paymentService.processWalletPlusUpiPayment(order, user, walletDeduction,
                    razorpayPaymentId, razorpayOrderId, razorpaySignature);
            orderService.save(order);
        } else if ("WALLET".equals(paymentType)) {
            // Full wallet payment
            paymentService.processFullWalletPayment(order, user);
            orderService.save(order);
        } else if ("UPI PAYMENT".equals(paymentType)) {
            // UPI only payment
            paymentService.processUpiPayment(order, razorpayPaymentId, razorpayOrderId, razorpaySignature);
            orderService.save(order);
        }
        // COD doesn't need special processing

        // Add order to user and clear cart
        user.getOrders().add(order);
        Cart cart = user.getCart();

        List<CartedItems> cartedItems = new ArrayList<>(cart.getCartedItems());
        for (CartedItems item : cartedItems) {
            cartedItemsService.deleteCartedItem(item);
        }
        cart.setCartedItems(new ArrayList<>());
        user.setCart(cart);
        userService.save(user);

        // Clear session attributes
        session.removeAttribute("order");
        session.removeAttribute("discount");
        session.removeAttribute("couponId");
        session.removeAttribute("razorpay_payment_id");
        session.removeAttribute("razorpay_order_id");
        session.removeAttribute("razorpay_signature");
        session.removeAttribute("walletDeduction");

        model.addAttribute("userId", user.getUserId());
        model.addAttribute("username", auth.getName());

        return "buy/order-confirmed";
    }

    // ==================== WALLET + UPI CONFIRM ====================

    @PostMapping("/payment/wallet-plus-upi/confirm")
    @ResponseBody
    public ResponseEntity<String> confirmWalletPlusUpiPayment(
            @RequestParam("razorpay_payment_id") String paymentId,
            @RequestParam("razorpay_order_id") String orderId,
            @RequestParam("razorpay_signature") String signature,
            HttpSession session) {

        OrderDto orderDto = (OrderDto) session.getAttribute("order");
        if (orderDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order not found in session");
        }

        // Set payment type to WALLET_PLUS_UPI
        orderDto.setPaymentType("WALLET_PLUS_UPI");

        // Store details in session
        session.setAttribute("order", orderDto);
        session.setAttribute("razorpay_payment_id", paymentId);
        session.setAttribute("razorpay_order_id", orderId);
        session.setAttribute("razorpay_signature", signature);

        return ResponseEntity.ok("Success");
    }
}
