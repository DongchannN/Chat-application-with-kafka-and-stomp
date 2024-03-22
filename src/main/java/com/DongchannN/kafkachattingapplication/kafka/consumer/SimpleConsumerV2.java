package com.DongchannN.kafkachattingapplication.kafka.consumer;

import com.DongchannN.kafkachattingapplication.dto.MessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
@Getter
@Slf4j
@RequiredArgsConstructor
public class SimpleConsumerV2 {
    private final SimpMessagingTemplate template;
    private CountDownLatch latch = new CountDownLatch(10);
    private List<MessageDto.KafkaSavedMessage> payloads = new ArrayList<>();
    private MessageDto.KafkaSavedMessage payload;

    @KafkaListener(topics = "dan-test-02"
            , groupId = "dan-200"
            , containerFactory = "kafkaListenerContainerFactory2")
    public void receive(ConsumerRecord<String, MessageDto.KafkaSavedMessage> consumerRecord) {
        payload = consumerRecord.value();
        log.info("SimpleConsumerV2 :: receive() : payload.sender = {}", payload.getSender());
        log.info("SimpleConsumerV2 :: receive() : payload.content = {}", payload.getContent());
        payloads.add(payload);
        latch.countDown();
    }

    public List<MessageDto.KafkaSavedMessage> getPayloads() {
        return payloads;
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }

    @KafkaListener(topics = "dan-test-02"
            , groupId = "dan-201"
            , containerFactory = "kafkaListenerContainerFactory2")
    public void receiveAndSend(ConsumerRecord<String, MessageDto.KafkaSavedMessage> consumerRecord) throws JsonProcessingException {
        payload = consumerRecord.value();
        log.info("SimpleConsumerV2 :: receiveAndSend() : payload.sender = {}", payload.getSender());
        log.info("SimpleConsumerV2 :: receiveAndSend() : payload.content = {}", payload.getContent());
        template.convertAndSend("/topic/room/dan-test-02", new ObjectMapper().writeValueAsString(payload));
        log.info("SimpleConsumer :: receive : after send");
        latch.countDown();
    }
}
