package com.sirajul.lenscraft.Service.interfaces;

import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.Ratings;
import com.sirajul.lenscraft.entity.user.UserInformation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RatingsService {

    /**
     * Add or update a rating for a product by a user
     */
    Ratings addOrUpdateRating(UserInformation user, Product product, Integer rating, String description);

    /**
     * Get average rating for a product
     * 
     * @return average rating (0.0 to 5.0) or 0.0 if no ratings
     */
    Double getAverageRating(Long productId);

    /**
     * Get total count of ratings for a product
     */
    Long getRatingsCount(Long productId);

    /**
     * Get all ratings for a product
     */
    List<Ratings> getRatingsByProduct(Long productId);

    /**
     * Check if user has already rated a product
     */
    boolean hasUserRatedProduct(UUID userId, Long productId);

    /**
     * Get rating distribution (star count)
     * 
     * @return Map with keys 1-5 (stars) and values as count
     */
    Map<Integer, Long> getRatingDistribution(Long productId);

    /**
     * Get user's rating for a product
     */
    Ratings getUserRatingForProduct(UserInformation user, Product product);

    /**
     * Delete a rating
     */
    void deleteRating(UUID ratingId);

    /**
     * Get recent ratings for a product (latest 10)
     */
    List<Ratings> getRecentRatings(Long productId);

    /**
     * PERFORMANCE: Batch fetch average ratings for multiple products
     * 
     * @param productIds List of product IDs
     * @return Map of productId -> averageRating
     */
    Map<Long, Double> getAverageRatingsForProducts(List<Long> productIds);

    /**
     * PERFORMANCE: Batch fetch rating counts for multiple products
     * 
     * @param productIds List of product IDs
     * @return Map of productId -> ratingsCount
     */
    Map<Long, Long> getRatingsCountsForProducts(List<Long> productIds);
}
