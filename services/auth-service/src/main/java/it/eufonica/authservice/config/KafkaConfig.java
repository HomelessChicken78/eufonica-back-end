package it.eufonica.authservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Value("${APP_USER_TOPIC_NAME:appuser.events}")
    private String appUserTopicName;

    @Value("${APP_USER_TOPIC_PARTITIONS:2}")
    private int appUserTopicPartitions;

    @Value("${APP_USER_TOPIC_REPLICAS:2}")
    private int appUserTopicReplicas;

    @Bean
    public NewTopic appUserTopic() {
        return TopicBuilder.name(appUserTopicName)
                .partitions(appUserTopicPartitions)
                .replicas(appUserTopicReplicas)
                .build();
    }
}
