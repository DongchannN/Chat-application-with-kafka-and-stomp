package com.DongchannN.kafkachattingapplication;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@SpringBootTest
class KafkaChattingApplicationTests {

	@Autowired
	Environment env;

	@Test
	void getProperties() {
		System.out.println("KafkaChattingApplicationTests :: getProperties() start");
		System.out.println("KafkaChattingApplicationTests :: getProperties() : TEST_STRING = " + env.getProperty("test-string"));
		System.out.println("KafkaChattingApplicationTests :: getProperties() : KAFKA_SERVER_ADDR = " + env.getProperty("kafka.server_addr"));
	}

	@Test
	void contextLoads() {
	}

}
