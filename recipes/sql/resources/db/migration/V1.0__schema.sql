CREATE TABLE person (
  id      BIGINT AUTO_INCREMENT PRIMARY KEY,
  name    VARCHAR(200),
  age     INTEGER,
  address VARCHAR(200)
);

INSERT INTO person (name, age, address) VALUES ('Joe', 42, '555 Nowhere');
INSERT INTO person (name, age, address) VALUES ('Sam', 19, '1020 Elsewhere');
