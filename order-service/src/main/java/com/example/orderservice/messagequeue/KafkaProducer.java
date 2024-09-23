package com.example.orderservice.messagequeue;

import com.example.orderservice.dto.OrderDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {
  private final KafkaTemplate<String, String> kafkaTemplate;

  public OrderDto send(String topic, OrderDto orderDto){
    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = "";

    try {
      jsonInString  = mapper.writeValueAsString(orderDto);
    } catch (Exception e ){
      e.printStackTrace();
    }

    kafkaTemplate.send("example-catalog-topic", jsonInString);
    log.info("kafka producer send data from the order microservice " + orderDto);

    return orderDto;
  }

}
