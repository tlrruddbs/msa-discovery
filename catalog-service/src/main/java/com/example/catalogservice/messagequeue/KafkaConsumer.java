package com.example.catalogservice.messagequeue;


import com.example.catalogservice.entity.CatalogEntity;
import com.example.catalogservice.jpa.CatalogRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
  private final CatalogRepository repository;

  @KafkaListener(topics = "example-catalog-topic")
  public void  updateQty(String kafkaMessage){
    log.info(kafkaMessage);

    Map<Object,String> map = new HashMap<>();
    ObjectMapper mapper = new ObjectMapper();

    try {
      map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, String>>() {});
    } catch (Exception e){
      e.printStackTrace();
    }

    CatalogEntity entity = repository.findByProductId((String)map.get("productId"));
    if(entity != null) {
      entity.setStock(entity.getStock() - Integer.parseInt(map.get("qty")));
      repository.save(entity);
    }

  }

}
