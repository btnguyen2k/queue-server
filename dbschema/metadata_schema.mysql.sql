DROP TABLE IF EXISTS queue_metadata;
CREATE TABLE queue_metadata (
    queue_name                  VARCHAR(64)         NOT NULL,
        PRIMARY KEY (queue_name),
    queue_timestamp_create      DATETIME            NOT NULL,
    queue_settings              TEXT
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
