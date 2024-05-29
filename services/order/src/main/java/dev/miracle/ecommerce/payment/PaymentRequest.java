package dev.miracle.ecommerce.payment;

import dev.miracle.ecommerce.customer.CustomerResponse;
import dev.miracle.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
