package com.adas.datastream.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;

/**
 * author koustavroy
 */

@EnableKafka
@Configuration
public class MainConsumerConfig {

  @Value("${kafka.main.bootstrapAddress:null}")
  private String bootstrapAddress;

  @Value("${kafka.main.groupId:null}")
  private String groupId;

  @Value("${kafka.main.concurrency:4}")
  private Integer concurrency;

  @Value("${kafka.main.maxPollRecords:100}")
  private String maxPollRecords;

  @Value("${kafka.main.auto.offset.reset:earliest}")
  private String autoOffsetReset;

  private static final Logger log = LoggerFactory.getLogger(MainConsumerConfig.class);

  @Bean
  @Qualifier("kafkaEventMainListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, String> kafkaEventMainListenerContainerFactory() {
    log.info("kafkaEventMainListenerContainerFactory:: bootstrapAddress: {}", bootstrapAddress);
    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(inOrderConsumerFactory());
    factory.setConcurrency(concurrency);
    factory.getContainerProperties()
        .setPollTimeout(5_000);
    factory.getContainerProperties()
        .setConsumerTaskExecutor(taskExecutor());
    return factory;
  }

  public AsyncListenableTaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(64);
    executor.setMaxPoolSize(128);
    executor.setQueueCapacity(128);
    executor.setThreadNamePrefix("EVNT-MAIN-CSMR-");
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.initialize();
    return executor;
  }

  private ConsumerFactory<String, String> inOrderConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
    props.put(AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
    props.put("fetch.message.max.bytes","15728640");
    // relevant only if if auto commit is true
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "3000");
    return new DefaultKafkaConsumerFactory<>(props);
  }

}
