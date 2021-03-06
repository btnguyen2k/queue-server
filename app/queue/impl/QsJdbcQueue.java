package queue.impl;

import queue.IQsQueue;
import queue.internal.QueueMessage;

import com.github.ddth.queue.impl.UniversalJdbcQueue;

/**
 * A JDBC queue that extends {@link UniversalJdbcQueue} and implements
 * {@link IQsQueue}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class QsJdbcQueue extends UniversalJdbcQueue implements IQsQueue {

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueMessage take() {
        return QueueMessage.newInstance(super.take());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QsJdbcQueue init() {
        super.init();
        return this;
    }

}
