package com.example.authservice.EventProducer;

import com.example.authservice.model.UserSignUpEventDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class UserInfoProducer {
    KafkaTemplate<String, Object> template;
    
    @Value("${KAFKA_TOPIC}")
    private String TOPIC;

    @Autowired
    UserInfoProducer(KafkaTemplate<String, Object> template) {
        this.template = template;
    }

    public void sendEventToKafka(UserSignUpEventDto dto) {
        try {
            Message<UserSignUpEventDto> message = MessageBuilder
                    .withPayload(dto)
                    .setHeader(KafkaHeaders.TOPIC, TOPIC)
                    .build();
            System.out.println("sending to the topic :" + TOPIC);
            template.send(message);
        }
        catch (Exception e){
            System.out.println("Exception in UserInfoProducer " + e.getMessage());
        }
    }
}