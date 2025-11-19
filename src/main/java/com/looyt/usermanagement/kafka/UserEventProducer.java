package com.looyt.usermanagement.kafka;

import com.looyt.usermanagement.dto.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka Producer for User Events
 * Publishes user-related events to Kafka topics
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Value("${kafka.topics.user-events}")
    private String userEventsTopic;

    @Value("${kafka.topics.user-created}")
    private String userCreatedTopic;

    @Value("${kafka.topics.user-updated}")
    private String userUpdatedTopic;

    @Value("${kafka.topics.user-deleted}")
    private String userDeletedTopic;

    /**
     * Publish user created event
     */
    public void publishUserCreatedEvent(UserEvent event) {
        publishEvent(userCreatedTopic, event);
        publishEvent(userEventsTopic, event); // Also publish to general events topic
    }

    /**
     * Publish user updated event
     */
    public void publishUserUpdatedEvent(UserEvent event) {
        publishEvent(userUpdatedTopic, event);
        publishEvent(userEventsTopic, event);
    }

    /**
     * Publish user deleted event
     */
    public void publishUserDeletedEvent(UserEvent event) {
        publishEvent(userDeletedTopic, event);
        publishEvent(userEventsTopic, event);
    }

    /**
     * Generic method to publish events to Kafka
     */
    private void publishEvent(String topic, UserEvent event) {
        try {
            String key = String.valueOf(event.getUserId());

            CompletableFuture<SendResult<String, UserEvent>> future =
                    kafkaTemplate.send(topic, key, event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Published event to Kafka - Topic: {}, Key: {}, Event: {}, Partition: {}, Offset: {}",
                            topic,
                            key,
                            event.getEventType(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish event to Kafka - Topic: {}, Key: {}, Event: {}, Error: {}",
                            topic,
                            key,
                            event.getEventType(),
                            ex.getMessage());
                }
            });

        } catch (Exception e) {
            log.error("Error publishing event to Kafka - Topic: {}, Error: {}", topic, e.getMessage(), e);
        }
    }
}