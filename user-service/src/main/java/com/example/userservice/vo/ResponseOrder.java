package com.example.userservice.vo;

import java.util.Date;
import lombok.Data;

@Data
public class ResponseOrder {
  private String productId;
  private Integer qty;
  private Integer unitPrice;
  private String totalPrice;
  private Date createAt;

  private String orderId;

}
