package dev.miracle.ecommerce.order;

import dev.miracle.ecommerce.customer.CustomerClient;
import dev.miracle.ecommerce.exception.BusinessException;
import dev.miracle.ecommerce.kafka.OrderConfirmation;
import dev.miracle.ecommerce.kafka.OrderProducer;
import dev.miracle.ecommerce.orderline.OrderLineRequest;
import dev.miracle.ecommerce.orderline.OrderLineService;
import dev.miracle.ecommerce.payment.PaymentClient;
import dev.miracle.ecommerce.payment.PaymentRequest;
import dev.miracle.ecommerce.product.ProductClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;

    public Integer createOrder(OrderRequest request) {
        // check the customer -> OpenFein
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Customer not found"));

        // purchase the products -> product-ms (RestTemplate)
        var purchasedProducts =  this.productClient.purchaseProduct(request.products());

        // persist order
        var order = this.orderRepository.save(mapper.toOrder(request));

        // persist order lines
        for(PurchaseRequest purchaseRequest: request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }
        // start payment process
        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);

        // send the order confirmation -> notification-ms (kafka)
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );
        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return orderRepository
                .findAll()
                .stream()
                .map(mapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return orderRepository
                .findById(orderId)
                .map(mapper::toOrderResponse)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", orderId)));
    }
}
