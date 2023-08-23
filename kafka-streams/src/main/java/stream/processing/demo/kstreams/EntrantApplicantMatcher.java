package stream.processing.demo.kstreams;

import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.processing.demo.PortEntryEnrichedRecord;
import stream.processing.demo.PortEntryRecord;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class EntrantApplicantMatcher implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(EntrantApplicantMatcher.class);

    private static final String DEFAULT_CONFIG_FILE = "streams.properties";

    private final Properties config;
    private final KafkaStreams streams;

    public EntrantApplicantMatcher(final Properties config) {
        this.config = config;
        streams = new KafkaStreams(getTopology(), config);
    }

    private Topology getTopology() {
        final String ENTRIES_TOPIC = config.getProperty("entries.topic");
        final String APPLICANTS_TOPIC = config.getProperty("applicants.topic");
        final String ENRICHED_TOPIC = config.getProperty("enriched.topic");
        final String APPLICANTS_TABLE = config.getProperty("applicants.table");
        // sanity check
        if (ENTRIES_TOPIC == null || APPLICANTS_TABLE == null || ENRICHED_TOPIC == null)
            throw new RuntimeException("Missing required property!");

        // configure Serdes
        final Serde<String> stringSerde = Serdes.String();
        final SpecificAvroSerde<PortEntryRecord> portEntrySerde = new SpecificAvroSerde<>();
        portEntrySerde.configure((Map)config, false);
        final SpecificAvroSerde<PortEntryEnrichedRecord> portEntryEnrichedSerde = new SpecificAvroSerde<>();
        portEntryEnrichedSerde.configure((Map)config, false);
        //final SpecificAvroSerde<stream.processing.demo.applicants> applicantSerde = new SpecificAvroSerde<>();
        //applicantSerde.configure((Map)config, false);
        final GenericAvroSerde genericAvroSerde = new GenericAvroSerde();
        genericAvroSerde.configure((Map)config, false);

        // build the streaming topology
        final  StreamsBuilder builder = new StreamsBuilder();

        //KTable<String, stream.processing.demo.applicants> applicantTable = builder.table(APPLICANTS_TOPIC, Consumed.with(stringSerde, applicantSerde));
        //KTable<String, GenericRecord> applicantTable = builder.table(APPLICANTS_TOPIC, Consumed.with(stringSerde, genericAvroSerde));
        KTable<String, GenericRecord> applicantTable = builder
                .stream(APPLICANTS_TOPIC, Consumed.with(stringSerde, genericAvroSerde))
                .selectKey((key, value) -> value.get("id").toString())
                .toTable(Named.as(APPLICANTS_TABLE));
        KStream<String, PortEntryRecord> entriesStream = builder.stream(ENTRIES_TOPIC, Consumed.with(stringSerde, portEntrySerde));

        applicantTable.toStream().peek((key, value) -> {
            logger.info("updating applicants table: [{}] {}", key, value);
        });

        entriesStream
                .peek((key, value) -> logger.info("got entry: [{}] {}", key, value))
                // re-key the stream - change from PORTNAME to ID
                .selectKey((key, value) -> value.getId())
                // Join the entries stream to the applicants table
                .leftJoin(applicantTable, (readOnlyKey, entryRecord, applicantRow) -> {
                    logger.info("joining records: [{}] {} {}", readOnlyKey, entryRecord, applicantRow);
                    // map the input stream values to the enriched data type
                    PortEntryEnrichedRecord.Builder enriched = PortEntryEnrichedRecord.newBuilder()
                            .setEntrantFirstName(entryRecord.getFirstName())
                            .setEntrantLastName(entryRecord.getLastName())
                            .setEntrantPhone(entryRecord.getPhone())
                            .setEntrantPortName(entryRecord.getPortName())
                            .setEntrantLatitude(entryRecord.getLatitude())
                            .setEntrantLongitude(entryRecord.getLongitude())
                            .setEntrantNotes(entryRecord.getNotes())
                            .setEntrantScanDate(entryRecord.getScanDate());

                    if (applicantRow != null) {
                        // if we have a matching applicant record, add its data
                        enriched
                                .setApplicantFirstName(applicantRow.get("first_name").toString())
                                .setApplicantLastName(applicantRow.get("last_name").toString())
                                .setApplicantPhone(applicantRow.get("phone").toString())
                                .setApplicantHomeAddress(applicantRow.get("home_address").toString())
                                .setApplicantStatus(applicantRow.get("status").toString());
                    }

                    return enriched.build();
                })
                .peek((key, value) -> logger.info("transformed entry: [{}] {}", key, value))
                // emit the enriched data to a new topic
                .to(ENRICHED_TOPIC, Produced.with(stringSerde, portEntryEnrichedSerde));

        return builder.build();
    }

    @Override
    public void run() {
        final CountDownLatch latch = new CountDownLatch(1);
        streams.setStateListener((newState, oldState) -> {
            if (oldState == KafkaStreams.State.RUNNING && newState != KafkaStreams.State.RUNNING) {
                latch.countDown();
            }
        });

        // Use this in development to clean the local streams state
        streams.cleanUp();
        // start the streams app
        streams.start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            logger.debug("EntrantApplicationMatcher closed!");
        }
    }

    public void stop() {
        streams.close();
    }

    public static void main(String ...args) throws IOException {
        // load passed in properties
        String configPath = args.length > 0 ? args[0] : DEFAULT_CONFIG_FILE;
        final Properties cfg = new Properties();
        cfg.load(new FileInputStream(configPath));

        final EntrantApplicantMatcher matcher = new EntrantApplicantMatcher(cfg);
        Runtime.getRuntime().addShutdownHook(new Thread(matcher::stop));
        matcher.run();
    }
}
