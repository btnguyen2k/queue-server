DROP TABLE IF EXISTS queue;
CREATE TABLE queue (
    queue_id                    BIGSERIAL,
    msg_org_timestamp           TIMESTAMP           NOT NULL,
    msg_timestamp               TIMESTAMP           NOT NULL,
    msg_num_requeues            INT                 NOT NULL DEFAULT 0,
    msg_content                 BYTEA,
    PRIMARY KEY (queue_id)
);

DROP TABLE IF EXISTS queue_ephemeral;
CREATE TABLE queue_ephemeral (
    queue_id                    BIGINT,
    msg_org_timestamp           TIMESTAMP           NOT NULL,
    msg_timestamp               TIMESTAMP           NOT NULL,
    msg_num_requeues            INT                 NOT NULL DEFAULT 0,
    msg_content                 BYTEA,
    PRIMARY KEY (queue_id)
);
CREATE INDEX queue_ephemeral_msg_timestamp ON queue_ephemeral(msg_timestamp);
