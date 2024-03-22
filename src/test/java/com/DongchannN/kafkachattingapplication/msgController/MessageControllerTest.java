package com.DongchannN.kafkachattingapplication.msgController;

import com.DongchannN.kafkachattingapplication.dto.MessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.awaitility.Awaitility.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageControllerTest {

    @LocalServerPort private Integer port;
    private WebSocketStompClient webSocketStompClient;

    @BeforeEach
    void setUp() {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(transports));
        this.webSocketStompClient.setMessageConverter(new StringMessageConverter());
    }

    @Test
    void givenNothingWhenSubscribeTestTopicThenSubscribed() throws ExecutionException, InterruptedException {
        // when
        StompSession session = webSocketStompClient.connectAsync(getURL(), new StompSessionHandlerAdapter() {}).get();

        session.subscribe("app/test", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("payload : " + (String) payload);
            }
        });

        // then
        assertTrue(session.isConnected());
    }

    @Test
    void givenMessageDtoWhenSendMessageThenVerityMessageSend() throws ExecutionException, InterruptedException, JsonProcessingException {
        // given
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
        MessageDto msg = new MessageDto("dan-test-02","test-sender", "test-content");
        StompSession session =
                webSocketStompClient
                        .connectAsync(getURL(), new StompSessionHandlerAdapter() {})
                        .get();
        session.subscribe("/topic/room/dan-test-02", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                queue.add((String) payload);
            }
        });

        // when (웹 소켓을 이용하여 메시지 객체를 String(JSON 형태)으로 변환해서 보냄.)
        session.send("/app/chat", new ObjectMapper().writeValueAsString(msg));

        // then (SimpleConsumerV2Test와 동일.)
        await()
                .atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> assertEquals(msg.makeKafkaSavedMsg(), queue.poll()));
    }

    String getURL() {
        return "ws://localhost:" + port + "/stomp-ws";
    }
}