package com.DongchannN.kafkachattingapplication.kafka.consumer;

import com.DongchannN.kafkachattingapplication.dto.MessageDto;
import com.DongchannN.kafkachattingapplication.kafka.producer.SimpleProducerV2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimpleConsumerV2Test {

    @Autowired
    private SimpleProducerV2 producer;
    @Autowired private SimpleConsumerV2 consumer;
    @LocalServerPort private Integer port;
    private WebSocketStompClient webSocketStompClient;

    @Test
    public void givenMessageDto_whenSendMessageDtoWithProducer_thenReceiveMessageDto() throws InterruptedException {
        // given
        MessageDto msg = new MessageDto("dan-test-02", "test-sender", "test-content");

        // when (producer를 이용하여 메시지를 보냄.)
        producer.sendMessageDto(msg);
        consumer.getLatch().await(1, TimeUnit.SECONDS);

        // then (consumer의 payloads(consume한 payload를 담아놓은 배열)의 개수, 내용을 검사.)
        assertEquals(1, consumer.getPayloads().size());
        assertEquals(msg.getSender(), consumer.getPayloads().get(0).getSender());
        assertEquals(msg.getContent(), consumer.getPayloads().get(0).getContent());
    }

    @Test
    public void givenMessageDtoAndClient_whenSendMessageDtoWithProducer_thenSendToUserBySTOMP() throws ExecutionException, InterruptedException {
        // given (STOMP Broker를 바라보고 있는 세션, 그 세션은 MessageBroker의 "/topic/room/dan-test-02"를 구독함.)
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
        MessageDto msg = new MessageDto("dan-test-02", "test-sender", "test-content");

        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(transports));
        this.webSocketStompClient.setMessageConverter(new StringMessageConverter());

        StompSession session = webSocketStompClient.connectAsync(getURL(), new StompSessionHandlerAdapter() {}).get();

        session.subscribe("/topic/room/dan-test-02", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("payload : " + (String) payload);
                queue.add((String) payload);
            }
        });

        // when (producer로 메시지를 보내면, kafkaListener로 듣고 있는 consumer가 data를 가져오고 STOMP를 이용하여 구독한 사용자들에게 메시지 전송.)
        producer.sendMessageDto(msg);

        // then (StompFrameHandler를 구현할 때 handleFrame 메서드를 오버라이딩해 미리 준비해놓은 큐에 데이터가 저장되도록 하였음.)
        await()
                .atMost(3, SECONDS)
                .untilAsserted(() -> assertEquals(msg.makeKafkaSavedMsg(), queue.poll()));
    }

    String getURL() {
        return "ws://localhost:" + port + "/stomp-ws";
    }
}
