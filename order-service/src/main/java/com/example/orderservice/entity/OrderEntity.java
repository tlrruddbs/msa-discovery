package com.example.orderservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

@Data
@Entity
@Table(name = "orders")
public class OrderEntity implements Serializable {
  @Id @GeneratedValue
  private Long id;
  @Column(nullable = false, length = 120)
  private String productId;
  @Column(nullable = false)
  private Integer qty;
  @Column(nullable = false)
  private Integer unitPrice;
  @Column(nullable = false)
  private Integer totalPrice;
  @Column(nullable = false)
  private String userId;
  @Column(nullable = false, unique = true)
  private String orderId;
  @Column(nullable = false, updatable = false, insertable = false)
  @ColumnDefault(value = "CURRENT_TIMESTAMP")
  private Date createAt;

}
