package queue;

import queue.internal.QueueMessage;

import com.github.ddth.queue.IQueue;

/**
 * Queue interface that overrides some methods in the original {@link IQueue}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public interface IQsQueue extends IQueue {
    @Override
    public QueueMessage take();

    public IQsQueue init();

    public void destroy();
}
