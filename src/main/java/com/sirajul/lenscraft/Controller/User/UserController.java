package com.sirajul.lenscraft.Controller.User;

import com.razorpay.RazorpayClient;
import com.sirajul.lenscraft.DTO.Product.CouponDto;
import com.sirajul.lenscraft.DTO.ToPassBoolean;
import com.sirajul.lenscraft.DTO.order.OrderDto;
import com.sirajul.lenscraft.Service.interfaces.*;
import com.sirajul.lenscraft.comparator.CartedItemComparator;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.Variables;
import com.sirajul.lenscraft.entity.user.*;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Var;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {
@Autowired
UserService userService;

@Autowired
ReferralOfferService referralOfferService;

@Autowired
ProductService productService;

@Autowired
VariableService variableService;

@Autowired
AddressService addressService;

@Autowired
WishlistService wishlistService;

@Autowired
CartedItemsService cartedItemsService;

@Autowired
CartService cartService;

@Autowired
OrderService orderService;

@Autowired
CouponService couponService;


@GetMapping("/wishlist")
public String getWishlist(Model model){
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    UserInformation user = userService.findByEmailId(auth.getName());

    List<Product> products = user.getWishlist().getProducts();

    model.addAttribute("products",products);

    model.addAttribute("username",auth.getName());

    model.addAttribute("userId",user.getUserId());

    return "user/Wishlist";
}
@GetMapping("/wishlist/add/{productId}/{variableId}")
    public String addToWishlist(
                                @PathVariable("productId")Long productId,
                                @PathVariable("variableId")Long variableId,
                                @RequestParam("userId")UUID userId

){


    UserInformation user = userService.findById(userId);

    Product product = productService.findProductById(productId);

    Wishlist wishlist = user.getWishlist();

    wishlist.getProducts().add(product);

    user.setWishlist(wishlist);

    userService.save(user);

    return "redirect:/shop/productView/"+productId+"/"+variableId;
}



@GetMapping("/wishlist/remove/{productId}/{variableId}")
    public String removeFromWishlist(@RequestParam(name = "fromProduct", required = false)String fromProduct,
                                     @PathVariable("productId")Long productId,
                                     @PathVariable("variableId")Long variableId,
                                     @RequestParam("userId")UUID userId){

        UserInformation user = userService.findById(userId);

        Product product = productService.findProductById(productId);

        user.getWishlist().getProducts().remove(product);

        userService.save(user);

        if(fromProduct != null) {

            return "redirect:/shop/productView/"+productId+"/"+variableId;
        }

        return "redirect:/user/wishlist";
}

@GetMapping("/cart")
public String getCart(
        Model model,
        HttpSession session
){

    String emailId = SecurityContextHolder.getContext().getAuthentication().getName();

    UserInformation user = userService.findByEmailId(emailId);

    List<CartedItems> cartedItems = user.getCart().getCartedItems();

    if(cartedItems == null){

        model.addAttribute("cartedItems",new ArrayList<>());
    }
    else {
        cartedItems.sort(new CartedItemComparator());

        model.addAttribute("cartedItems", cartedItems);
    }

    model.addAttribute("username",emailId);

    model.addAttribute("userId",user.getUserId());

    session.setAttribute("discount",0);

    session.setAttribute("couponId",null);
    Integer totalAmount = 0;

    for(CartedItems item : cartedItems){

        totalAmount += item.getCurrentPrice();

    }

    model.addAttribute("totalAmount",totalAmount);

    return "user/Cart";
}
@GetMapping("/cart/add/{productId}/{variableId}")
    public String addToCart(@PathVariable("productId")Long productId,
                            @PathVariable("variableId")Long variableId,
                            @RequestParam("username")String username){

    UserInformation user = userService.findByEmailId(username);

    Product product = productService.findProductById(productId);

    Variables variable = variableService.findVariableById(variableId);

    Cart cart = user.getCart();

    CartedItems  cartedItem = new CartedItems();

    cartedItem.setProduct(product);
    cartedItem.setVariable(variable);
    cartedItem.setCart(cart);

    cartedItem.setCurrentPrice(product.getDiscountedPrice());

    cartedItemsService.saveCartedItem(cartedItem);

    cart.getCartedItems().add(cartedItem);

    cartService.save(cart);

    user.setCart(cart);

    userService.save(user);

    return "redirect:/shop/productView/"+productId+"/"+variableId;
}

@GetMapping("/cart/remove/{productId}/{variableId}")
    public String removeFromCart(
            @PathVariable("productId")Long productId,
            @PathVariable("variableId")Long variableId,
            @RequestParam(name = "fromProduct", required = false)String fromProduct,
            @RequestParam("userId") UUID userId,
            @RequestParam("cartedItemId") Long CartedItemId
){


    UserInformation user = userService.findById(userId);

    Cart cart = user.getCart();

    CartedItems cartedItem = cartedItemsService.findById(CartedItemId);


    Variables variable = cartedItem.getVariable();
    variable.setQuantity(variable.getQuantity()+cartedItem.getQuantity());
    variableService.saveVariable(variable);

    cart.getCartedItems().remove(cartedItem);


    cartedItemsService.deleteCartedItem(cartedItem);

    user.setCart(cart);

    userService.save(user);


    if(fromProduct != null) {

        return "redirect:/shop/productView/"+productId+"/"+variableId;
    }

    return "redirect:/user/cart";

}
@PostMapping("/cart/offer/apply")
    public String applyOffer(
        @RequestParam("couponId")UUID couponId,
        @RequestParam("userId")UUID userId,
        HttpSession session

        ){

    Integer discount =  (Integer) session.getAttribute("discount");

    Coupon coupon = couponService.findCouponById(couponId);

    discount = coupon.getDiscountPercentage();

    session.setAttribute("discount", discount);

    UUID couponIdDto = (UUID) session.getAttribute("couponId");

    couponIdDto = couponId;

    session.setAttribute("couponId",couponId);

    return "redirect:/user/cart/buy/"+userId;


}
@GetMapping("/cart/quantity/add/{cartedItemId}")
    public String addQuantityInCart(
            @PathVariable("cartedItemId")Long cartedItemId,
            @RequestParam("username")String emailId
) {

    CartedItems cartedItem = cartedItemsService.findById(cartedItemId);

    cartedItem.setQuantity(cartedItem.getQuantity() + 1);
    cartedItem.setCurrentPrice(cartedItem.getProduct().getDiscountedPrice() * cartedItem.getQuantity());
    Variables variable = cartedItem.getVariable();
    variable.setQuantity(variable.getQuantity()-1);

    cartedItemsService.saveCartedItem(cartedItem);

    return "redirect:/user/cart";
}
@GetMapping("/cart/quantity/sub/{cartedItemId}")
    public String subQuantityInCart(
            @PathVariable("cartedItemId")Long cartedItemId,
            @RequestParam("username")String emailId
){

    UserInformation user = userService.findByEmailId(emailId);

    CartedItems cartedItem = cartedItemsService.findById(cartedItemId);

    cartedItem.setQuantity(cartedItem.getQuantity()-1);
    cartedItem.setCurrentPrice(cartedItem.getProduct().getDiscountedPrice() * cartedItem.getQuantity());
    Variables variable = cartedItem.getVariable();

    variable.setQuantity(variable.getQuantity()+1);

    variableService.saveVariable(variable);

    if(cartedItem.getQuantity() == 0){

        user.getCart().getCartedItems().remove(cartedItem);
        cartedItemsService.deleteCartedItem(cartedItem);
    }
    else {
        cartedItemsService.saveCartedItem(cartedItem);
    }

    return "redirect:/user/cart";
}
@GetMapping("/cart/buy/{userId}")
    public String buyFromCart(
            HttpSession session,
            Model model,
            @PathVariable("userId")UUID userId
){
    UserInformation user = userService.findById(userId);
    Cart cart = user.getCart();
    List<CartedItems> items = cart.getCartedItems();

    Integer discount = (Integer) session.getAttribute("discount");

    model.addAttribute("delivery",40);
    model.addAttribute("username",user.getEmailId());
    model.addAttribute("userId",userId);
    model.addAttribute("items",items);

    UUID couponIdDto = (UUID) session.getAttribute("couponId");

    OrderDto orderDto = new OrderDto();
    orderDto.setUserId(user.getUserId());
    orderDto.setItemsToBuy(items);
    Integer totalAmount = 0;

    for(CartedItems item : items){

        totalAmount += item.getCurrentPrice();

    }
    totalAmount += 40;

    int discountedPrice = 0;
    if(couponIdDto != null){
        orderDto.setCouponId(couponIdDto);

       CouponDto coupon = couponService.findCouponByIdDto(couponIdDto);

        model.addAttribute("couponSelected",coupon);

        discountedPrice = coupon.getDiscountPercentage();
    }else{
        model.addAttribute("couponSelected",null);
    }



    totalAmount -= discountedPrice;

    model.addAttribute("discount",discountedPrice);

    orderDto.setTotalAmount(totalAmount);



    session.setAttribute("order",orderDto);

    List<CouponDto> couponDtos = couponService.findCouponsForUser(user,totalAmount);



    model.addAttribute("orderDto",orderDto);
    model.addAttribute("coupons",couponDtos);

    return "buy/order-summary";
}

    @PostMapping("/payment/razor")
    @ResponseBody
    public ResponseEntity<String> createOrder (HttpSession session) throws Exception{

        OrderDto dto = (OrderDto) session.getAttribute("order");

        int amount = dto.getTotalAmount();

        session.setAttribute("order",dto);
        var client=new RazorpayClient("rzp_test_3BnqGyxnP22wsH","WrGbzmVjQ5QriEPZXbkw4A4I");
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount",amount*100);
        orderRequest.put("currency","INR");
        orderRequest.put("receipt", "txn_2345433");
        com.razorpay.Order order = client.orders.create(orderRequest);
        return new ResponseEntity<>(order.toString(),HttpStatus.OK);
    }
@GetMapping("/buy/address/change")
    public String addAddressToOrder(
            Model model,
            HttpSession session

){

    OrderDto orderDto = (OrderDto) session.getAttribute("order");

    UserInformation user = userService.findById(orderDto.getUserId());

    List<Address> addresses = user.getAddresses();

    List<Address> addressToShow = new ArrayList<>();

    for(Address add : addresses){
        if(add.isActive()){
            addressToShow.add(add);
        }
    }

    String username = user.getEmailId();

    model.addAttribute("username",username);

    model.addAttribute("addresses",addressToShow);

    session.setAttribute("order",orderDto);

    return "buy/change-address";
}
@PostMapping("/buy/address/change")
    public String postChangeAddress(
            HttpSession session,
            @RequestParam("addressId") Long addressId,
            @RequestParam("username") String username
){

    OrderDto orderDto = (OrderDto) session.getAttribute("order");

    orderDto.setAddressId(addressId);

    session.setAttribute("order",orderDto);


    return "redirect:/user/cart/buy/payment";
}

@GetMapping("/cart/buy/payment")
    public String getPayment(Model model,
                             HttpSession session){

    OrderDto dto = (OrderDto) session.getAttribute("order");

    Integer totalAmount = dto.getTotalAmount();

    model.addAttribute("totalAmount", totalAmount);

    session.setAttribute("order",dto);
    return "buy/payment";
}
@PostMapping("/cart/buy/payment")
    public String postPayment(
            @RequestParam("paymentType") String paymentType,
            HttpSession session
){
    OrderDto orderDto = (OrderDto) session.getAttribute("order");

    orderDto.setPaymentType(paymentType);

    session.setAttribute("order",orderDto);

    return "redirect:/user/cart/buy/confirm";
}
@GetMapping("/cart/buy/confirm")
    public String confirmOrder(
            Model model,
            HttpSession session
){

    OrderDto orderDto = (OrderDto) session.getAttribute("order");

    System.out.println(orderDto.getAddressId());
    System.out.println(orderDto.getPaymentType());
    System.out.println(orderDto.getTotalAmount());

    Order order = orderService.saveAndReturn(orderDto);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    UserInformation user = userService.findByEmailId(auth.getName());

    user.getOrders().add(order);


    Cart cart = user.getCart();

    List<CartedItems> cartedItems = new ArrayList<>(cart.getCartedItems());
    Iterator<CartedItems> iterator = cartedItems.iterator();

    while (iterator.hasNext()) {
        CartedItems items = iterator.next();
        cartedItemsService.deleteCartedItem(items);
        iterator.remove(); // Use iterator's remove method to avoid ConcurrentModificationException
    }

    cart.setCartedItems(cartedItems); // Update the cart with the modified list

    user.setCart(cart);

    userService.save(user);

    model.addAttribute("userId",user.getUserId());
    model.addAttribute("username",auth.getName());

    return "buy/order-confirmed";
}
//@GetMapping("/address")
//    public String getAddress(
//            Model model,
//            @RequestParam("username")String emailId
//){
//
//
//
//    UserInformation user = userService.findByEmailId(emailId);
//
//    List<Address> addresses = user.getAddresses();
//
//    model.addAttribute("addresses",addresses);
//
//    model.addAttribute("user",user);
//
//    model.addAttribute("username",emailId);
//
//    return "user/address";
//
//}

@GetMapping("/address/add")
    public String addAddress(Model model,
                             @RequestParam(name = "fromChange", required = false,defaultValue = "no")String change,
                             HttpSession session){

    Address address = new Address();

    model.addAttribute("address",address);
    if(change.matches("yes")){
      model.addAttribute("change",true);
    }

    return "user/add-address";

}

@PostMapping("/address/add/post")
    public String postAddAddress(
            @ModelAttribute Address address,
            @RequestParam(name = "fromChange", required = false,defaultValue = "no")String change,
            HttpSession session
){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInformation user = userService.findByEmailId(auth.getName());

        address.setUser(user);

        List<Address> addresses = user.getAddresses();

        if(addresses == null){
            addresses = new ArrayList<>();
        }

        addresses.add(addressService.saveAndReturn(address));

        user.setAddresses(addresses);

        userService.save(user);

        if(change.matches("yes")){

            return "redirect:/user/cart/buy/"+user.getUserId();
        }

        return "redirect:/user/profile";
}
@GetMapping("/address/delete/{addressId}")
    public String deleteAddress(
            @PathVariable("addressId")Long addressId,
            @RequestParam("userId")UUID userId
){
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
            @PathVariable("addressId")Long addressId
){
    Address address = addressService.findById(addressId);

    model.addAttribute("address",address);

    model.addAttribute("username",address.getUser().getEmailId());

    return "user/edit-address";
}

@PostMapping("/address/edit/{addressId}")
    public String postEditAddress(
            @PathVariable("addressId")Long addressId,
            @ModelAttribute Address address,
            @RequestParam("userId")UUID userId
){
    Address addressToChange = addressService.findById(addressId);

    if(Objects.nonNull(address.getBuyerName()) && "" != address.getBuyerName()){
        addressToChange.setBuyerName(address.getBuyerName());
    }

    if(Objects.nonNull(address.getHouseAddress()) && "" != address.getHouseAddress()){
        addressToChange.setHouseAddress(address.getHouseAddress());
    }
    if(Objects.nonNull(address.getDistrict()) && ""!=address.getDistrict()){
        addressToChange.setDistrict(address.getDistrict());
    }
    if(Objects.nonNull(address.getState()) && ""!=address.getState()){
        addressToChange.setState(address.getState());
    }
    if(Objects.nonNull(address.getMobileNumber())){
        addressToChange.setMobileNumber(address.getState());
    }
    if(Objects.nonNull(address.getPincode())){
        addressToChange.setPincode(address.getPincode());
    }

    addressService.save(addressToChange);

    return "redirect:/user/profile";

}


}
