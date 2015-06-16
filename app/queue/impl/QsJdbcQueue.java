package queue.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import queue.IQsQueue;
import queue.internal.QueueMessage;

import com.github.ddth.queue.IQueueMessage;
import com.github.ddth.queue.impl.JdbcQueue;

/**
 * Universal JDBC-queue.
 * 
 * <p>
 * Queue/Ephemeral table structure:
 * </p>
 * 
 * <pre>
 *     queue_id              BIGINT     PRIMARY KEY
 *     msg_org_timestamp     DATETIME
 *     msg_timestamp         DATETIME
 *     msg_num_requeues      INT
 *     msg_content           BLOB
 * </pre>
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class QsJdbcQueue extends JdbcQueue implements IQsQueue {

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueMessage take() {
        return (QueueMessage) super.take();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QsJdbcQueue init() {
        super.init();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IQueueMessage readFromQueueStorage(JdbcTemplate jdbcTemplate) {
        final String SQL = "SELECT queue_id AS {1}, " + "msg_org_timestamp AS {2}, "
                + "msg_timestamp AS {3}, " + "msg_num_requeues AS {4}, " + "msg_content AS {5} "
                + "FROM {0} ORDER BY queue_id LIMIT 1";
        List<Map<String, Object>> dbRows = jdbcTemplate.queryForList(MessageFormat.format(SQL,
                getTableName(), QueueMessage.FIELD_QUEUE_ID, QueueMessage.FIELD_ORG_TIMESTAMP,
                QueueMessage.FIELD_TIMESTAMP, QueueMessage.FIELD_NUM_REQUEUES,
                QueueMessage.FIELD_CONTENT));
        if (dbRows != null && dbRows.size() > 0) {
            Map<String, Object> dbRow = dbRows.get(0);
            QueueMessage msg = new QueueMessage();
            return (IQueueMessage) msg.fromMap(dbRow);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<IQueueMessage> getOrphanFromEphemeralStorage(JdbcTemplate jdbcTemplate,
            long thresholdTimestampMs) {
        final String SQL = "SELECT queue_id AS {1}, " + "msg_org_timestamp AS {2}, "
                + "msg_timestamp AS {3}, " + "msg_num_requeues AS {4}, " + "msg_content AS {5} "
                + "FROM {0} WHERE msg_timestamp < ? ORDER BY queue_id";
        final Calendar threshold = Calendar.getInstance();
        threshold.add(Calendar.MILLISECOND, (int) (0 - thresholdTimestampMs));
        List<Map<String, Object>> dbRows = jdbcTemplate.queryForList(MessageFormat.format(SQL,
                getTableNameEphemeral(), QueueMessage.FIELD_QUEUE_ID,
                QueueMessage.FIELD_ORG_TIMESTAMP, QueueMessage.FIELD_TIMESTAMP,
                QueueMessage.FIELD_NUM_REQUEUES, QueueMessage.FIELD_CONTENT), threshold.getTime());
        if (dbRows != null && dbRows.size() > 0) {
            Collection<IQueueMessage> result = new ArrayList<IQueueMessage>();
            for (Map<String, Object> dbRow : dbRows) {
                QueueMessage msg = new QueueMessage();
                msg.fromMap(dbRow);
                result.add(msg);
            }
            return result;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean putToQueueStorage(JdbcTemplate jdbcTemplate, IQueueMessage _msg) {
        QueueMessage msg = (QueueMessage) _msg;
        final String SQL = "INSERT INTO {0} (msg_org_timestamp, msg_timestamp, msg_num_requeues, msg_content) VALUES (?, ?, ?, ?)";
        int numRows = jdbcTemplate.update(MessageFormat.format(SQL, getTableName()),
                msg.qOriginalTimestamp(), msg.qTimestamp(), msg.qNumRequeues(), msg.content());
        return numRows > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean putToEphemeralStorage(JdbcTemplate jdbcTemplate, IQueueMessage _msg) {
        QueueMessage msg = (QueueMessage) _msg;
        final String SQL = "INSERT INTO {0} (queue_id, msg_org_timestamp, msg_timestamp, msg_num_requeues, msg_content) VALUES (?, ?, ?, ?, ?)";
        int numRows = jdbcTemplate.update(MessageFormat.format(SQL, getTableNameEphemeral()),
                msg.qId(), msg.qOriginalTimestamp(), msg.qTimestamp(), msg.qNumRequeues(),
                msg.content());
        return numRows > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean removeFromQueueStorage(JdbcTemplate jdbcTemplate, IQueueMessage msg) {
        final String SQL = "DELETE FROM {0} WHERE queue_id=?";
        int numRows = jdbcTemplate.update(MessageFormat.format(SQL, getTableName()), msg.qId());
        return numRows > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean removeFromEphemeralStorage(JdbcTemplate jdbcTemplate, IQueueMessage msg) {
        final String SQL = "DELETE FROM {0} WHERE queue_id=?";
        int numRows = jdbcTemplate.update(MessageFormat.format(SQL, getTableNameEphemeral()),
                msg.qId());
        return numRows > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IQueueMessage readFromEphemeralStorage(JdbcTemplate jdbcTemplate, IQueueMessage _msg) {
        final String SQL = "SELECT queue_id AS {1}, " + "msg_org_timestamp AS {2}, "
                + "msg_timestamp AS {3}, " + "msg_num_requeues AS {4}, " + "msg_content AS {5} "
                + "FROM {0} WHERE queue_id = ? ORDER BY queue_id";
        List<Map<String, Object>> dbRows = jdbcTemplate.queryForList(MessageFormat.format(SQL,
                getTableNameEphemeral(), QueueMessage.FIELD_QUEUE_ID,
                QueueMessage.FIELD_ORG_TIMESTAMP, QueueMessage.FIELD_TIMESTAMP,
                QueueMessage.FIELD_NUM_REQUEUES, QueueMessage.FIELD_CONTENT), _msg.qId());
        if (dbRows != null && dbRows.size() > 0) {
            Map<String, Object> dbRow = dbRows.get(0);
            QueueMessage msg = new QueueMessage();
            return (IQueueMessage) msg.fromMap(dbRow);
        }
        return null;
    }

}
