package com.looyt.usermanagement.kafka;

import com.looyt.usermanagement.dto.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka Consumer for User Events
 * Listens to user-related events from Kafka topics
 *
 * This is an example consumer that logs events.
 * In a real microservices architecture, you would:
 * - Update caches
 * - Send notifications
 * - Update search indexes
 * - Sync with other services
 */
@Component
@Slf4j
public class UserEventConsumer {

    /**
     * Listen to all user events
     */
    @KafkaListener(
            topics = "${kafka.topics.user-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUserEvent(
            @Payload UserEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("📨 Received User Event - Topic: {}, Partition: {}, Offset: {}, Event: {}, UserId: {}, Type: {}",
                topic, partition, offset, event.getEventType(), event.getUserId(), event.getEventType());

        // Process the event based on type
        processUserEvent(event);
    }

    /**
     * Listen to user created events
     */
    @KafkaListener(
            topics = "${kafka.topics.user-created}",
            groupId = "${spring.kafka.consumer.group-id}-created"
    )
    public void consumeUserCreatedEvent(@Payload UserEvent event) {
        log.info("✨ User Created - UserId: {}, Name: {}, Email: {}",
                event.getUserId(), event.getName(), event.getEmail());

        // Example actions:
        // - Send welcome email
        // - Create user profile in another service
        // - Add to mailing list
        // - Update analytics
    }

    /**
     * Listen to user updated events
     */
    @KafkaListener(
            topics = "${kafka.topics.user-updated}",
            groupId = "${spring.kafka.consumer.group-id}-updated"
    )
    public void consumeUserUpdatedEvent(@Payload UserEvent event) {
        log.info("🔄 User Updated - UserId: {}, Name: {}, Email: {}",
                event.getUserId(), event.getName(), event.getEmail());

        // Example actions:
        // - Invalidate cache
        // - Update search index
        // - Sync with other services
        // - Notify connected systems
    }

    /**
     * Listen to user deleted events
     */
    @KafkaListener(
            topics = "${kafka.topics.user-deleted}",
            groupId = "${spring.kafka.consumer.group-id}-deleted"
    )
    public void consumeUserDeletedEvent(@Payload UserEvent event) {
        log.info("🗑️ User Deleted - UserId: {}, Email: {}",
                event.getUserId(), event.getEmail());

        // Example actions:
        // - Remove from cache
        // - Delete from search index
        // - Archive user data
        // - Clean up related resources
    }

    /**
     * Process user event - central processing logic
     */
    private void processUserEvent(UserEvent event) {
        switch (event.getEventType()) {
            case "CREATED":
                handleUserCreated(event);
                break;
            case "UPDATED":
                handleUserUpdated(event);
                break;
            case "DELETED":
                handleUserDeleted(event);
                break;
            default:
                log.warn("Unknown event type: {}", event.getEventType());
        }
    }

    private void handleUserCreated(UserEvent event) {
        // Business logic for user creation
        log.debug("Processing user created event: {}", event);
    }

    private void handleUserUpdated(UserEvent event) {
        // Business logic for user update
        log.debug("Processing user updated event: {}", event);
    }

    private void handleUserDeleted(UserEvent event) {
        // Business logic for user deletion
        log.debug("Processing user deleted event: {}", event);
    }
}