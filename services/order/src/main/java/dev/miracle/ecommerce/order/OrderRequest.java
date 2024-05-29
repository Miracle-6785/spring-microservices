package dev.miracle.ecommerce.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(
        Integer id,
        String reference,
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,
        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,
        @NotNull(message = "Customer ID is required")
        @NotEmpty(message = "Customer ID is required")
        @NotBlank(message = "Customer ID is required")
        String customerId,
        @NotEmpty(message = "Products are required")
        List<PurchaseRequest> products
) {
}
