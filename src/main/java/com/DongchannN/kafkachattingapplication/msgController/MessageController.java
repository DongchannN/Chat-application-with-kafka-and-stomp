package com.DongchannN.kafkachattingapplication.msgController;

import com.DongchannN.kafkachattingapplication.dto.MessageDto;
import com.DongchannN.kafkachattingapplication.kafka.producer.SimpleProducer;
import com.DongchannN.kafkachattingapplication.kafka.producer.SimpleProducerV2;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MessageController {
    private final SimpleProducer simpleProducer;
    private final SimpleProducerV2 simpleProducerV2;

    @SubscribeMapping("/topic/test")
    public String subscribeTopicTest(String payload) {
        log.info("MessageController :: subscribeTopicTest() : payload = {}", payload);
        return ("hello " + payload);
    }

    @MessageMapping("/test")
    public String testStringMessaging(String payload) {
        log.info("MessageController :: testStringMessage() : payload = {}", payload);
        // kafka 로 메시지 내용 send.
        simpleProducer.sendString("dan-test", payload);
        return payload;
    }

    @SubscribeMapping("/room/{roomId}")
    public void subscribeRoom(@DestinationVariable String roomId) {
        log.info("MessageController :: subscribeRoom() : roomId = {}", roomId);
    }

    @MessageMapping("/chat")
    public void receiveMessage(String payload) throws Exception {
        MessageDto msg = new ObjectMapper().readValue(payload, MessageDto.class);
        log.info("MessageController :: receiveMessage() : roomId = {}, sender = {}, content = {}", msg.getRoomId(), msg.getSender(), msg.getSender());
        simpleProducerV2.sendMessageDto(msg);
        log.info("MessageController :: receiveMessage() : after sendMessageDto()");
    }
}
