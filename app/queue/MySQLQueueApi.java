package queue;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;

import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;

import play.Logger;

/**
 * MySQL-Specific {@link JdbcQueueApi}.
 * 
 * <p>
 * Table schema:
 * </p>
 * 
 * <pre>
 * CREATE TABLE queue (
 *   queue_id                    BIGINT              AUTO_INCREMENT,
 *       PRIMARY KEY (queue_id),
 *   msg_org_timestamp           DATETIME            NOT NULL,
 *   msg_timestamp               DATETIME            NOT NULL,
 *   msg_num_requeues            INT                 NOT NULL DEFAULT 0,
 *   msg_content                 LONGBLOB
 * ) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
 * CREATE TABLE queue_ephemeral (
 *   queue_id                    BIGINT,
 *       PRIMARY KEY (queue_id),
 *   msg_org_timestamp           DATETIME            NOT NULL,
 *   msg_timestamp               DATETIME            NOT NULL,
 *       INDEX (msg_timestamp),
 *   msg_num_requeues            INT                 NOT NULL DEFAULT 0,
 *   msg_content                 LONGBLOB
 * ) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
 * </pre>
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class MySQLQueueApi extends JdbcQueueApi {

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
                final String SQL_CREATE_QUEUE = "CREATE TABLE {0} (queue_id BIGINT AUTO_INCREMENT, PRIMARY KEY (queue_id),"
                        + "msg_org_timestamp DATETIME NOT NULL,"
                        + "msg_timestamp DATETIME NOT NULL,"
                        + "msg_num_requeues INT NOT NULL DEFAULT 0,"
                        + "msg_content LONGBLOB)"
                        + "ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci";
                final String SQL_CREATE_EPHEMERAL = "CREATE TABLE {0} (queue_id BIGINT, PRIMARY KEY (queue_id),"
                        + "msg_org_timestamp DATETIME NOT NULL,"
                        + "msg_timestamp DATETIME NOT NULL,INDEX (msg_timestamp),"
                        + "msg_num_requeues INT NOT NULL DEFAULT 0,"
                        + "msg_content LONGBLOB)"
                        + "ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci";
                JdbcTemplate jdbcTemplate = jdbcTemplate(conn);
                try {
                    String tableName = "queue_" + normalizedQueueName;
                    jdbcTemplate.execute(MessageFormat.format(SQL_CREATE_QUEUE, tableName));
                } catch (BadSqlGrammarException bsge) {
                    // IGNORE
                } catch (Exception e) {
                    Logger.warn(e.getMessage(), e);
                }
                try {
                    String tableName = "queue_" + normalizedQueueName + "_ephemeral";
                    jdbcTemplate.execute(MessageFormat.format(SQL_CREATE_EPHEMERAL, tableName));
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
