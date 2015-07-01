package queue.impl;

import queue.IQsQueue;
import queue.internal.QueueMessage;

import com.github.ddth.queue.impl.UniversalRedisQueue;

/**
 * A Redis queue that extends {@link UniversalRedisQueue} and implements
 * {@link IQsQueue}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.3.0
 */
public class QsRedisQueue extends UniversalRedisQueue implements IQsQueue {

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
    public QsRedisQueue init() {
        super.init();
        return this;
    }

}
