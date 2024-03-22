# Kafka/Web Socket Study

## Profile
- dev profile(Default) : 개발 시 사용할 profile.
- test profile : junit 테스트 시 사용할 profile. @ActiveProfiles("test")를 이용해 test 코드 실행 시 test profile을 사용할 수 있게 함.

## Configuration
### KafkaProducerConfig.java
- key, value serializer : StringSerializer 사용.
- producer를 이용하여 카프카로 데이터 전송 시 key는 따로 사용하지 않음.
### KafkaConsumerConfig.java
- 2개의 Bean 등록. (kafkaListenerContainerFactory, kafkaListenerContainerFactory2)
- kafkaListenerContainerFactory : kafka에 저장되어 있는 String data를 취급.
- kafkaListenerContainerFactory2 : kafka에 저장되어 있는 MessageDto.KafkaSavedMessage 객체를 취급.
### WebSocketConfig.java
- 기본적인 웹소켓 설정.
- 웹소켓 연결 시 `ws://서버주소:포트번호/stomp-ws`로 연결.
- `/app/**`으로 메시지 보낼 시 MessageController.java의 @MessageMapping, 헤더가 SUBSCRIBE라면 @SubscribeMapping으로 연결. 
- `/topic/**`으로 메시지 보낼 시 SimpleBroker로 바로 연결할 수 있음.

## kafka/consumer
### SimpleConsumer.java
- topic : dan-test-01
- group id : dan-100
- container factory : kafkaListenerContainerFactory(String -> String) 사용.

카프카에 저장되어 있는 String을 consume, payloads라는 List<String>에 consume한 데이터 저장.
### SimpleConsumerV2.java
- topic : dan-test-02
- group id : dan-200(receive()), dan-201(receiveAndSend())
- container factory : kafkaListenerContainerFactory2(String -> MessageDto.KafkaSavedMessage) 사용.

1. receive() : 카프카에 저장되어 있는 MessageDto.KafkaSavedMessage를 consume, payloads라는 List<MessageDto.KafkaSavedMessage>에 consume한 데이터 저장.
2. receiveAndSend() : 카프카에 저장되어 있는 MessageDto.KafkaSavedMessage를 consume, SimpMessageTemplate을 이용해 웹소켓으로 `/topic/room/dan-test-02`를 구독한 클라이언트에게 메시지를 보냄.

**group id를 나눈 이유** : 만약 receive()함수에서 데이터를 consume했다면, receiveAndSend()로 클라이언트들에게 메시지가 전송되지 않는 문제 발생. consumer group에 따라 kafka topic의 오프셋을 따로 기억하므로 group id를 나누어 각 메서드가 영향을 받지 않게 할 수 있음.
## kafka/producer
### SimpleProducer.java
- String을 kafka로 전송하는 역할.
- 메시지를 성공적으로 보냈는지 테스트하기 위하여 SendResult객체를 return할 수 있도록 함.
- 만약 메시지 전송에 실패하였다면 null을 return하므로 메서드를 사용하는 구문에서 null 체크가 필요함. 

### SimpleProducerV2.java
- MessageDto.KafkaSavedMessage을 kafka로 전송하는 역할.
- return type은 SimpleProducer와 동일.
