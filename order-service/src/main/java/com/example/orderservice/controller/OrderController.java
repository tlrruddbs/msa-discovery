package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.messagequeue.KafkaProducer;
import com.example.orderservice.messagequeue.OrderProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import java.net.Authenticator.RequestorType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/order-service")
public class OrderController {
  private final OrderService orderService;
  private final Environment env;
  private final KafkaProducer kafkaProducer;
  private final OrderProducer orderProducer;

  @GetMapping("/health_check")
  public String status(){
    return String.format("It's working in user-service %s", env.getProperty("local.server.port"));
  }

  @PostMapping(value = "/{userId}/orders")
  public ResponseEntity<ResponseOrder> createOrder(
      @PathVariable("userId") String userId,
      @RequestBody RequestOrder orderDetails) {

    log.info("before add order data");

    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

    //jpa
    OrderDto orderDto = modelMapper.map(orderDetails, OrderDto.class);
    orderDto.setUserId(userId);

//    OrderDto createDto = orderService.createOrder(orderDto);
//    ResponseOrder returnValue = modelMapper.map(createDto, ResponseOrder.class);

    //kafka
    orderDto.setOrderId(UUID.randomUUID().toString());
    orderDto.setTotalPrice(orderDetails.getQty() * orderDetails.getUnitPrice());

    kafkaProducer.send("example-category-topic", orderDto);
    orderProducer.send("orders", orderDto);

    ResponseOrder returnValue = modelMapper.map(orderDto, ResponseOrder.class);

    log.info("after add order data");

    return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);

  }

  @GetMapping(value="/{userId}/orders")
  public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId){

    log.info("before retrieve  orders microservice");

    Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

    List<ResponseOrder> result = new ArrayList<>();
    orderList.forEach(v->result.add(new ModelMapper().map(v, ResponseOrder.class)));

    log.info("add retrieve orders microservice");

    return ResponseEntity.status(HttpStatus.OK).body(result);
  }
}
