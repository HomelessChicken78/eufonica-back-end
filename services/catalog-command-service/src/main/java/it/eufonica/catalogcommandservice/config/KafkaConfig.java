package it.eufonica.catalogcommandservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Value("${ARTIST_TOPIC_NAME:artist.events}")
    private String artistTopicName;

    @Value("${ARTIST_TOPIC_PARTITIONS}")
    private int artistTopicPartitions;

    @Value("${ARTIST_TOPIC_REPLICAS:1}")
    private int artistTopicReplicas;

    @Bean
    public NewTopic artistTopic() {
        return TopicBuilder.name(artistTopicName)
                .partitions(artistTopicPartitions)
                .replicas(artistTopicReplicas)
                .build();
    }
}
