package com.adas.datastream.kafka;

import com.adas.datastream.model.AdasDataset;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * author koustavroy
 */

@Service
public class KafkaRetryService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaRetryService.class);
    @Value("${msg.retry.count:2}")
    protected int maxRetry;
    @Value("${kafka.retry.topic:null}")
    protected String retryTopic;
    @Value("${kafka.letter.topic:null}")
    protected String letterTopic;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private KafkaProducerService kafkaProducerService;

    public void sendToRetryTopic(AdasDataset adasDataset) throws JsonProcessingException, JSONException {
        int currentAttempts = adasDataset.getRetryCount();
        if (currentAttempts < maxRetry) {
            currentAttempts = currentAttempts + 1;
            adasDataset.setRetryCount(currentAttempts);
            logger.info("KafkaRetryService:: sendToRetryTopic: retry count = {}", currentAttempts);
            kafkaProducerService.sendMessage(mapper.writeValueAsString(adasDataset), retryTopic);
        } else {
            sendToLetterTopic(mapper.writeValueAsString(adasDataset));
        }
    }

    public void sendToLetterTopic(String retryMessage) {
        logger.warn("KafkaRetryService:: sendToLetterTopic: letterTopic: {} , retryMessage : {}",
                letterTopic, retryMessage);
        kafkaProducerService.sendMessage(retryMessage, letterTopic);
    }
}
