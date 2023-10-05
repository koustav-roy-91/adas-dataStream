package com.adas.datastream.service;


import com.adas.datastream.dispatcher.DefaultDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * author koustavroy
 */

@Component
public class RetryTopicListener implements ConsumerSeekAware {


  @Value("${kafka.retry.offset:null}")
  protected String offset;

  @Autowired
  DefaultDispatcher defaultDispatcher;

  private static final Logger logger = LoggerFactory.getLogger(RetryTopicListener.class);

  @KafkaListener(topics = "${kafka.retry.topic}", groupId = "${kafka.retry.groupId}", containerFactory = "kafkaEventRetryListenerContainerFactory")
  public void onMessage(ConsumerRecord<String, String> record) {
    logger.info("onMessage for retry :: ===== from retry topic: {}, partition: {}, offset: {},"
            + " timestamp: {}", record.topic(), record.partition(), record.offset(),
         record.timestamp());
    defaultDispatcher.uploadToS3(record.value());

    // at the end of this method, offsets will be automatically committed for all partitions
  }

  @Override
  public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
    if ("latest".equals(offset))
      assignments.forEach((t, o) -> callback.seekToEnd(t.topic(), t.partition()));
    else if ("earliest".equals(offset))
      assignments.forEach((t, o) -> callback.seekToBeginning(t.topic(), t.partition()));
  }

  @Override
  public void registerSeekCallback(ConsumerSeekCallback callback) {
    // TODO
  }

  @Override
  public void onIdleContainer(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
    // TODO
  }

}
