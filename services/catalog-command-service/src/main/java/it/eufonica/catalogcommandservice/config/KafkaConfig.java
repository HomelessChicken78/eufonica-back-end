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

    @Bean
    public NewTopic artistTopic() {
        return TopicBuilder.name(artistTopicName)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
