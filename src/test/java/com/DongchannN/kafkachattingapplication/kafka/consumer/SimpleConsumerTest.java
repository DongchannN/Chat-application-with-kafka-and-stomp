package com.DongchannN.kafkachattingapplication.kafka.consumer;

import com.DongchannN.kafkachattingapplication.kafka.producer.SimpleProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimpleConsumerTest {

    @Autowired private SimpleProducer producer;
    @Autowired private SimpleConsumer consumer;

    @Test
    public void givenKafkaBroker_whenSendMessageWithProducer_thenReceiveMessage() throws InterruptedException {
        // given
        String topic = "dan-test-01";
        String payload = "test payload";

        // when
        producer.sendString(topic, payload);

        // then
        consumer.getLatch().await(2, TimeUnit.SECONDS);

        assertEquals(1, consumer.getPayloads().size());
        assertEquals(payload, consumer.getPayloads().get(0));
    }
}