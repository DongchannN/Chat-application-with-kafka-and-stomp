package com.DongchannN.kafkachattingapplication.kafka.producer;

import com.DongchannN.kafkachattingapplication.dto.MessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimpleProducerV2Test {

    @Autowired
    private SimpleProducerV2 producer;

    @Test
    public void givenMessageDto_whenSendingMessageDto_thenVerifyMessageSend() throws JsonProcessingException {
        // given
        MessageDto msg = new MessageDto("test-room-id", "test-sender", "test-content");

        // when
        SendResult<String, String> result = producer.sendMessageDto(msg);

        String roomId = result.getRecordMetadata().topic();
        String savedStringMsg = result.getProducerRecord().value();

        // then
        MessageDto.KafkaSavedMessage savedMsg = new ObjectMapper().readValue(savedStringMsg, MessageDto.KafkaSavedMessage.class);

        assertEquals(roomId, "test-room-id", "채팅방 id가 일치하지 않습니다.");
        assertEquals(savedMsg.getSender(), "test-sender", "송신지가 일치하지 않습니다.");
        assertEquals(savedMsg.getContent(), "test-content", "보낸 내용이 일치하지 않습니다.");
    }
}