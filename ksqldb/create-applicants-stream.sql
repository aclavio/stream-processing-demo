CREATE OR REPLACE TABLE applicants_table (
    id string PRIMARY KEY,
    first_name string,
    last_name string,
    phone string,
    home_address string,
    status string,
    modified timestamp
) WITH (
    kafka_topic = 'postgresql.applicants',
    key_format = 'kafka',
    value_format = 'avro'
);

CREATE OR REPLACE STREAM entries_stream WITH (kafka_topic = 'port.entries.avro', value_format = 'avro');
CREATE OR REPLACE STREAM entries_id_stream WITH (
    kafka_topic = 'port.entries.id.avro',
    value_format = 'avro'
)
AS SELECT *
FROM entries_stream
PARTITION BY id;


CREATE OR REPLACE STREAM applicants_enriched_stream
WITH (
    kafka_topic = 'port.entries.enriched.ksql.avro',
    key_format = 'kafka',
    value_format = 'avro'
)
AS SELECT
    es.id AS id,
    es.firstname AS entrant_first_name,
    es.lastname AS entrant_last_name,
    es.phone AS entrant_phone,
    es.notes AS entrant_notes,
    ap.first_name AS applicant_first_name,
    ap.last_name AS applicant_last_name,
    ap.phone AS applicant_phone,
    ap.home_address AS applicant_home_address,
    ap.status AS applicant_status
FROM entries_id_stream es
LEFT JOIN applicants_table ap
ON es.id = ap.id
EMIT CHANGES;
