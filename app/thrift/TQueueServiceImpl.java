package thrift;

import globals.Registry;

import org.apache.thrift.TException;

import play.Logger;
import queue.QueueApi;
import queue.internal.QueueMessage;

import com.github.btnguyen2k.queueserver.thrift.TQueueMessage;
import com.github.btnguyen2k.queueserver.thrift.TQueueResponse;
import com.github.btnguyen2k.queueserver.thrift.TQueueService;
import com.github.btnguyen2k.queueserver.thrift.TQueueSizeResponse;

/**
 * Queue Thrift Service implementation.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class TQueueServiceImpl implements TQueueService.Iface {

    public final static TQueueServiceImpl instance = new TQueueServiceImpl();

    /**
     * {@inheritDoc}
     */
    @Override
    public void ping() throws TException {
        Registry.incConcurrency();
        try {
        } finally {
            Registry.decConcurrency();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean ping2() throws TException {
        Registry.incConcurrency();
        try {
            return true;
        } finally {
            Registry.decConcurrency();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TQueueResponse queueExists(String _secret, String _queueName) throws TException {
        QueueApi queueApi = Registry.getQueueApi();
        try {
            if (!queueApi.authorize(_secret, _queueName)) {
                return doResponse(403, "Unauthorized!", false, null);
            }
            boolean result = queueApi.queueExists(_queueName);
            return doResponse(result ? 200 : 404, String.valueOf(result), result, null);
        } catch (Exception e) {
            final String logMsg = "Exception [" + e.getClass() + "]: " + e.getMessage();
            Logger.error(logMsg, e);
            return doResponse(500, logMsg, false, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TQueueResponse initQueue(String _secret, String _queueName) throws TException {
        QueueApi queueApi = Registry.getQueueApi();
        try {
            if (!queueApi.isValidQueueName(_queueName)) {
                return doResponse(400, "Invalid queue name [" + _queueName + "]!", false, null);
            }
            if (!queueApi.authorize(_secret, _queueName)) {
                return doResponse(403, "Unauthorized!", false, null);
            }
            boolean result = queueApi.initQueue(_queueName);
            return doResponse(200, String.valueOf(result), result, null);
        } catch (Exception e) {
            final String logMsg = "Exception [" + e.getClass() + "]: " + e.getMessage();
            Logger.error(logMsg, e);
            return doResponse(500, logMsg, false, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TQueueResponse queue(String _secret, String _queueName, TQueueMessage _message)
            throws TException {
        QueueApi queueApi = Registry.getQueueApi();
        try {
            if (!queueApi.isValidQueueName(_queueName) || _message == null) {
                return doResponse(400, "Invalid input!", false, null);
            }
            if (!queueApi.authorize(_secret, _queueName)) {
                return doResponse(403, "Unauthorized!", false, null);
            }
            QueueMessage message = QueueMessage.newInstance(_message);
            boolean result = queueApi.queue(_queueName, message);
            return doResponse(200, String.valueOf(result), result, null);
        } catch (Exception e) {
            final String logMsg = "Exception [" + e.getClass() + "]: " + e.getMessage();
            Logger.error(logMsg, e);
            return doResponse(500, logMsg, false, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TQueueResponse requeue(String _secret, String _queueName, TQueueMessage _message)
            throws TException {
        QueueApi queueApi = Registry.getQueueApi();
        try {
            if (!queueApi.isValidQueueName(_queueName) || _message == null) {
                return doResponse(400, "Invalid input!", false, null);
            }
            if (!queueApi.authorize(_secret, _queueName)) {
                return doResponse(403, "Unauthorized!", false, null);
            }
            QueueMessage message = QueueMessage.newInstance(_message);
            boolean result = queueApi.requeue(_queueName, message);
            return doResponse(200, String.valueOf(result), result, null);
        } catch (Exception e) {
            final String logMsg = "Exception [" + e.getClass() + "]: " + e.getMessage();
            Logger.error(logMsg, e);
            return doResponse(500, logMsg, false, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TQueueResponse requeueSilent(String _secret, String _queueName, TQueueMessage _message)
            throws TException {
        QueueApi queueApi = Registry.getQueueApi();
        try {
            if (!queueApi.isValidQueueName(_queueName) || _message == null) {
                return doResponse(400, "Invalid input!", false, null);
            }
            if (!queueApi.authorize(_secret, _queueName)) {
                return doResponse(403, "Unauthorized!", false, null);
            }
            QueueMessage message = QueueMessage.newInstance(_message);
            boolean result = queueApi.requeueSilent(_queueName, message);
            return doResponse(200, String.valueOf(result), result, null);
        } catch (Exception e) {
            final String logMsg = "Exception [" + e.getClass() + "]: " + e.getMessage();
            Logger.error(logMsg, e);
            return doResponse(500, logMsg, false, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TQueueResponse finish(String _secret, String _queueName, TQueueMessage _message)
            throws TException {
        QueueApi queueApi = Registry.getQueueApi();
        try {
            if (!queueApi.isValidQueueName(_queueName) || _message == null) {
                return doResponse(400, "Invalid input!", false, null);
            }
            if (!queueApi.authorize(_secret, _queueName)) {
                return doResponse(403, "Unauthorized!", false, null);
            }
            QueueMessage message = QueueMessage.newInstance(_message);
            boolean result = queueApi.finish(_queueName, message);
            return doResponse(200, String.valueOf(result), result, null);
        } catch (Exception e) {
            final String logMsg = "Exception [" + e.getClass() + "]: " + e.getMessage();
            Logger.error(logMsg, e);
            return doResponse(500, logMsg, false, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TQueueResponse take(String _secret, String _queueName) throws TException {
        QueueApi queueApi = Registry.getQueueApi();
        try {
            if (!queueApi.isValidQueueName(_queueName)) {
                return doResponse(400, "Invalid queue name [" + _queueName + "]!", false, null);
            }
            if (!queueApi.authorize(_secret, _queueName)) {
                return doResponse(403, "Unauthorized!", false, null);
            }
            QueueMessage result = queueApi.take(_queueName);
            return doResponse(200, "", result != null, result);
        } catch (Exception e) {
            final String logMsg = "Exception [" + e.getClass() + "]: " + e.getMessage();
            Logger.error(logMsg, e);
            return doResponse(500, logMsg, false, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TQueueSizeResponse queueSize(String _secret, String _queueName) throws TException {
        QueueApi queueApi = Registry.getQueueApi();
        try {
            if (!queueApi.isValidQueueName(_queueName)) {
                return doSizeResponse(400, "Invalid queue name [" + _queueName + "]!", -1);
            }
            if (!queueApi.authorize(_secret, _queueName)) {
                return doSizeResponse(403, "Unauthorized!", -1);
            }
            int result = queueApi.queueSize(_queueName);
            return doSizeResponse(200, "", result);
        } catch (Exception e) {
            final String logMsg = "Exception [" + e.getClass() + "]: " + e.getMessage();
            Logger.error(logMsg, e);
            return doSizeResponse(500, logMsg, -1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TQueueSizeResponse ephemeralSize(String _secret, String _queueName) throws TException {
        QueueApi queueApi = Registry.getQueueApi();
        try {
            if (!queueApi.isValidQueueName(_queueName)) {
                return doSizeResponse(400, "Invalid queue name [" + _queueName + "]!", -1);
            }
            if (!queueApi.authorize(_secret, _queueName)) {
                return doSizeResponse(403, "Unauthorized!", -1);
            }
            int result = queueApi.ephemeralSize(_queueName);
            return doSizeResponse(200, "", result);
        } catch (Exception e) {
            final String logMsg = "Exception [" + e.getClass() + "]: " + e.getMessage();
            Logger.error(logMsg, e);
            return doSizeResponse(500, logMsg, -1);
        }
    }

    private static TQueueResponse doResponse(int status, String message, boolean result,
            QueueMessage queueMessage) {
        TQueueMessage msg = queueMessage != null ? new TQueueMessage() : null;
        if (msg != null) {
            msg.setQueueId(queueMessage.queueId());
            msg.setMsgContent(queueMessage.content());
            msg.setMsgNumRequeues(queueMessage.qNumRequeues());
            msg.setMsgOrgTimestamp(queueMessage.qOriginalTimestamp() != null ? queueMessage
                    .qOriginalTimestamp().getTime() : 0);
            msg.setMsgTimestamp(queueMessage.qTimestamp() != null ? queueMessage.qTimestamp()
                    .getTime() : 0);
        }
        TQueueResponse response = new TQueueResponse(status, message, result, msg);
        Registry.updateCounters(status);
        return response;
    }

    private static TQueueSizeResponse doSizeResponse(int status, String message, long size) {
        TQueueSizeResponse response = new TQueueSizeResponse(status, message, size);
        Registry.updateCounters(status);
        return response;
    }
}
