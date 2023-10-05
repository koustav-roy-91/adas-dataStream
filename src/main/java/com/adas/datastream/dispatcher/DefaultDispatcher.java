package com.adas.datastream.dispatcher;

import com.adas.datastream.kafka.KafkaRetryService;
import com.adas.datastream.model.AdasDataset;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * author koustavroy
 */

@Component
public class DefaultDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(DefaultDispatcher.class);

    @Autowired
    KafkaRetryService kafkaRetryService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    AmazonS3 amazonS3;

    public void uploadToS3(String kafkaMessage) {
        AdasDataset adasDataset = null;
        try {
            adasDataset = mapper.readValue(kafkaMessage, AdasDataset.class);
            amazonS3.putObject("adas-" + adasDataset.getType(), adasDataset.getFileName(), adasDataset.getMessage());
            logger.info("\n :::::::  msg having fileName: {}, uuid {} has successfully pushed into amazon s3 ::::::", adasDataset.getFileName(), adasDataset.getUuid());
        } catch (AmazonServiceException | JsonProcessingException e) {
            try {
                logger.error("\n msg sending failed to amazon s3 ,fileName: {}, uuid: {} Error: {}", adasDataset.getFileName(), adasDataset.getUuid(), e.getMessage());
                logger.info("\n msg having fileName: {}, uuid {} pushing into retry topic", adasDataset.getFileName(), adasDataset.getUuid());
                kafkaRetryService.sendToRetryTopic(adasDataset);
            } catch (JsonProcessingException | JSONException ex) {
                logger.error("\n pushing failed into retry topic fileName: {},   uuid: {} Error: {}", adasDataset.getFileName(), adasDataset.getUuid(), e.getMessage());
                logger.info("\n pushing into letter topic fileName: {},uuid {}", adasDataset.getFileName(), adasDataset.getUuid());
                try {
                    kafkaRetryService.sendToLetterTopic(mapper.writeValueAsString(adasDataset));
                } catch (JsonProcessingException exc) {
                    logger.error("\n pushing failed into letter topic fileName: {},   uuid: {} Error: {}", adasDataset.getFileName(), adasDataset.getUuid(), e.getMessage());
                }
            }
        }
    }
}
