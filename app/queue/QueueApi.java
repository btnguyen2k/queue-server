package queue;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import play.Logger;
import queue.internal.QueueMessage;

import com.github.ddth.queue.IQueue;
import com.github.ddth.queue.IQueueMessage;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * APIs to interact with queues.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public abstract class QueueApi {

    /*----------------------------------------------------------------------*/
    private String tableMetadata = "queue_metadata";

    public String getTableMetadata() {
        return tableMetadata;
    }

    public QueueApi setTableMetadata(String tableMetadata) {
        this.tableMetadata = tableMetadata;
        return this;
    }

    protected abstract boolean initQueueMetadata(String queueName);

    protected abstract Collection<String> getAllQueueNames();

    /*----------------------------------------------------------------------*/

    private LoadingCache<String, IQsQueue> cache = CacheBuilder.newBuilder()
            .removalListener(new RemovalListener<String, IQsQueue>() {
                @Override
                public void onRemoval(RemovalNotification<String, IQsQueue> entry) {
                    invalidateQueue(entry.getValue());
                }
            }).build(new CacheLoader<String, IQsQueue>() {
                @Override
                public IQsQueue load(String queueName) throws Exception {
                    return createNewQueueInstance(queueName);
                }
            });

    private ConcurrentSkipListSet<String> check = new ConcurrentSkipListSet<String>();
    private CheckThread checkThread;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    private class CheckThread extends Thread {
        private boolean running = true;

        public void stopWork() {
            this.running = false;
        }

        public void run() {
            while (running && !interrupted()) {
                String queueName = check.pollFirst();
                while (queueName != null) {
                    final String normalizedQueueName = normalizeQueueName(queueName);
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            IQsQueue queue = getQueue(normalizedQueueName);
                            if (queue != null) {
                                Collection<IQueueMessage> orphanMsgs = queue
                                        .getOrphanMessages(getOrphanMessageThresholdMs());
                                if (orphanMsgs != null) {
                                    for (IQueueMessage msg : orphanMsgs) {
                                        if (orphanMessagePolicy == ORPHAN_MESSAGE_POLICY_DISCARD) {
                                            Logger.info(MessageFormat
                                                    .format("Discarding orphan message [{0}] from queue [{1}]...",
                                                            msg.qId(), normalizedQueueName));
                                            queue.finish(msg);
                                        } else {
                                            Logger.info(MessageFormat
                                                    .format("Requeueing orphan message [{0}] from queue [{1}]...",
                                                            msg.qId(), normalizedQueueName));
                                            queue.moveFromEphemeralToQueueStorage(msg);
                                        }
                                    }
                                }
                                if (queue.ephemeralSize() > 0) {
                                    check.add(normalizedQueueName);
                                }
                            }
                        }
                    });
                    queueName = check.pollFirst();
                }

                try {
                    Thread.sleep(Math.min(10000, getOrphanMessageThresholdMs()) / 2);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public QueueApi init() {
        checkThread = new CheckThread();
        checkThread.setDaemon(true);
        checkThread.start();

        Collection<String> allQueueNames = getAllQueueNames();
        if (allQueueNames != null) {
            Logger.info("Startup: Queue(s) to check for orphan messages: " + allQueueNames);
            check.addAll(allQueueNames);
        }

        return this;
    }

    public void destroy() {
        checkThread.stopWork();
    }

    /**
     * Discard orphan messages.
     */
    public final static int ORPHAN_MESSAGE_POLICY_DISCARD = 0;

    /**
     * Re-queue orphan messages.
     */
    public final static int ORPHAN_MESSAGE_POLICY_REQUEUE = 1;

    private int orphanMessagePolicy = ORPHAN_MESSAGE_POLICY_DISCARD;

    private long orphanMessageThresholdMs = 10000; // default 10 seconds

    /**
     * Number of milliseconds before a message is marked orphan.
     * 
     * @return
     */
    public long getOrphanMessageThresholdMs() {
        return this.orphanMessageThresholdMs;
    }

    /**
     * Set number of milliseconds before a message is marked orphan.
     * 
     * @param orphanThresholdMs
     * @return
     */
    public QueueApi setOrphanMessageThresholdMs(long orphanMessageThresholdMs) {
        this.orphanMessageThresholdMs = orphanMessageThresholdMs;
        return this;
    }

    /**
     * Get orphan message policy. See {@link #ORPHAN_MESSAGE_POLICY_DISCARD} and
     * {@link #ORPHAN_MESSAGE_POLICY_REQUEUE}.
     * 
     * @return
     */
    public int getOrphanMessagePolicy() {
        return this.orphanMessagePolicy;
    }

    /**
     * Set orphan message policy.
     * 
     * @param orphanMessagePolicy
     *            See {@link #ORPHAN_MESSAGE_POLICY_DISCARD} and
     *            {@link #ORPHAN_MESSAGE_POLICY_REQUEUE}
     * @return
     */
    public QueueApi setOrphanMessagePolicy(int orphanMessagePolicy) {
        this.orphanMessagePolicy = orphanMessagePolicy;
        return this;
    }

    /**
     * Invalidate a queue object.
     * 
     * @param queue
     */
    protected abstract void invalidateQueue(IQsQueue queue);

    /**
     * Creates a new queue object.
     * 
     * <p>
     * </p>
     * 
     * @param normalizedQueueName
     * @return
     */
    protected abstract IQsQueue createNewQueueInstance(String normalizedQueueName);

    /**
     * Tests if a queue's name is valid.
     * 
     * @param queueName
     * @return
     */
    public boolean isValidQueueName(String queueName) {
        String normalizeQueueName = normalizeQueueName(queueName);
        return normalizeQueueName.matches("^[a-z0-9]+$");
    }

    /**
     * Always returns {@code true} for now.
     * 
     * @param secret
     * @param queueName
     * @return
     */
    public boolean authorize(String secret, String queueName) {
        return true;
    }

    /**
     * Gets a queue instance from cache.
     * 
     * @param queueName
     * @return
     */
    protected IQsQueue getQueue(String queueName) {
        if (!isValidQueueName(queueName)) {
            return null;
        }

        try {
            boolean queueExists = queueExists(queueName);
            return queueExists ? cache.get(normalizeQueueName(queueName)) : null;
        } catch (ExecutionException e) {
            return null;
        }
    }

    /**
     * Normalizes queue's name.
     * 
     * @param queueName
     * @return
     */
    protected String normalizeQueueName(String queueName) {
        return queueName.trim().toLowerCase();
    }

    /*----------------------------------------------------------------------*/

    /**
     * Tests if a queue exists.
     * 
     * @param queueName
     * @return
     */
    public abstract boolean queueExists(String queueName);

    /**
     * Creates & Initializes a new queue.
     * 
     * @param queueName
     * @return
     */
    public abstract boolean initQueue(String queueName);

    /**
     * Puts a message to a queue specified by {@code queueName}.
     * 
     * @param queueName
     * @param msg
     * @return
     */
    public boolean queue(String queueName, IQueueMessage msg) {
        IQueue queue = getQueue(queueName);
        if (queue != null) {
            return queue.queue(msg);
        }
        return false;
    }

    /**
     * Re-queue a message.
     * 
     * @param queueName
     * @param msg
     * @return
     */
    public boolean requeue(String queueName, IQueueMessage msg) {
        IQueue queue = getQueue(queueName);
        if (queue != null) {
            return queue.requeue(msg);
        }
        return false;
    }

    /**
     * Re-queue a message "silently".
     * 
     * @param queueName
     * @param msg
     * @return
     */
    public boolean requeueSilent(String queueName, IQueueMessage msg) {
        IQueue queue = getQueue(queueName);
        if (queue != null) {
            return queue.requeueSilent(msg);
        }
        return false;
    }

    /**
     * Called when finish processing the message to cleanup ephemeral storage.
     * 
     * @param queueName
     * @param msg
     * @return
     */
    public boolean finish(String queueName, IQueueMessage msg) {
        IQueue queue = getQueue(queueName);
        if (queue != null) {
            queue.finish(msg);
            return true;
        }
        return false;
    }

    /**
     * Takes a message from queue specified by {@code queueName}.
     * 
     * @param queueName
     * @return
     */
    public QueueMessage take(String queueName) {
        IQsQueue queue = getQueue(queueName);
        if (queue != null) {
            QueueMessage result = queue.take();
            if (result != null) {
                check.add(queueName);
            }
            return result;
        }
        return null;
    }

    /**
     * Gets number of items currently in the queue specified by
     * {@code queueName}.
     * 
     * @param queueName
     * @return
     */
    public int queueSize(String queueName) {
        IQueue queue = getQueue(queueName);
        if (queue != null) {
            return queue.queueSize();
        }
        return -1;
    }

    /**
     * Gets number of items currently in the queue's - specified by
     * {@code queueName} - ephemeral storage.
     * 
     * @param queueName
     * @return
     */
    public int ephemeralSize(String queueName) {
        IQueue queue = getQueue(queueName);
        if (queue != null) {
            return queue.ephemeralSize();
        }
        return -1;
    }

}
