package queue;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;

import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;

import play.Logger;

/**
 * PostgreSQL-Specific {@link JdbcQueueApi}.
 * 
 * <p>
 * Table schema:
 * </p>
 * 
 * <pre>
 * CREATE TABLE queue (
 *     queue_id                    BIGSERIAL,
 *     msg_org_timestamp           TIMESTAMP           NOT NULL,
 *     msg_timestamp               TIMESTAMP           NOT NULL,
 *     msg_num_requeues            INT                 NOT NULL DEFAULT 0,
 *     msg_content                 BYTEA,
 *     PRIMARY KEY (queue_id)
 * );
 * CREATE TABLE queue_ephemeral (
 *     queue_id                    BIGINT,
 *     msg_org_timestamp           TIMESTAMP           NOT NULL,
 *     msg_timestamp               TIMESTAMP           NOT NULL,
 *     msg_num_requeues            INT                 NOT NULL DEFAULT 0,
 *     msg_content                 BYTEA,
 *     PRIMARY KEY (queue_id)
 * );
 * CREATE INDEX queue_ephemeral_msg_timestamp ON queue_ephemeral(msg_timestamp);
 * </pre>
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.2.0
 */
public class PgSQLQueueApi extends JdbcQueueApi {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean initQueue(String queueName) {
        if (!isValidQueueName(queueName)) {
            return false;
        }

        String normalizedQueueName = normalizeQueueName(queueName);
        try {
            Connection conn = connection();
            try {
                conn.setAutoCommit(true);
                final String SQL_CREATE_QUEUE = "CREATE TABLE {0} (queue_id BIGSERIAL,"
                        + "msg_org_timestamp TIMESTAMP NOT NULL,"
                        + "msg_timestamp TIMESTAMP NOT NULL,"
                        + "msg_num_requeues INT NOT NULL DEFAULT 0," + "msg_content BYTEA,"
                        + "PRIMARY KEY (queue_id))";
                JdbcTemplate jdbcTemplate = jdbcTemplate(conn);
                try {
                    String tableName = "queue_" + normalizedQueueName;
                    jdbcTemplate.execute(MessageFormat.format(SQL_CREATE_QUEUE, tableName));
                } catch (BadSqlGrammarException bsge) {
                    // IGNORE
                } catch (Exception e) {
                    Logger.warn(e.getMessage(), e);
                }

                final String SQL_CREATE_EPHEMERAL = "CREATE TABLE {0} (queue_id BIGINT,"
                        + "msg_org_timestamp TIMESTAMP NOT NULL,"
                        + "msg_timestamp TIMESTAMP NOT NULL,"
                        + "msg_num_requeues INT NOT NULL DEFAULT 0," + "msg_content BYTEA,"
                        + "PRIMARY KEY (queue_id))";
                try {
                    String tableName = "queue_" + normalizedQueueName + "_ephemeral";
                    jdbcTemplate.execute(MessageFormat.format(SQL_CREATE_EPHEMERAL, tableName));
                } catch (BadSqlGrammarException bsge) {
                    // IGNORE
                } catch (Exception e) {
                    Logger.warn(e.getMessage(), e);
                }

                final String SQL_CREATE_EPHEMERAL_INDEX = "CREATE INDEX {0}_msg_timestamp ON {0}(msg_timestamp)";
                try {
                    String tableName = "queue_" + normalizedQueueName + "_ephemeral";
                    jdbcTemplate.execute(MessageFormat
                            .format(SQL_CREATE_EPHEMERAL_INDEX, tableName));
                } catch (BadSqlGrammarException bsge) {
                    // IGNORE
                } catch (Exception e) {
                    Logger.warn(e.getMessage(), e);
                }

                return queueExists(queueName) && initQueueMetadata(queueName);
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            Logger.error(e.getMessage(), e);
        }
        return false;
    }

}
