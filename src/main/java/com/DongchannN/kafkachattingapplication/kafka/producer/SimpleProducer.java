package com.DongchannN.kafkachattingapplication.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SimpleProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public SendResult<String, String> sendString(String topic, String payload) {
        log.info("SimpleProducer :: sendString() : topic = {}, payload = {}", topic, payload);
        SendResult<String, String> res = null;
        try {
            res = kafkaTemplate.send(topic, payload).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
