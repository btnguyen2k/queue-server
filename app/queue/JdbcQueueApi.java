package queue;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import play.Logger;
import queue.impl.QsJdbcQueue;

import com.github.ddth.commons.utils.DPathUtils;

/**
 * JDBC-implementation of {@link QueueApi}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public abstract class JdbcQueueApi extends QueueApi {

    private DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Gets a new connections.
     * 
     * @return
     * @throws SQLException
     */
    protected Connection connection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Constructs a {@link JdbcTemplate} from a connection.
     * 
     * @param conn
     * @return
     */
    protected JdbcTemplate jdbcTemplate(Connection conn) {
        DataSource ds = new SingleConnectionDataSource(conn, true);
        return new JdbcTemplate(ds);
    }

    /*----------------------------------------------------------------------*/
    // Metadata methods

    protected boolean initQueueMetadata(String queueName) {
        if (!isValidQueueName(queueName)) {
            return false;
        }

        String normalizedQueueName = normalizeQueueName(queueName);
        try {
            Connection conn = connection();
            try {
                conn.setAutoCommit(true);
                JdbcTemplate jdbcTemplate = jdbcTemplate(conn);

                final String SQL = "INSERT INTO {0} (queue_name, queue_timestamp_create) VALUES (?, ?)";
                jdbcTemplate.update(MessageFormat.format(SQL, getTableMetadata()),
                        normalizedQueueName, new Date());
                return true;
            } catch (DuplicateKeyException dke) {
                // EMPTY
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            Logger.error(e.getMessage(), e);
        }
        return false;
    }

    protected Collection<String> getAllQueueNames() {
        Collection<String> allQueueNames = new HashSet<String>();
        try {
            Connection conn = connection();
            try {
                conn.setAutoCommit(true);
                JdbcTemplate jdbcTemplate = jdbcTemplate(conn);

                final String SQL = "SELECT * FROM {0}";
                List<Map<String, Object>> dbRows = jdbcTemplate.queryForList(MessageFormat.format(
                        SQL, getTableMetadata()));
                if (dbRows != null) {
                    for (Map<String, Object> dbRow : dbRows) {
                        String queueName = DPathUtils.getValue(dbRow, "queue_name", String.class);
                        if (!StringUtils.isBlank(queueName)) {
                            allQueueNames.add(queueName);
                        }
                    }
                }
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            Logger.error(e.getMessage(), e);
        }
        return allQueueNames;
    }

    /*----------------------------------------------------------------------*/
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean queueExists(String queueName) {
        if (!isValidQueueName(queueName)) {
            return false;
        }

        boolean result = true;
        try {
            Connection conn = connection();
            try {
                conn.setAutoCommit(true);
                String table1 = "queue_" + normalizeQueueName(queueName);
                try {
                    conn.createStatement().execute(
                            MessageFormat.format("SELECT 0 FROM {0}", table1));
                } catch (SQLException e) {
                    result = false;
                }

                String table2 = "queue_" + normalizeQueueName(queueName) + "_ephemeral";
                try {
                    conn.createStatement().execute(
                            MessageFormat.format("SELECT 0 FROM {0}", table2));
                } catch (SQLException e) {
                    result = false;
                }
            } finally {
                conn.close();
            }

            return result;
        } catch (SQLException e) {
            Logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void invalidateQueue(IQsQueue queue) {
        queue.destroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IQsQueue createNewQueueInstance(String normalizedQueueName) {
        QsJdbcQueue jdbcQueue = new QsJdbcQueue();
        jdbcQueue.setTableName("queue_" + normalizedQueueName)
                .setTableNameEphemeral("queue_" + normalizedQueueName + "_ephemeral")
                .setDataSource(dataSource);
        jdbcQueue.init();
        return jdbcQueue;
    }
}
