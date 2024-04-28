package com.drevotyuk.model;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "ORDERS")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Order {
    @Id
    @GeneratedValue
    private int id;
    @GeneratedValue
    private LocalDateTime creationTime;
    @NonNull
    private OrderStatus status;
    @NonNull
    private int customerId;
    @NonNull
    private String productName;
    @NonNull
    private int productQuantity;
    @GeneratedValue
    private double totalPrice;

    public enum OrderStatus {
        ORDERED,
        READY,
        DELIVERED;
    }

    public Order(@NonNull OrderStatus status, @NonNull int customerId, @NonNull String productName,
            @NonNull int productQuantity) {
        this.status = status;
        this.customerId = customerId;
        this.productName = productName;
        this.productQuantity = productQuantity;
    }
}
