package co.uk.joecastle.imdbtop250.config;

import java.time.Duration;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

@Configuration
public class RedisCacheConfig {

  @Bean
  public RedisCacheManagerBuilderCustomizer cacheManagerBuilderCustomizer() {
    return cacheManagerBuilder ->
        cacheManagerBuilder.withCacheConfiguration(
            "moviesCache",
            RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(30)));
  }
}
