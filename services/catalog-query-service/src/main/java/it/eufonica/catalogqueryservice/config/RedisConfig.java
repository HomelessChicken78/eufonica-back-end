package it.eufonica.catalogqueryservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {
    @Value("${REDIS_BASE_TTL_DURATION_SECONDS:1800}")
    private Long ttlDuration;

    @Value("${SEARCH_TTL_DURATION_SECONDS:300}")
    private Long searchTtlDuration;

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        // The previous GenericJackson2JsonRedisSerializer is deprecated and can't be used. GenericJackson2JsonRedisSerializer
        // used to take no argument and created their own object mapper, which automatically asked for the class where the
        // cache originated from. Now that it is deprecated, we have to use GenericJacksonJsonRedisSerializer, which requires
        // an object mapper. However, we can't inject the one from spring, since it does not support classes. In fact,
        // if we use it, redis will try to ask for the class without succeeding, thus defaulting to trying to convert
        // to a LinkedHashMap. It may technically work if every method returns a LinkedHashMap or a subclass, however
        // if you do something like (LinkedHashMap) Animal it will crash.
        // Why does the Spring ObjectMapper often fail? Because it is configured for Rest API, where you don't want to return
        // the class for security reasons (obviously).
        // Solution: Use GenericJacksonJsonRedisSerializer, which allow us to create a custom serializer (used by the Object Mapper to turn
        // Objects into JSON) with enableDefaultTyping. This allows us to also add the java class to the JSON, avoiding the Redis confusion.

        GenericJacksonJsonRedisSerializer serializer =
                GenericJacksonJsonRedisSerializer.builder()
                        .enableDefaultTyping(
                                BasicPolymorphicTypeValidator.builder()
                                        .allowIfBaseType(Object.class)
                                        .build()
                        )
                        .build();

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(ttlDuration))
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(serializer)
                );
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(
            RedisCacheConfiguration cacheConfiguration) {
        return (builder) -> builder
                .cacheDefaults(cacheConfiguration)
                .withCacheConfiguration("songs",
                        cacheConfiguration.entryTtl(Duration.ofSeconds(ttlDuration)))
                .withCacheConfiguration("albums",
                        cacheConfiguration.entryTtl(Duration.ofSeconds(ttlDuration)))
                .withCacheConfiguration("artists",
                        cacheConfiguration.entryTtl(Duration.ofSeconds(ttlDuration)))
                .withCacheConfiguration("song-search",
                        cacheConfiguration.entryTtl(Duration.ofSeconds(searchTtlDuration)))
                .withCacheConfiguration("album-search",
                        cacheConfiguration.entryTtl(Duration.ofSeconds(searchTtlDuration)))
                .withCacheConfiguration("artist-search",
                        cacheConfiguration.entryTtl(Duration.ofSeconds(searchTtlDuration)));
    }
}