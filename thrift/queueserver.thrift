/**
 * Thrift definition file for queue-server.
 * Version: 0.1.0
 */

namespace java com.github.btnguyen2k.queueserver.thrift

struct TQueueMessage {
    1: i64 queueId = 0,
    2: i64 msgOrgTimestamp = 0,
    3: i64 msgTimestamp = 0,
    4: i32 msgNumRequeues = 0,
    5: binary msgContent
}

struct TQueueResponse {
    1: i32 status,
    2: string message,
    3: bool result,
    4: TQueueMessage queueMessage
}

struct TQueueSizeResponse {
    1: i32 status,
    2: string message,
    3: i64 size
}

service TQueueService {
    /**
     * "Ping" the server. This method is to test if server is reachable.
     */
    oneway void ping(),
    
    /**
     * "Ping" the server. This method is to test if server is reachable.
     */
    bool ping2(),
    
    /**
     * Checks if a queue exists.
     *
     * @param _secret
     * @param _queueName
     * @return {@code status == 200} if queue exists, {@code status == 404} otherwise; {@code status == 500} means "exception/error on server"
     */
    TQueueResponse queueExists(1: string _secret, 2: string _queueName),
    
    /**
     * Creates & Initializes a new queue.
     *
     * @param _secret
     * @param _queueName
     * @return {@code status == 200} if successful; {@code status == 500} means "exception/error on server"
     */
    TQueueResponse initQueue(1: string _secret, 2: string _queueName),
    
    /**
     * Puts a message to a queue.
     *
     * @param _secret
     * @param _queueName
     * @param _message
     * @return {@code status == 200} if successful, {@code status == 404} if queue does not exist; {@code status == 500} means "exception/error on server"
     */
    TQueueResponse queue(1: string _secret, 2: string _queueName, 3: TQueueMessage _message),
    
    /**
     * Re-queues a message.
     *
     * @param _secret
     * @param _queueName
     * @param _message
     * @return {@code status == 200} if successful, {@code status == 404} if queue does not exist; {@code status == 500} means "exception/error on server"
     */
    TQueueResponse requeue(1: string _secret, 2: string _queueName, 3: TQueueMessage _message),
    
    /**
     * Re-queues a message "silently".
     *
     * @param _secret
     * @param _queueName
     * @param _message
     * @return {@code status == 200} if successful, {@code status == 404} if queue does not exist; {@code status == 500} means "exception/error on server"
     */
    TQueueResponse requeueSilent(1: string _secret, 2: string _queueName, 3: TQueueMessage _message),
    
    /**
     * Called when finish processing the message to cleanup ephemeral storage.
     *
     * @param _secret
     * @param _queueName
     * @param _message
     * @return {@code status == 200} if successful, {@code status == 404} if queue does not exist; {@code status == 500} means "exception/error on server"
     */
    TQueueResponse finish(1: string _secret, 2: string _queueName, 3: TQueueMessage _message),
    
    /**
     * Takes a message from a queue.
     *
     * @param _secret
     * @param _queueName
     * @return {@code status == 200} if successful, {@code status == 404} if queue does not exist; {@code status == 500} means "exception/error on server"
     */
    TQueueResponse take(1: string _secret, 2: string _queueName),
    
    /**
     * Gets number of items currently in a queue.
     *
     * @param _secret
     * @param _queueName
     * @return {@code status == 200} if successful, {@code status == 404} if queue does not exist; {@code status == 500} means "exception/error on server"
     */
    TQueueSizeResponse queueSize(1: string _secret, 2: string _queueName),
    
    /**
     * Gets number of items currently in a queue's ephemeral storage.
     *
     * @param _secret
     * @param _queueName
     * @return {@code status == 200} if successful, {@code status == 404} if queue does not exist; {@code status == 500} means "exception/error on server"
     */
    TQueueSizeResponse ephemeralSize(1: string _secret, 2: string _queueName),
}
