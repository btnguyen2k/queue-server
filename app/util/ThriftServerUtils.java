package util;

import java.util.concurrent.TimeUnit;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.server.TThreadedSelectorServer.Args.AcceptPolicy;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;

/**
 * ThriftServer utility class.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class ThriftServerUtils {

    public final static int DEFAULT_CLIENT_TIMEOUT_MS = 10000;
    public final static int DEFAULT_MAX_FRAMESIZE = 1024 * 1024;
    public final static int DEFAULT_TOTAL_MAX_READ_BUFFERSIZE = 16 * 1024 * 1024;
    public final static int DEFAULT_NUM_SELECTOR_THREADS = 2;
    public final static int DEFAULT_NUM_WORKER_THREADS;
    static {
        DEFAULT_NUM_WORKER_THREADS = Math.max(4, Runtime.getRuntime().availableProcessors());
    }

    /**
     * Creates a {@link TThreadPoolServer} server.
     * 
     * <ul>
     * <li>1 dedicated thread for accepting connections</li>
     * <li>Once a connection is accepted, it gets scheduled to be processed by a
     * worker thread. The worker thread is tied to the specific client
     * connection until it's closed. Once the connection is closed, the worker
     * thread goes back to the thread pool.</li>
     * </ul>
     * 
     * @param processorFactory
     * @param protocolFactory
     * @param port
     *            port number on which the Thrift server will listen
     * @param clientTimeoutMillisecs
     * @param maxFrameSize
     *            max size (in bytes) of a transport frame, supply {@code <=0}
     *            value to let the method choose a default {@code maxFrameSize}
     *            value (which is 1Mb)
     * @param maxWorkerThreads
     *            max number of worker threads, supply {@code <=0} value to let
     *            the method choose a default {@code maxWorkerThreads} value
     *            (which is
     *            {@code Math.max(4, Runtime.getRuntime().availableProcessors())}
     *            )
     * @return
     * @throws TTransportException
     */
    public static TThreadPoolServer createThreadPoolServer(TProcessorFactory processorFactory,
            TProtocolFactory protocolFactory, int port, int clientTimeoutMillisecs,
            int maxFrameSize, int maxWorkerThreads) throws TTransportException {
        if (clientTimeoutMillisecs <= 0) {
            clientTimeoutMillisecs = DEFAULT_CLIENT_TIMEOUT_MS;
        }
        if (maxFrameSize <= 0) {
            maxFrameSize = DEFAULT_MAX_FRAMESIZE;
        }
        if (maxWorkerThreads <= 0) {
            maxWorkerThreads = DEFAULT_NUM_WORKER_THREADS;
        }

        TServerTransport transport = new TServerSocket(port, clientTimeoutMillisecs);
        TTransportFactory transportFactory = new TFramedTransport.Factory(maxFrameSize);
        TThreadPoolServer.Args args = new TThreadPoolServer.Args(transport)
                .processorFactory(processorFactory).protocolFactory(protocolFactory)
                .transportFactory(transportFactory).minWorkerThreads(1)
                .maxWorkerThreads(maxWorkerThreads);
        TThreadPoolServer server = new TThreadPoolServer(args);
        return server;
    }

    /**
     * Creates a {@link TNonblockingServer} server.
     * 
     * <p>
     * Non-blocking Thrift server that uses {@code java.nio.channels.Selector}
     * to handle multiple clients. However, messages are processed by the same
     * thread that calls {@code select()}.
     * </p>
     * 
     * @param processorFactory
     * @param protocolFactory
     * @param port
     *            port number on which the Thrift server will listen
     * @param clientTimeoutMillisecs
     * @param maxFrameSize
     *            max size (in bytes) of a transport frame, supply {@code <=0}
     *            value to let the method choose a default {@code maxFrameSize}
     *            value (which is 1Mb)
     * @param maxReadBufferSize
     *            max size (in bytes) of read buffer, supply {@code <=0} value
     *            to let the method choose a default {@code maxReadBufferSize}
     *            value (which is 16Mb)
     * @return
     * @throws TTransportException
     */
    public static TNonblockingServer createNonBlockingServer(TProcessorFactory processorFactory,
            TProtocolFactory protocolFactory, int port, int clientTimeoutMillisecs,
            int maxFrameSize, long maxReadBufferSize) throws TTransportException {
        if (clientTimeoutMillisecs <= 0) {
            clientTimeoutMillisecs = DEFAULT_CLIENT_TIMEOUT_MS;
        }
        if (maxFrameSize <= 0) {
            maxFrameSize = DEFAULT_MAX_FRAMESIZE;
        }
        if (maxReadBufferSize <= 0) {
            maxReadBufferSize = DEFAULT_TOTAL_MAX_READ_BUFFERSIZE;
        }

        TNonblockingServerTransport transport = new TNonblockingServerSocket(port,
                clientTimeoutMillisecs);
        TTransportFactory transportFactory = new TFramedTransport.Factory(maxFrameSize);
        TNonblockingServer.Args args = new TNonblockingServer.Args(transport)
                .processorFactory(processorFactory).protocolFactory(protocolFactory)
                .transportFactory(transportFactory);
        args.maxReadBufferBytes = maxReadBufferSize;
        TNonblockingServer server = new TNonblockingServer(args);
        return server;
    }

    /**
     * Creates a {@link THsHaServer} server.
     * 
     * <p>
     * Similar to {@link TNonblockingServer} but a separate pool of worker
     * threads is used to handle message processing.
     * </p>
     * 
     * @param processorFactory
     * @param protocolFactory
     * @param port
     *            port number on which the Thrift server will listen
     * @param clientTimeoutMillisecs
     * @param maxFrameSize
     *            max size (in bytes) of a transport frame, supply {@code <=0}
     *            value to let the method choose a default {@code maxFrameSize}
     *            value (which is 1Mb)
     * @param maxReadBufferSize
     *            max size (in bytes) of read buffer, supply {@code <=0} value
     *            to let the method choose a default {@code maxReadBufferSize}
     *            value (which is 16Mb)
     * @param numWorkerThreads
     *            number of worker threads, supply {@code <=0} value to let the
     *            method choose a default {@code numWorkerThreads} value (which
     *            is
     *            {@code Math.max(4, Runtime.getRuntime().availableProcessors())}
     *            )
     * @return
     * @throws TTransportException
     */
    public static THsHaServer createHaHsServer(TProcessorFactory processorFactory,
            TProtocolFactory protocolFactory, int port, int clientTimeoutMillisecs,
            int maxFrameSize, long maxReadBufferSize, int numWorkerThreads)
            throws TTransportException {
        if (clientTimeoutMillisecs <= 0) {
            clientTimeoutMillisecs = DEFAULT_CLIENT_TIMEOUT_MS;
        }
        if (maxFrameSize <= 0) {
            maxFrameSize = DEFAULT_MAX_FRAMESIZE;
        }
        if (maxReadBufferSize <= 0) {
            maxReadBufferSize = DEFAULT_TOTAL_MAX_READ_BUFFERSIZE;
        }
        if (numWorkerThreads <= 0) {
            numWorkerThreads = DEFAULT_NUM_WORKER_THREADS;
        }

        TNonblockingServerTransport transport = new TNonblockingServerSocket(port,
                clientTimeoutMillisecs);
        TTransportFactory transportFactory = new TFramedTransport.Factory(maxFrameSize);
        THsHaServer.Args args = new THsHaServer.Args(transport).processorFactory(processorFactory)
                .protocolFactory(protocolFactory).transportFactory(transportFactory)
                .workerThreads(numWorkerThreads).stopTimeoutVal(60)
                .stopTimeoutUnit(TimeUnit.SECONDS);
        args.maxReadBufferBytes = maxReadBufferSize;
        THsHaServer server = new THsHaServer(args);
        return server;
    }

    /**
     * Creates a {@link TThreadedSelectorServer} server.
     * 
     * <p>
     * Similar to {@link THsHaServer} but its use 2 thread pools: one for
     * handling network I/O (e.g. accepting client connections), one for
     * handling message such like {@link THsHaServer}.
     * </p>
     * 
     * @param processorFactory
     * @param protocolFactory
     * @param port
     *            port number on which the Thrift server will listen
     * @param clientTimeoutMillisecs
     * @param maxFrameSize
     *            max size (in bytes) of a transport frame, supply {@code <=0}
     *            value to let the method choose a default {@code maxFrameSize}
     *            value (which is 1Mb)
     * @param maxReadBufferSize
     *            max size (in bytes) of read buffer, supply {@code <=0} value
     *            to let the method choose a default {@code maxReadBufferSize}
     *            value (which is 16Mb)
     * @param numSelectorThreads
     *            number of selector threads, supply {@code <=0} value to let
     *            the method choose a default {@code numSelectorThreads} value
     *            (which is {@code 2} )
     * @param numWorkerThreads
     *            number of worker threads, supply {@code <=0} value to let the
     *            method choose a default {@code numWorkerThreads} value (which
     *            is
     *            {@code Math.max(4, Runtime.getRuntime().availableProcessors())}
     *            )
     * @return
     * @throws TTransportException
     */
    public static TThreadedSelectorServer createThreadedSelectorServer(
            TProcessorFactory processorFactory, TProtocolFactory protocolFactory, int port,
            int clientTimeoutMillisecs, int maxFrameSize, long maxReadBufferSize,
            int numSelectorThreads, int numWorkerThreads) throws TTransportException {
        if (clientTimeoutMillisecs <= 0) {
            clientTimeoutMillisecs = DEFAULT_CLIENT_TIMEOUT_MS;
        }
        if (maxFrameSize <= 0) {
            maxFrameSize = DEFAULT_MAX_FRAMESIZE;
        }
        if (maxReadBufferSize <= 0) {
            maxReadBufferSize = DEFAULT_TOTAL_MAX_READ_BUFFERSIZE;
        }
        if (numSelectorThreads <= 0) {
            numSelectorThreads = DEFAULT_NUM_SELECTOR_THREADS;
        }
        if (numWorkerThreads <= 0) {
            numWorkerThreads = DEFAULT_NUM_WORKER_THREADS;
        }

        TNonblockingServerTransport transport = new TNonblockingServerSocket(port,
                clientTimeoutMillisecs);
        TTransportFactory transportFactory = new TFramedTransport.Factory(maxFrameSize);
        TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(transport)
                .processorFactory(processorFactory).protocolFactory(protocolFactory)
                .transportFactory(transportFactory).workerThreads(numWorkerThreads)
                .acceptPolicy(AcceptPolicy.FAIR_ACCEPT).acceptQueueSizePerThread(100000)
                .selectorThreads(numSelectorThreads);
        args.maxReadBufferBytes = maxReadBufferSize;
        TThreadedSelectorServer server = new TThreadedSelectorServer(args);
        return server;
    }

}
