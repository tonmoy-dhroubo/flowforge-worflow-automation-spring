package com.flowforge.orchestrator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
public class KafkaConfig {
    // Basic configuration is handled in application.yml
    // This class enables Kafka and can be used for more complex setup
    // like custom error handlers or container factory configurations.
}