package com.adas.datastream.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * author koustavroy
 */


@Configuration
@EnableKafka
public class KafkaProducerConfig {

  @Value("${kafka.producer.bootstrapAddress}")
  private String kafkaBootstrapServers;

  @Value("${kafka.producer.retries}")
  private int kafkaRetries;

  @Value("${kafka.producer.acks}")
  private String kafkaRequiredAcks;

  private Map<String, Object> createKafkaProducer() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.ACKS_CONFIG, kafkaRequiredAcks);
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "Data Pipeline Dispatcher Service");
    props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
    props.put(ProducerConfig.RETRIES_CONFIG, kafkaRetries);
    props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, "52428800");
    return props;
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate() {
    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(createKafkaProducer()));
  }
}
