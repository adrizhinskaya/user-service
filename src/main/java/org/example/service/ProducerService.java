package org.example.service;

import org.example.model.dto.UserEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
@Service
public class ProducerService {@Autowired
private KafkaTemplate<String, String> kafkaTemplate;
    private final String topic = "user-events";

    public void sendUserEvent(String operation, String email) {
        UserEvent event = new UserEvent(operation, email);
        kafkaTemplate.send(topic, event.getEmail(), event.toString());
        System.out.println("Sent message to Kafka: " + event);
    }
}
