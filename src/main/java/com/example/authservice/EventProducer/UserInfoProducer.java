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
    
    @Value("${spring.kafka.topic-name}")
    private String TOPIC;

    @Autowired
    UserInfoProducer(KafkaTemplate<String, Object> template) {
        this.template = template;
    }

    public void sendEventToKafka(UserSignUpEventDto dto) {
        Message<UserSignUpEventDto> message = MessageBuilder
                .withPayload(dto)
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .build();
        template.send(message);
    }
}