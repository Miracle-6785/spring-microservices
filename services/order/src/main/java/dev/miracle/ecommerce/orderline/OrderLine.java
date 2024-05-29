package dev.miracle.ecommerce.orderline;

import dev.miracle.ecommerce.order.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class OrderLine {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne()
    @JoinColumn(name = "order_id")
    private Order order;
    private Integer productId;
    private double quantity;
}