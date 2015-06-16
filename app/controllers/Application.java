package controllers;

import globals.Registry;

import java.util.HashMap;
import java.util.Map;

import play.Logger;
import play.api.templates.Html;
import play.mvc.Http.RawBuffer;
import play.mvc.Http.RequestBody;
import play.mvc.Result;
import queue.QueueApi;
import queue.internal.QueueMessage;
import util.Constants;
import util.QueueServerUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.commons.utils.SerializationUtils;
import com.github.ddth.tsc.DataPoint;
import com.github.ddth.tsc.ICounter;
import com.github.ddth.tsc.ICounterFactory;

public class Application extends BaseController {

    private static Result doResponse(int status, String message, boolean result, Object value) {
        Map<String, Object> responseData = new HashMap<String, Object>();
        responseData.put(Constants.RESPONSE_FIELD_STATUS, status);
        responseData.put(Constants.RESPONSE_FIELD_MESSAGE, message);
        responseData.put(Constants.RESPONSE_FIELD_RESULT, result);
        if (value != null) {
            responseData.put(Constants.RESPONSE_FIELD_VALUE, value);
        }
        response().setHeader(CONTENT_TYPE, "application/json");
        response().setHeader(CONTENT_ENCODING, "utf-8");
        Registry.updateCounters(status);
        return ok(SerializationUtils.toJsonString(responseData));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> parseRequest() {
        RequestBody requestBody = request().body();
        String requestContent = null;
        JsonNode jsonNode = requestBody.asJson();
        if (jsonNode != null) {
            requestContent = jsonNode.toString();
        } else {
            RawBuffer rawBuffer = requestBody.asRaw();
            if (rawBuffer != null) {
                requestContent = new String(rawBuffer.asBytes(), Constants.UTF8);
            } else {
                requestContent = requestBody.asText();
            }
        }
        return SerializationUtils.fromJsonString(requestContent, Map.class);
    }

    /*
     * Handles POST:/queueExists
     */
    public static Result queueExists() {
        Map<String, Object> params = parseRequest();
        String queueName = DPathUtils.getValue(params, "queue_name", String.class);
        String secret = DPathUtils.getValue(params, "secret", String.class);

        QueueApi queueApi = Registry.getQueueApi();
        try {
            if (!queueApi.authorize(secret, queueName)) {
                return doResponse(403, "Unauthorized!", false, null);
            }
            boolean result = queueApi.queueExists(queueName);
            return doResponse(result ? 200 : 404, String.valueOf(result), result, null);
        } catch (Exception e) {
            final String logMsg = "Exception [" + e.getClass() + "]: " + e.getMessage();
            Logger.error(logMsg, e);
            return doResponse(500, logMsg, false, null);
        }
    }

    /*
     * Handles POST:/initQueue
     */
    public static Result initQueue() {
        Map<String, Object> params = parseRequest();
        String queueName = DPathUtils.getValue(params, "queue_name", String.class);
        String secret = DPathUtils.getValue(params, "secret", String.class);

        QueueApi queueApi = Registry.getQueueApi();
        try {
            if (!queueApi.isValidQueueName(queueName)) {
                return doResponse(400, "Invalid queue name [" + queueName + "]!", false, null);
            }
            if (!queueApi.authorize(secret, queueName)) {
                return doResponse(403, "Unauthorized!", false, null);
            }
            boolean result = queueApi.initQueue(queueName);
            return doResponse(200, String.valueOf(result), result, null);
        } catch (Exception e) {
            final String logMsg = "Exception [" + e.getClass() + "]: " + e.getMessage();
            Logger.error(logMsg, e);
            return doResponse(500, logMsg, false, null);
        }
    }

    /*
     * Handles POST:/queue
     */
    public static Result queue() {
        Map<String, Object> params = parseRequest();
        String queueName = DPathUtils.getValue(params, "queue_name", String.class);
        String secret = DPathUtils.getValue(params, "secret", String.class);
        String contentBase64 = DPathUtils.getValue(params, "content", String.class);

        QueueApi queueApi = Registry.getQueueApi();
        try {
            if (!queueApi.isValidQueueName(queueName)) {
                return doResponse(400, "Invalid queue name [" + queueName + "]!", false, null);
            }
            if (!queueApi.authorize(secret, queueName)) {
                return doResponse(403, "Unauthorized!", false, null);
            }
            QueueMessage queueMessage = QueueMessage.newInstance();
            queueMessage.content(QueueServerUtils.base64Decode(contentBase64));
            boolean result = queueApi.queue(queueName, queueMessage);
            return doResponse(200, String.valueOf(result), result, null);
        } catch (Exception e) {
            final String logMsg = "Exception [" + e.getClass() + "]: " + e.getMessage();
            Logger.error(logMsg, e);
            return doResponse(500, logMsg, false, null);
        }
    }

    /*
     * Handles POST:/requeue
     */
    public static Result requeue() {
        Map<String, Object> params = parseRequest();
        String queueName = DPathUtils.getValue(params, "queue_name", String.class);
        String secret = DPathUtils.getValue(params, "secret", String.class);

        QueueApi queueApi = Registry.getQueueApi();
        try {
            if (!queueApi.isValidQueueName(queueName)) {
                return doResponse(400, "Invalid queue name [" + queueName + "]!", false, null);
            }
            if (!queueApi.authorize(secret, queueName)) {
                return doResponse(403, "Unauthorized!", false, null);
            }
            QueueMessage queueMessage = QueueServerUtils.queueMessageFromRequestParams(params);
            boolean result = queueApi.requeue(queueName, queueMessage);
            return doResponse(200, String.valueOf(result), result, null);
        } catch (Exception e) {
            final String logMsg = "Exception [" + e.getClass() + "]: " + e.getMessage();
            Logger.error(logMsg, e);
            return doResponse(500, logMsg, false, null);
        }
    }

    /*
     * Handles POST:/requeueSilent
     */
    public static Result requeueSilent() {
        Map<String, Object> params = parseRequest();
        String queueName = DPathUtils.getValue(params, "queue_name", String.class);
        String secret = DPathUtils.getValue(params, "secret", String.class);

        QueueApi queueApi = Registry.getQueueApi();
        try {
            if (!queueApi.isValidQueueName(queueName)) {
                return doResponse(400, "Invalid queue name [" + queueName + "]!", false, null);
            }
            if (!queueApi.authorize(secret, queueName)) {
                return doResponse(403, "Unauthorized!", false, null);
            }
            QueueMessage queueMessage = QueueServerUtils.queueMessageFromRequestParams(params);
            boolean result = queueApi.requeueSilent(queueName, queueMessage);
            return doResponse(200, String.valueOf(result), result, false);
        } catch (Exception e) {
            final String logMsg = "Exception [" + e.getClass() + "]: " + e.getMessage();
            Logger.error(logMsg, e);
            return doResponse(500, logMsg, false, null);
        }
    }

    /*
     * Handles POST:/finish
     */
    public static Result finish() {
        Map<String, Object> params = parseRequest();
        String queueName = DPathUtils.getValue(params, "queue_name", String.class);
        String secret = DPathUtils.getValue(params, "secret", String.class);

        QueueApi queueApi = Registry.getQueueApi();
        try {
            if (!queueApi.isValidQueueName(queueName)) {
                return doResponse(400, "Invalid queue name [" + queueName + "]!", false, null);
            }
            if (!queueApi.authorize(secret, queueName)) {
                return doResponse(403, "Unauthorized!", false, null);
            }
            QueueMessage queueMessage = QueueServerUtils.queueMessageFromRequestParams(params);
            boolean result = queueApi.finish(queueName, queueMessage);
            return doResponse(200, String.valueOf(result), result, null);
        } catch (Exception e) {
            final String logMsg = "Exception [" + e.getClass() + "]: " + e.getMessage();
            Logger.error(logMsg, e);
            return doResponse(500, logMsg, false, null);
        }
    }

    /*
     * Handles POST:/take
     */
    public static Result take() {
        Map<String, Object> params = parseRequest();
        String queueName = DPathUtils.getValue(params, "queue_name", String.class);
        String secret = DPathUtils.getValue(params, "secret", String.class);

        QueueApi queueApi = Registry.getQueueApi();
        try {
            if (!queueApi.isValidQueueName(queueName)) {
                return doResponse(400, "Invalid queue name [" + queueName + "]!", false, null);
            }
            if (!queueApi.authorize(secret, queueName)) {
                return doResponse(403, "Unauthorized!", false, null);
            }
            QueueMessage msg = queueApi.take(queueName);
            Map<String, Object> result = QueueServerUtils.queueMessageToResponseParams(msg);
            return doResponse(200, "", result != null, result);
        } catch (Exception e) {
            final String logMsg = "Exception [" + e.getClass() + "]: " + e.getMessage();
            Logger.error(logMsg, e);
            return doResponse(500, logMsg, false, null);
        }
    }

    /*
     * Handles POST:/queueSize
     */
    public static Result queueSize() {
        Map<String, Object> params = parseRequest();
        String queueName = DPathUtils.getValue(params, "queue_name", String.class);
        String secret = DPathUtils.getValue(params, "secret", String.class);

        QueueApi queueApi = Registry.getQueueApi();
        try {
            if (!queueApi.isValidQueueName(queueName)) {
                return doResponse(400, "Invalid queue name [" + queueName + "]!", false, null);
            }
            if (!queueApi.authorize(secret, queueName)) {
                return doResponse(403, "Unauthorized!", false, null);
            }
            int result = queueApi.queueSize(queueName);
            return doResponse(200, "", result >= 0, result);
        } catch (Exception e) {
            final String logMsg = "Exception [" + e.getClass() + "]: " + e.getMessage();
            Logger.error(logMsg, e);
            return doResponse(500, logMsg, false, null);
        }
    }

    /*
     * Handles POST:/ephemeralSize
     */
    public static Result ephemeralSize() {
        Map<String, Object> params = parseRequest();
        String queueName = DPathUtils.getValue(params, "queue_name", String.class);
        String secret = DPathUtils.getValue(params, "secret", String.class);

        QueueApi queueApi = Registry.getQueueApi();
        try {
            if (!queueApi.isValidQueueName(queueName)) {
                return doResponse(400, "Invalid queue name [" + queueName + "]!", false, null);
            }
            if (!queueApi.authorize(secret, queueName)) {
                return doResponse(403, "Unauthorized!", false, null);
            }
            int result = queueApi.ephemeralSize(queueName);
            return doResponse(200, "", result >= 0, result);
        } catch (Exception e) {
            final String logMsg = "Exception [" + e.getClass() + "]: " + e.getMessage();
            Logger.error(logMsg, e);
            return doResponse(500, logMsg, false, null);
        }
    }

    private static DataPoint[] buildCounterData(ICounter counter, long timestamp) {
        long last1Min = timestamp - 60 * 1000L;
        long last5Mins = timestamp - 5 * 60 * 1000L;
        long last15Mins = timestamp - 15 * 60 * 1000L;
        DataPoint[] result = new DataPoint[] {
                new DataPoint(DataPoint.Type.SUM, last1Min, 0, ICounter.STEPS_1_MIN * 1000),
                new DataPoint(DataPoint.Type.SUM, last5Mins, 0, ICounter.STEPS_5_MINS * 1000),
                new DataPoint(DataPoint.Type.SUM, last15Mins, 0, ICounter.STEPS_15_MINS * 1000) };
        if (counter == null) {
            return result;
        }

        DataPoint[] tempArr = counter.getSeries(last1Min, timestamp);
        for (DataPoint dp : tempArr) {
            result[0].add(dp);
        }
        tempArr = counter.getSeries(last5Mins, timestamp);
        for (DataPoint dp : tempArr) {
            result[1].add(dp);
        }
        tempArr = counter.getSeries(last15Mins, timestamp);
        for (DataPoint dp : tempArr) {
            result[2].add(dp);
        }
        return result;
    }

    /*
     * Handle: GET:/index
     */
    public static Result index() throws Exception {
        Map<String, DataPoint[]> statsLocal = new HashMap<String, DataPoint[]>();
        Map<String, DataPoint[]> statsGlobal = new HashMap<String, DataPoint[]>();
        Map<String, Long> countersLocal = new HashMap<String, Long>();
        Map<String, Long> countersGlobal = new HashMap<String, Long>();
        ICounterFactory localCounterFactory = Registry.getLocalCounterFactory();
        ICounterFactory globalCounterFactory = Registry.getGlobalCounterFactory();

        long timestamp = System.currentTimeMillis();
        String[] tscNames = new String[] { Registry.TSC_TOTAL, Registry.TSC_200, Registry.TSC_400,
                Registry.TSC_403, Registry.TSC_404, Registry.TSC_500 };
        for (String name : tscNames) {
            statsLocal.put(
                    name,
                    buildCounterData(
                            localCounterFactory != null ? localCounterFactory.getCounter(name)
                                    : null, timestamp));
            statsGlobal.put(
                    name,
                    buildCounterData(
                            globalCounterFactory != null ? globalCounterFactory.getCounter(name)
                                    : null, timestamp));
        }

        String[] counterNames = new String[] { Registry.COUNTER_TOTAL, Registry.COUNTER_200,
                Registry.COUNTER_400, Registry.COUNTER_403, Registry.COUNTER_404,
                Registry.COUNTER_500 };
        for (String name : counterNames) {
            ICounter counter = localCounterFactory != null ? localCounterFactory.getCounter(name)
                    : null;
            DataPoint dp = counter != null ? counter.get(0) : null;
            long value = dp != null ? dp.value() : 0;
            countersLocal.put(name, value);

            counter = globalCounterFactory != null ? globalCounterFactory.getCounter(name) : null;
            dp = counter != null ? counter.get(0) : null;
            value = dp != null ? dp.value() : 0;
            countersGlobal.put(name, value);
        }

        long[] concurrency = Registry.getConcurrency();

        Html html = render("index", concurrency, countersLocal, statsLocal, countersGlobal,
                statsGlobal);
        return ok(html);
    }

}
