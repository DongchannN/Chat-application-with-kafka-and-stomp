package com.DongchannN.kafkachattingapplication.kafka.producer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimpleProducerTest {

    @Autowired
    private SimpleProducer producer;

    @Test
    public void givenKafkaURL_whenSendingStringMessage_thenVerifyMessageSend() {
        // given
        String topic = "dan-test";
        String payload = "test payload";

        // when
        SendResult<String, String> result = producer.sendString(topic, payload);

        String resTopic = result.getRecordMetadata().topic();
        String resValue = result.getProducerRecord().value();

        // then
        assertEquals(topic, resTopic, "보낸 토픽과 콜백으로 받은 토픽이 일치하지 않습니다.");
        assertEquals(payload, resValue, "보낸 value와 콜백으로 받은 value이 일치하지 않습니다.");
    }
}