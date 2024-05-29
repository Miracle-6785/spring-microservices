package dev.miracle.ecommerce.kafka;

import dev.miracle.ecommerce.customer.CustomerResponse;
import dev.miracle.ecommerce.order.PaymentMethod;
import dev.miracle.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
