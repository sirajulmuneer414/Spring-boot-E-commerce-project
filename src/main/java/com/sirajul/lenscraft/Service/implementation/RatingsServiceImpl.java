package com.sirajul.lenscraft.Service.implementation;

import com.sirajul.lenscraft.Repository.RatingsRepository;
import com.sirajul.lenscraft.Service.interfaces.RatingsService;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.Ratings;
import com.sirajul.lenscraft.entity.user.UserInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@Transactional
public class RatingsServiceImpl implements RatingsService {

    @Autowired
    private RatingsRepository ratingsRepository;

    @Override
    public Ratings addOrUpdateRating(UserInformation user, Product product, Integer rating, String description) {
        log.info("Adding/Updating rating for product: {} by user: {}", product.getProductId(), user.getUserId());

        // Validate rating value
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // Check if user has already rated this product
        Optional<Ratings> existingRating = ratingsRepository.findByUserAndProduct(user, product);

        Ratings ratingEntity;
        if (existingRating.isPresent()) {
            // Update existing rating
            ratingEntity = existingRating.get();
            ratingEntity.setProductRating(rating);
            ratingEntity.setDescription(description);
            log.info("Updating existing rating with ID: {}", ratingEntity.getRatingId());
        } else {
            // Create new rating
            ratingEntity = Ratings.builder()
                    .user(user)
                    .product(product)
                    .productRating(rating)
                    .description(description)
                    .build();
            log.info("Creating new rating");
        }

        return ratingsRepository.save(ratingEntity);
    }

    @Override
    public Double getAverageRating(Long productId) {
        log.debug("Calculating average rating for product: {}", productId);
        Double average = ratingsRepository.getAverageRatingByProductId(productId);
        return average != null ? Math.round(average * 10.0) / 10.0 : 0.0; // Round to 1 decimal place
    }

    @Override
    public Long getRatingsCount(Long productId) {
        log.debug("Getting ratings count for product: {}", productId);
        return ratingsRepository.countRatingsByProductId(productId);
    }

    @Override
    public List<Ratings> getRatingsByProduct(Long productId) {
        log.debug("Getting all ratings for product: {}", productId);
        return ratingsRepository.findByProductId(productId);
    }

    @Override
    public boolean hasUserRatedProduct(UUID userId, Long productId) {
        return ratingsRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    public Map<Integer, Long> getRatingDistribution(Long productId) {
        log.debug("Getting rating distribution for product: {}", productId);

        // Initialize map with all star ratings (1-5) set to 0
        Map<Integer, Long> distribution = new LinkedHashMap<>();
        for (int i = 5; i >= 1; i--) {
            distribution.put(i, 0L);
        }

        // Get actual distribution from database
        List<Object[]> results = ratingsRepository.getRatingDistributionByProductId(productId);

        // Update map with actual counts
        for (Object[] result : results) {
            Integer stars = (Integer) result[0];
            Long count = (Long) result[1];
            distribution.put(stars, count);
        }

        return distribution;
    }

    @Override
    public Ratings getUserRatingForProduct(UserInformation user, Product product) {
        log.debug("Getting user rating for product: {} by user: {}", product.getProductId(), user.getUserId());
        return ratingsRepository.findByUserAndProduct(user, product).orElse(null);
    }

    @Override
    public void deleteRating(UUID ratingId) {
        log.info("Deleting rating with ID: {}", ratingId);
        ratingsRepository.deleteById(ratingId);
    }

    @Override
    public List<Ratings> getRecentRatings(Long productId) {
        log.debug("Getting recent ratings for product: {}", productId);
        List<Ratings> allRatings = ratingsRepository.findRecentRatingsByProductId(productId);

        // Limit to 10 most recent
        if (allRatings.size() > 10) {
            return allRatings.subList(0, 10);
        }
        return allRatings;
    }

    @Override
    public Map<Long, Double> getAverageRatingsForProducts(List<Long> productIds) {
        log.debug("Batch fetching average ratings for {} products", productIds.size());

        Map<Long, Double> ratingsMap = new HashMap<>();

        // Initialize all products with 0.0 rating
        for (Long productId : productIds) {
            ratingsMap.put(productId, 0.0);
        }

        // Fetch all ratings for these products in ONE query
        List<Ratings> allRatings = ratingsRepository.findAll();

        // Group by product and calculate averages
        Map<Long, List<Integer>> productRatingsMap = new HashMap<>();
        for (Ratings rating : allRatings) {
            Long productId = rating.getProduct().getProductId();
            if (productIds.contains(productId)) {
                productRatingsMap.computeIfAbsent(productId, k -> new ArrayList<>())
                        .add(rating.getProductRating());
            }
        }

        // Calculate averages
        for (Map.Entry<Long, List<Integer>> entry : productRatingsMap.entrySet()) {
            List<Integer> ratings = entry.getValue();
            if (!ratings.isEmpty()) {
                double average = ratings.stream()
                        .mapToInt(Integer::intValue)
                        .average()
                        .orElse(0.0);
                ratingsMap.put(entry.getKey(), Math.round(average * 10.0) / 10.0);
            }
        }

        return ratingsMap;
    }

    @Override
    public Map<Long, Long> getRatingsCountsForProducts(List<Long> productIds) {
        log.debug("Batch fetching rating counts for {} products", productIds.size());

        Map<Long, Long> countsMap = new HashMap<>();

        // Initialize all products with 0 count
        for (Long productId : productIds) {
            countsMap.put(productId, 0L);
        }

        // Fetch all ratings for these products
        List<Ratings> allRatings = ratingsRepository.findAll();

        // Count ratings per product
        for (Ratings rating : allRatings) {
            Long productId = rating.getProduct().getProductId();
            if (productIds.contains(productId)) {
                countsMap.merge(productId, 1L, Long::sum);
            }
        }

        return countsMap;
    }
}
