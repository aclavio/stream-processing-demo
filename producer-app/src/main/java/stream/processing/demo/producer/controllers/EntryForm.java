package stream.processing.demo.producer.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import stream.processing.demo.PortEntryRecord;
import stream.processing.demo.producer.controllers.pojos.EntryFormRequest;
import stream.processing.demo.producer.kafka.EntryScanKafkaProducer;

import java.time.Instant;
import java.util.UUID;

@Controller
public class EntryForm {

    protected static final Logger logger = LoggerFactory.getLogger(EntryForm.class);

    @Autowired
    private EntryScanKafkaProducer producer;

    @PostMapping(value = "/publish", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity publishRecord(@RequestBody EntryFormRequest request) {
        // Transform the form data into the canonical Avro data
        PortEntryRecord record = new PortEntryRecord();
        record.setId(
                StringUtils.hasLength(request.getId()) ?
                        request.getId() :
                        UUID.randomUUID().toString());
        record.setScanDate(Instant.now());
        record.setFirstName(request.getFirstName());
        record.setLastName(request.getLastName());
        record.setPortName(request.getPortName());
        record.setLatitude(request.getLatitude());
        record.setLongitude(request.getLongitude());
        record.setPhone(request.getPhone());
        record.setNotes(request.getNotes());

        logger.info("Publishing record: {}", record);
        // Publish the record to Kafka
        producer.publishEntryRecord(record);

        return new ResponseEntity(HttpStatus.CREATED);
    }

}
