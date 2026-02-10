package com.sirajul.lenscraft.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Global exception handler for payment and order processing errors.
 */
@ControllerAdvice
@Slf4j
public class PaymentExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException ex, RedirectAttributes redirectAttributes) {
        log.error("Payment validation error: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/user/cart";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, RedirectAttributes redirectAttributes) {
        log.error("Order processing error: {}", ex.getMessage(), ex);

        // Check if it's wrapped IllegalStateException
        if (ex.getCause() instanceof IllegalStateException) {
            redirectAttributes.addFlashAttribute("error", ex.getCause().getMessage());
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "An error occurred while processing your order. Please try again.");
        }

        return "redirect:/user/cart";
    }
}
