package com.example.orderservice.messagequeue;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

public class KafkaProducerConfig {
  @Bean
  public ProducerFactory<String, String> producerFactory(){
    Map<String, Object> properties = new HashMap<>();

    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.18.0.101:9092");
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, "consumerGroupId");
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);

    return new DefaultKafkaProducerFactory<>(properties);
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate(){
    return new KafkaTemplate<>(producerFactory());
  }

}
