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

    @Value("${ARTIST_TOPIC_PARTITIONS:2}")
    private int artistTopicPartitions;

    @Value("${ARTIST_TOPIC_REPLICAS:1}")
    private int artistTopicReplicas;

    @Value("${SONG_TOPIC_NAME:song.events}")
    private String songTopicName;

    @Value("${SONG_TOPIC_PARTITIONS:2}")
    private int songTopicPartitions;

    @Value("${SONG_TOPIC_REPLICAS:1}")
    private int songTopicReplicas;

    @Value("${ALBUM_TOPIC_NAME:album.events}")
    private String albumTopicName;

    @Value("${ALBUM_TOPIC_PARTITIONS:2}")
    private int albumTopicPartitions;

    @Value("${ALBUM_TOPIC_REPLICAS:1}")
    private int albumTopicReplicas;

    @Bean
    public NewTopic artistTopic() {
        return TopicBuilder.name(artistTopicName)
                .partitions(artistTopicPartitions)
                .replicas(artistTopicReplicas)
                .build();
    }

    @Bean
    public NewTopic songTopic() {
        return TopicBuilder.name(songTopicName)
                .partitions(songTopicPartitions)
                .replicas(songTopicReplicas)
                .build();
    }

    @Bean
    public NewTopic albumTopic() {
        return TopicBuilder.name(albumTopicName)
                .partitions(albumTopicPartitions)
                .replicas(albumTopicReplicas)
                .build();
    }
}