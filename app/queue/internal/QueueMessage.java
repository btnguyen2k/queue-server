package queue.internal;

import java.util.Date;

import com.github.btnguyen2k.queueserver.thrift.TQueueMessage;
import com.github.ddth.queue.UniversalQueueMessage;

public class QueueMessage extends UniversalQueueMessage {

    public static QueueMessage newInstance() {
        Date now = new Date();
        QueueMessage msg = new QueueMessage();
        msg.qId(0).qNumRequeues(0).qOriginalTimestamp(now).qTimestamp(now);
        return msg;
    }

    public static QueueMessage newInstance(TQueueMessage _queueMsg) {
        if (_queueMsg == null) {
            return null;
        }
        QueueMessage msg = newInstance();
        msg.content(_queueMsg.getMsgContent()).qId(_queueMsg.getQueueId())
                .qNumRequeues(_queueMsg.getMsgNumRequeues())
                .qOriginalTimestamp(new Date(_queueMsg.getMsgOrgTimestamp()))
                .qTimestamp(new Date(_queueMsg.getMsgTimestamp()));
        return msg;
    }

    public static QueueMessage newInstance(long queueId, Date orgTimestamp, Date timestamp,
            int numRequeues, byte[] content) {
        Date now = new Date();
        QueueMessage msg = newInstance();
        msg.queueId(queueId >= 0 ? queueId : 0).qNumRequeues(numRequeues >= 0 ? numRequeues : 0)
                .qOriginalTimestamp(orgTimestamp != null ? orgTimestamp : now)
                .qTimestamp(timestamp != null ? timestamp : now).content(content);
        return msg;
    }

    public static QueueMessage newInstance(UniversalQueueMessage _msg) {
        if (_msg == null) {
            return null;
        }
        if (_msg instanceof QueueMessage) {
            return (QueueMessage) _msg;
        }
        QueueMessage msg = newInstance();
        msg.fromMap(_msg.toMap());
        return msg;
    }

    public long queueId() {
        Object id = super.qId();
        return id instanceof Number ? ((Number) id).longValue() : 0;
    }

    public QueueMessage queueId(long queueId) {
        super.qId(queueId);
        return this;
    }

}
