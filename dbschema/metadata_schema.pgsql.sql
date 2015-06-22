DROP TABLE IF EXISTS queue_metadata;
CREATE TABLE queue_metadata (
    queue_name                  VARCHAR(64)         NOT NULL,
    queue_timestamp_create      TIMESTAMP           NOT NULL,
    queue_settings              TEXT,
    PRIMARY KEY (queue_name)
);