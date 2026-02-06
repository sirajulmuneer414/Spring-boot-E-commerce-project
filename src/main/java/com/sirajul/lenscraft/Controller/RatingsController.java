package com.sirajul.lenscraft.Controller;

import com.sirajul.lenscraft.Service.interfaces.ProductService;
import com.sirajul.lenscraft.Service.interfaces.RatingsService;
import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.Ratings;
import com.sirajul.lenscraft.entity.user.UserInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/ratings")
@Slf4j
public class RatingsController {

    @Autowired
    private RatingsService ratingsService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    /**
     * Submit or update a rating for a product
     */
    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> submitRating(
            @RequestParam("productId") Long productId,
            @RequestParam("rating") Integer rating,
            @RequestParam(value = "description", required = false) String description) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Get authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            UserInformation user = userService.findByEmailId(username);

            if (user == null) {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Get product
            Product product = productService.findProductById(productId);
            if (product == null) {
                response.put("success", false);
                response.put("message", "Product not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Validate rating
            if (rating < 1 || rating > 5) {
                response.put("success", false);
                response.put("message", "Rating must be between 1 and 5");
                return ResponseEntity.badRequest().body(response);
            }

            // Save rating
            Ratings savedRating = ratingsService.addOrUpdateRating(user, product, rating, description);

            // Get updated statistics
            Double averageRating = ratingsService.getAverageRating(productId);
            Long ratingsCount = ratingsService.getRatingsCount(productId);

            response.put("success", true);
            response.put("message", "Rating submitted successfully");
            response.put("averageRating", averageRating);
            response.put("ratingsCount", ratingsCount);
            response.put("userRating", savedRating.getProductRating());

            log.info("Rating submitted: Product={}, User={}, Rating={}", productId, user.getUserId(), rating);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error submitting rating", e);
            response.put("success", false);
            response.put("message", "Error submitting rating: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get rating statistics for a product
     */
    @GetMapping("/product/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProductRatings(@PathVariable Long productId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Double averageRating = ratingsService.getAverageRating(productId);
            Long ratingsCount = ratingsService.getRatingsCount(productId);
            Map<Integer, Long> distribution = ratingsService.getRatingDistribution(productId);

            response.put("averageRating", averageRating);
            response.put("ratingsCount", ratingsCount);
            response.put("distribution", distribution);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching ratings for product: {}", productId, e);
            response.put("error", "Error fetching ratings");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Check if user has rated a product
     */
    @GetMapping("/check/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkUserRating(@PathVariable Long productId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            UserInformation user = userService.findByEmailId(username);

            if (user == null) {
                response.put("hasRated", false);
                return ResponseEntity.ok(response);
            }

            Product product = productService.findProductById(productId);
            Ratings userRating = ratingsService.getUserRatingForProduct(user, product);

            if (userRating != null) {
                response.put("hasRated", true);
                response.put("rating", userRating.getProductRating());
                response.put("description", userRating.getDescription());
            } else {
                response.put("hasRated", false);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error checking user rating for product: {}", productId, e);
            response.put("error", "Error checking rating");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
