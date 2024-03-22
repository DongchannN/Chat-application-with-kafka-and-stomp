package com.DongchannN.kafkachattingapplication.kafka.producer;

import com.DongchannN.kafkachattingapplication.dto.MessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@Slf4j
@RequiredArgsConstructor
public class SimpleProducerV2 {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public SendResult<String, String> sendMessageDto(MessageDto msg) {
        try {
            String savedMsg = msg.makeKafkaSavedMsg();
            log.info("SimpleProducerV2 :: sendMessageDto() : roomId = {}, content = {}", msg.getRoomId(), savedMsg);
            SendResult<String, String> res = kafkaTemplate.send(msg.getRoomId(), savedMsg).get();
            log.info("SimpleProducerV2 :: sendMessageDto() : after send");
            return res;
        } catch (JsonProcessingException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
