package com.looyt.usermanagement.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka Configuration
 * Creates topics automatically if they don't exist
 */
@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.user-events}")
    private String userEventsTopic;

    @Value("${kafka.topics.user-created}")
    private String userCreatedTopic;

    @Value("${kafka.topics.user-updated}")
    private String userUpdatedTopic;

    @Value("${kafka.topics.user-deleted}")
    private String userDeletedTopic;

    @Bean
    public NewTopic userEventsTopic() {
        return TopicBuilder.name(userEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userCreatedTopic() {
        return TopicBuilder.name(userCreatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userUpdatedTopic() {
        return TopicBuilder.name(userUpdatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userDeletedTopic() {
        return TopicBuilder.name(userDeletedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}