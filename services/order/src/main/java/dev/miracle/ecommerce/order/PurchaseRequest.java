package dev.miracle.ecommerce.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PurchaseRequest(
        @NotNull(message = "Product ID is required")
        Integer productId,
        @Positive(message = "Quantity must be greater than zero")
        double quantity
) {
}
