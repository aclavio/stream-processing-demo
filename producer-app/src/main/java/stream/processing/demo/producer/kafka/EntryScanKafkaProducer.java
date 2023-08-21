package stream.processing.demo.producer.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import stream.processing.demo.PortEntryRecord;

import java.util.Properties;

public class EntryScanKafkaProducer implements InitializingBean, DisposableBean {

    protected static final Logger logger = LoggerFactory.getLogger(EntryScanKafkaProducer.class);

    @Autowired
    private Properties kafkaProperties;
    @Value("${kafka.producer.topic}")
    private String kafkaTopic;

    private KafkaProducer<String, PortEntryRecord> producer;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Creating Kafka Producer with Properties:");
        logger.info(kafkaProperties.toString());

        // initialize the Kafka producer using the properties file
        producer = new KafkaProducer<>(kafkaProperties);
    }

    public void publishEntryRecord(PortEntryRecord record) {
        // publish the canonical data to the configured Kafka topic
        producer.send(new ProducerRecord<>(kafkaTopic, record.getPortName(), record));
    }

    @Override
    public void destroy() throws Exception {
        logger.info("Destroying KafkaProducer");
        // gracefully close the kafka producer
        producer.flush();
        producer.close();
        producer = null;
    }
}
