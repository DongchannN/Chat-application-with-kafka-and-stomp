package com.DongchannN.kafkachattingapplication.kafka.consumer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
@Getter
@Slf4j
public class SimpleConsumer {
    private CountDownLatch latch = new CountDownLatch(10);
    private List<String> payloads = new ArrayList<>();
    private String payload;

    @KafkaListener(topics = "dan-test-01"
            , groupId = "dan-100"
            , containerFactory = "kafkaListenerContainerFactory")
    public void receive(ConsumerRecord<String, String> consumerRecord) {
        payload = consumerRecord.value();
        log.info("SimpleConsumer :: receive() : payload = {}", payload);
        payloads.add(payload);
        latch.countDown();
    }


    public List<String> getPayloads() {
        return payloads;
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }
}
