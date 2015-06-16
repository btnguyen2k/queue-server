package globals;

import java.io.File;

import org.apache.thrift.server.TServer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import play.Logger;
import play.Play;
import queue.QueueApi;

import com.github.ddth.tsc.DataPoint;
import com.github.ddth.tsc.ICounter;
import com.github.ddth.tsc.ICounterFactory;

public class Registry {

    public static void init() {
        initApplicationContext();

        localCounterFactory = getBean("TSC_LOCAL", ICounterFactory.class);
        globalCounterFactory = getBean("TSC_GLOBAL", ICounterFactory.class);
        queueApi = getBean(QueueApi.class);
    }

    public static void destroy() {
        stopThriftServer();

        destroyApplicationContext();
    }

    /*----------------------------------------------------------------------*/
    private static ICounterFactory localCounterFactory, globalCounterFactory;
    public final static String TSC_TOTAL = "BLOOMSERVER_TSC_TOTAL";
    public final static String TSC_200 = "BLOOMSERVER_TSC_200";
    public final static String TSC_400 = "BLOOMSERVER_TSC_400";
    public final static String TSC_403 = "BLOOMSERVER_TSC_403";
    public final static String TSC_404 = "BLOOMSERVER_TSC_404";
    public final static String TSC_500 = "BLOOMSERVER_TSC_500";

    public final static String COUNTER_TOTAL = "BLOOMSERVER_COUNTER_TOTAL";
    public final static String COUNTER_200 = "BLOOMSERVER_COUNTER_200";
    public final static String COUNTER_400 = "BLOOMSERVER_COUNTER_400";
    public final static String COUNTER_403 = "BLOOMSERVER_COUNTER_403";
    public final static String COUNTER_404 = "BLOOMSERVER_COUNTER_404";
    public final static String COUNTER_500 = "BLOOMSERVER_COUNTER_500";

    public final static String COUNTER_CONCURENCY = "BLOOMSERVER_CONCURENCY";

    public static void incConcurrency() {
        ICounter counter;

        counter = localCounterFactory != null ? localCounterFactory.getCounter(COUNTER_CONCURENCY)
                : null;
        if (counter != null) {
            counter.add(1000, 1);
        }

        counter = globalCounterFactory != null ? globalCounterFactory
                .getCounter(COUNTER_CONCURENCY) : null;
        if (counter != null) {
            counter.add(1000, 1);
        }
    }

    public static void decConcurrency() {
        ICounter counter;

        counter = localCounterFactory != null ? localCounterFactory.getCounter(COUNTER_CONCURENCY)
                : null;
        if (counter != null) {
            counter.add(1000, -1);
        }

        counter = globalCounterFactory != null ? globalCounterFactory
                .getCounter(COUNTER_CONCURENCY) : null;
        if (counter != null) {
            counter.add(1000, -1);
        }
    }

    public static long[] getConcurrency() {
        ICounter counter;
        DataPoint dp;
        long[] result = new long[2];

        counter = localCounterFactory != null ? localCounterFactory.getCounter(COUNTER_CONCURENCY)
                : null;
        dp = counter != null ? counter.get(1000) : null;
        result[0] = dp != null ? dp.value() : 0;

        counter = globalCounterFactory != null ? globalCounterFactory
                .getCounter(COUNTER_CONCURENCY) : null;
        dp = counter != null ? counter.get(1000) : null;
        result[1] = dp != null ? dp.value() : 0;

        return result;
    }

    private static void _updateTscCounters(final String name) {
        ICounter counterLocal = localCounterFactory != null ? localCounterFactory.getCounter(name)
                : null;
        if (counterLocal != null) {
            counterLocal.add(1);
        }

        ICounter counterGlobal = globalCounterFactory != null ? globalCounterFactory
                .getCounter(name) : null;
        if (counterGlobal != null) {
            counterGlobal.add(1);
        }
    }

    private static void _updateCounters(final String name) {
        ICounter counterLocal = localCounterFactory != null ? localCounterFactory.getCounter(name)
                : null;
        if (counterLocal != null) {
            counterLocal.add(0, 1);
        }

        ICounter counterGlobal = globalCounterFactory != null ? globalCounterFactory
                .getCounter(name) : null;
        if (counterGlobal != null) {
            counterGlobal.add(0, 1);
        }
    }

    public static void updateCounters(final int status) {
        _updateTscCounters(TSC_TOTAL);
        _updateCounters(COUNTER_TOTAL);
        switch (status) {
        case 200:
            _updateTscCounters(TSC_200);
            _updateCounters(COUNTER_200);
            break;
        case 400:
            _updateTscCounters(TSC_400);
            _updateCounters(COUNTER_400);
            break;
        case 403:
            _updateTscCounters(TSC_403);
            _updateCounters(COUNTER_403);
            break;
        case 404:
            _updateTscCounters(TSC_404);
            _updateCounters(COUNTER_404);
            break;
        case 500:
            _updateTscCounters(TSC_500);
            _updateCounters(COUNTER_500);
            break;
        }
    }

    public static ICounterFactory getLocalCounterFactory() {
        return localCounterFactory;
    }

    public static ICounterFactory getGlobalCounterFactory() {
        return globalCounterFactory;
    }

    /*----------------------------------------------------------------------*/
    private static QueueApi queueApi;

    public static QueueApi getQueueApi() {
        return queueApi;
    }

    /*----------------------------------------------------------------------*/
    private static TServer thriftServer = null;

    public static void startThriftServer(final TServer thriftServer) {
        Registry.thriftServer = thriftServer;
        Thread t = new Thread("Thrift Server") {
            public void run() {
                thriftServer.serve();
            }
        };
        t.start();
    }

    public static void stopThriftServer() {
        if (thriftServer != null) {
            try {
                thriftServer.stop();
            } catch (Exception e) {
                Logger.warn(e.getMessage(), e);
            } finally {
                thriftServer = null;
            }
        }
    }

    /*----------------------------------------------------------------------*/
    private static ApplicationContext applicationContext;

    public static <T> T getBean(Class<T> clazz) {
        try {
            return applicationContext.getBean(clazz);
        } catch (BeansException e) {
            return null;
        }
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        try {
            return applicationContext.getBean(name, clazz);
        } catch (BeansException e) {
            return null;
        }
    }

    synchronized private static void initApplicationContext() {
        if (Registry.applicationContext == null) {
            String configFile = "conf/spring/beans.xml";
            File springConfigFile = new File(Play.application().path(), configFile);
            AbstractApplicationContext applicationContext = new FileSystemXmlApplicationContext(
                    "file:" + springConfigFile.getAbsolutePath());
            applicationContext.start();
            Registry.applicationContext = applicationContext;
        }
    }

    synchronized private static void destroyApplicationContext() {
        if (applicationContext != null) {
            try {
                ((AbstractApplicationContext) applicationContext).destroy();
            } catch (Exception e) {
                Logger.warn(e.getMessage(), e);
            } finally {
                applicationContext = null;
            }
        }
    }
}
