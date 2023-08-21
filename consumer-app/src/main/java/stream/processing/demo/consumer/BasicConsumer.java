package stream.processing.demo.consumer;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.processing.demo.PortEntryRecord;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class BasicConsumer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(BasicConsumer.class);

    private static final String DEFAULT_CONFIG_FILE = "consumer.properties";
    private static final String DEFAULT_TOPIC = "port.entries.avro,port.entries.enriched.ksql.avro,port.entries.enriched.kstreams.avro";
    private final KafkaConsumer<String, PortEntryRecord> consumer;
    //private final KafkaConsumer<String, GenericRecord> consumer;
    private final List<String> topics;
    private final CountDownLatch shutdownLatch;

    public BasicConsumer(Properties config, List<String> topics) {
        // initialize the Kafka Consumer using the properties file
        this.consumer = new KafkaConsumer<>(config);
        this.topics = topics;
        this.shutdownLatch = new CountDownLatch(1);
    }

    @Override
    public void run() {
        try {
            // subscribe to the Kafka topics
            consumer.subscribe(topics);

            // basic Kafka consumer "poll loop"
            while (true) {
                // poll for new kafka events
                //ConsumerRecords<String, GenericRecord> records = consumer.poll(Long.MAX_VALUE);
                ConsumerRecords<String, PortEntryRecord> records = consumer.poll(Long.MAX_VALUE);

                // application specific processing...
                records.forEach(record -> {
                    // for demo purposes, just emit a log statement
                    logger.info("[{}] got record: [{}] {}", record.topic(), record.key(), record.value().toString());
                });

                // commit the offsets back to kafka
                consumer.commitSync();
            }

        } catch (WakeupException ex) {
            // awake from poll
        } catch (Exception ex) {
            logger.error("An unexpected error occurred!", ex);
        } finally {
            // gracefully shutdown the consumer!
            consumer.close();
            shutdownLatch.countDown();
        }
    }

    public void shutdown() throws InterruptedException {
        consumer.wakeup();
        shutdownLatch.await();
    }

    public static void main(String[] args) throws Exception {
        // load passed in properties
        String configPath = args.length > 0 ? args[0] : DEFAULT_CONFIG_FILE;
        final Properties cfg = new Properties();
        cfg.load(new FileInputStream(configPath));
        // get the topic to consumer from
        String kafkaTopic = args.length > 1 ? args[1] : DEFAULT_TOPIC;
        List<String> topics = Arrays.asList(kafkaTopic.split(","));

        // Start up our consumer thread
        BasicConsumer bc = new BasicConsumer(cfg, topics);
        Thread thread = new Thread(bc);
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            bc.shutdown();
        }
    }
}
