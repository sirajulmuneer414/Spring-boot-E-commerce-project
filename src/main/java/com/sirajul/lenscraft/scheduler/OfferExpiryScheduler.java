package com.sirajul.lenscraft.scheduler;

import com.sirajul.lenscraft.Repository.CategoryRepository;
import com.sirajul.lenscraft.Repository.ProductRepository;
import com.sirajul.lenscraft.entity.offer.OfferEmbeddable;
import com.sirajul.lenscraft.entity.product.Category;
import com.sirajul.lenscraft.entity.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class OfferExpiryScheduler {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at midnight
    @Transactional
    public void checkOfferExpiry() {
        LocalDate today = LocalDate.now();

        // 1. Check Product Offers
        List<Product> productsWithOffers = productRepository.findAllByIsHavingOfferTrue();
        for (Product product : productsWithOffers) {
            // Check if end date exists and is before today (yesterday was last valid day)
            // If end date is today, it is still valid until midnight, so we check if
            // strictly before today
            if (product.getOffer().getEndDate() != null && product.getOffer().getEndDate().isBefore(today)) {

                // Remove product offer
                product.setOffer(new OfferEmbeddable());
                product.setHavingOffer(false);

                // Check for fallback to Category Offer
                if (product.getCategory().isHavingOffer()) {
                    // Fallback to active category offer
                    int discountedPrice = (product.getPrice()
                            - (product.getPrice() * product.getCategory().getOffer().getOfferPercentage() / 100));
                    product.setDiscountedPrice(discountedPrice);
                } else {
                    // No category offer, revert to original price
                    product.setDiscountedPrice(product.getPrice());
                }
                productRepository.save(product);
                System.out.println("Scheduler: Expired offer removed for product: " + product.getProductName());
            }
        }

        // 2. Check Category Offers
        List<Category> categoriesWithOffers = categoryRepository.findAllByIsHavingOfferTrue();
        for (Category category : categoriesWithOffers) {
            if (category.getOffer().getEndDate() != null && category.getOffer().getEndDate().isBefore(today)) {

                // Remove category offer
                category.setOffer(new OfferEmbeddable());
                category.setHavingOffer(false);
                categoryRepository.save(category);

                // Revert prices for all products in this category (unless they have their own
                // offer)
                List<Product> products = category.getProducts();
                for (Product product : products) {
                    if (!product.isHavingOffer()) { // Only affect products relying on category offer
                        product.setDiscountedPrice(product.getPrice());
                        productRepository.save(product);
                    }
                }
                System.out.println("Scheduler: Expired offer removed for category: " + category.getCategoryName());
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at midnight
    @Transactional
    public void startPendingOffers() {
        LocalDate today = LocalDate.now();

        // 1. Activate Product Offers
        List<Product> productsWithOffers = productRepository.findAllByIsHavingOfferTrue();
        for (Product product : productsWithOffers) {
            // Check if offer is scheduled for today (start date is today or before)
            if (product.getOffer().getStartDate() != null && !product.getOffer().getStartDate().isAfter(today) &&
                    (product.getOffer().getEndDate() == null || !product.getOffer().getEndDate().isBefore(today))) {

                // If discounted price is not applied correctly (e.g. it was saved as pending
                // future offer)
                int calculatedDiscount = (product.getPrice()
                        - (product.getPrice() * product.getOffer().getOfferPercentage() / 100));

                // If current discounted price is not what it should be, update it
                // This covers cases where it was 0 or full price because start date was in
                // future
                if (product.getDiscountedPrice() != calculatedDiscount) {
                    product.setDiscountedPrice(calculatedDiscount);
                    productRepository.save(product);
                    System.out.println("Scheduler: Activated pending offer for product: " + product.getProductName());
                }
            }
        }

        // 2. Activate Category Offers
        List<Category> categoriesWithOffers = categoryRepository.findAllByIsHavingOfferTrue();
        for (Category category : categoriesWithOffers) {
            // Check if offer starts today
            if (category.getOffer().getStartDate() != null && !category.getOffer().getStartDate().isAfter(today) &&
                    (category.getOffer().getEndDate() == null || !category.getOffer().getEndDate().isBefore(today))) {

                // Loop products and apply if not having specific offer
                List<Product> products = category.getProducts();
                for (Product product : products) {
                    if (!product.isHavingOffer()) {
                        int calculatedDiscount = (product.getPrice()
                                - (product.getPrice() * category.getOffer().getOfferPercentage() / 100));
                        if (product.getDiscountedPrice() != calculatedDiscount) {
                            product.setDiscountedPrice(calculatedDiscount);
                            productRepository.save(product);
                        }
                    }
                }
            }
        }
    }
}
