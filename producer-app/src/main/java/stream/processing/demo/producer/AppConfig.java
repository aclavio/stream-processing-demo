package stream.processing.demo.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stream.processing.demo.producer.kafka.EntryScanKafkaProducer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Configuration
public class AppConfig {

    @Value("${kafka.producer.properties.file}")
    private String kafkaPropertiesFile;

    @Bean
    Properties kafkaProperties() throws IOException {
        final Properties cfg = new Properties();
        cfg.load(new FileInputStream(kafkaPropertiesFile));
        return cfg;
    }

    @Bean
    EntryScanKafkaProducer getKafkaProducer() {
        return new EntryScanKafkaProducer();
    }

}
