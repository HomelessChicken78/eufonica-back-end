package it.eufonica.catalogcommandservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Value("${ARTIST_CREATED_TOPIC_NAME:artist.created}")
    private String artistCreatedTopicName;

    @Value("${ARTIST_UPDATED_TOPIC_NAME:artist.updated}")
    private String artistUpdatedTopicName;

    @Bean
    public NewTopic artistCreatedTopic() {
        return TopicBuilder.name(artistCreatedTopicName)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic artistUpdatedTopic() {
        return TopicBuilder.name(artistUpdatedTopicName)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
