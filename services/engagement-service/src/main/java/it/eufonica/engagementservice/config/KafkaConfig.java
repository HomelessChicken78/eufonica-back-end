package it.eufonica.engagementservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Value("${SONG_ENGAGEMENT_TOPIC_NAME:song-engagement.events}")
    private String songEngagementTopicName;

    @Value("${SONG_ENGAGEMENT_TOPIC_PARTITIONS:2}")
    private int songEngagementTopicPartitions;

    @Value("${SONG_ENGAGEMENT_TOPIC_REPLICAS:1}")
    private int songEngagementTopicReplicas;

    @Bean
    public NewTopic songEngagementTopic() {
        return TopicBuilder.name(songEngagementTopicName)
                .partitions(songEngagementTopicPartitions)
                .replicas(songEngagementTopicReplicas)
                .build();
    }
}
