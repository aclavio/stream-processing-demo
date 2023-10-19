INSERT INTO "applicants" ("first_name", "last_name", "phone", "home_address", "status")
VALUES ('Bilbo', 'Baggins', '555-5555', '123 Bag End, Hobbiton, the Shire.', 'PENDING');

INSERT INTO "applicants" ("first_name", "last_name", "phone", "home_address", "status")
VALUES ('Frodo', 'Baggins', '555-5555', '123 Bag End, Hobbiton, the Shire.', 'APPROVED');

INSERT INTO "applicants" ("first_name", "last_name", "phone", "home_address", "status")
VALUES ('Peregrin', 'Took', '555-0001', '', 'DENIED');

INSERT INTO "applicants" ("first_name", "last_name", "phone", "home_address", "status")
VALUES ('Samwise', 'Gamgee', '555-0002', '52 Something Lane, Hobbiton, the Shire', 'PENDING');

INSERT INTO "applicants" ("first_name", "last_name", "phone", "home_address", "status")
VALUES ('Meriadoc', 'Brandybuck', '555-0003', '1 Buckland Way, Hobbiton, the Shire', 'PENDING');

INSERT INTO "applicants" ("first_name", "last_name", "phone", "home_address", "status", "modified")
VALUES ('Aragorn', 'Elessar', '555-1234', '1 Main Street, Gondor', 'PENDING');


UPDATE "applicants"
SET "phone" = '555-1112', "modified" = now()
WHERE id = 2;

SELECT id, COUNT(*) AS num_updates
FROM  APPLICANTS_STREAM
WINDOW TUMBLING (SIZE 2 MINUTES)
GROUP BY id HAVING COUNT(*) > 2
EMIT CHANGES;