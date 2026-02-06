package com.sirajul.lenscraft.Repository;

import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.Ratings;
import com.sirajul.lenscraft.entity.user.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingsRepository extends JpaRepository<Ratings, UUID> {

    /**
     * Find all ratings for a specific product
     */
    List<Ratings> findByProduct(Product product);

    /**
     * Find all ratings for a specific product by productId
     */
    @Query("SELECT r FROM Ratings r WHERE r.product.productId = :productId")
    List<Ratings> findByProductId(@Param("productId") Long productId);

    /**
     * Calculate average rating for a product
     */
    @Query("SELECT AVG(r.productRating) FROM Ratings r WHERE r.product.productId = :productId")
    Double getAverageRatingByProductId(@Param("productId") Long productId);

    /**
     * Count total ratings for a product
     */
    @Query("SELECT COUNT(r) FROM Ratings r WHERE r.product.productId = :productId")
    Long countRatingsByProductId(@Param("productId") Long productId);

    /**
     * Find rating by user and product (to check if user already rated)
     */
    Optional<Ratings> findByUserAndProduct(UserInformation user, Product product);

    /**
     * Check if user has already rated a product
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Ratings r WHERE r.user.userId = :userId AND r.product.productId = :productId")
    boolean existsByUserIdAndProductId(@Param("userId") UUID userId, @Param("productId") Long productId);

    /**
     * Get ratings count by star value for a product
     */
    @Query("SELECT r.productRating, COUNT(r) FROM Ratings r WHERE r.product.productId = :productId GROUP BY r.productRating ORDER BY r.productRating DESC")
    List<Object[]> getRatingDistributionByProductId(@Param("productId") Long productId);

    /**
     * Find recent ratings for a product (limit 10)
     */
    @Query("SELECT r FROM Ratings r WHERE r.product.productId = :productId ORDER BY r.ratingId DESC")
    List<Ratings> findRecentRatingsByProductId(@Param("productId") Long productId);
}
