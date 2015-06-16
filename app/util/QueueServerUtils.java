package util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import queue.internal.QueueMessage;

import com.github.ddth.commons.utils.DPathUtils;

/**
 * Utility class.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class QueueServerUtils {

    public static byte[] base64Decode(String encodedStr) {
        return encodedStr != null ? Base64.decodeBase64(encodedStr) : null;
    }

    public static String base64Encode(byte[] data) {
        return data != null ? Base64.encodeBase64String(data) : null;
    }

    /**
     * Constructs a new {@link QueueMessage} from request's parameters.
     * 
     * @param params
     * @return
     */
    public static QueueMessage queueMessageFromRequestParams(Map<String, Object> params) {
        if (params == null) {
            return null;
        }
        Long qId = DPathUtils.getValue(params, "queue_id", Long.class);
        Date qOrgTimestamp = DPathUtils.getValue(params, "org_timestamp", Date.class);
        Date qTimestamp = DPathUtils.getValue(params, "timestamp", Date.class);
        Integer qNumRequeues = DPathUtils.getValue(params, "num_requeues", Integer.class);
        String contentBase64 = DPathUtils.getValue(params, "content", String.class);

        QueueMessage queueMessage = QueueMessage.newInstance(qId != null ? qId.longValue() : 0,
                qOrgTimestamp, qTimestamp, qNumRequeues != null ? qNumRequeues.intValue() : 0,
                QueueServerUtils.base64Decode(contentBase64));
        return queueMessage;
    }

    /**
     * Constructs response data from a {@link QueueMessage} object.
     * 
     * @param queueMessage
     * @return
     */
    public static Map<String, Object> queueMessageToResponseParams(QueueMessage queueMessage) {
        if (queueMessage == null) {
            return null;
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("queue_id", queueMessage.queueId());
        result.put("org_timestamp", queueMessage.qOriginalTimestamp());
        result.put("timestamp", queueMessage.qTimestamp());
        result.put("num_requeues", queueMessage.qNumRequeues());
        result.put("content", base64Encode(queueMessage.content()));
        return result;
    }

}
