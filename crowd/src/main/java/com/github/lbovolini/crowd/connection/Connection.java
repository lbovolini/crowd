package com.github.lbovolini.crowd.connection;

import com.github.lbovolini.crowd.message.Message;
import com.github.lbovolini.crowd.message.PartialMessage;
import com.github.lbovolini.crowd.handler.ReadHandler;
import com.github.lbovolini.crowd.handler.WriteHandler;
import com.github.lbovolini.crowd.scheduler.MessageFrom;
import com.github.lbovolini.crowd.scheduler.Scheduler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.lbovolini.crowd.configuration.Config.BUFFER_ARRAY_SIZE;
import static com.github.lbovolini.crowd.configuration.Config.BUFFER_SIZE;
import static com.github.lbovolini.crowd.configuration.Config.HEADER_SIZE;

public class Connection {

    private long hostId;

    private final Scheduler scheduler;

    private Message message;
    private PartialMessage partialMessage;
    private boolean closed;

    private final AsynchronousSocketChannel channel;
    private final AsynchronousServerSocketChannel serverChannel;

    private static final ByteBufferPool readerBufferPool = new ByteBufferPool();
    private static final ByteBufferPool writerBufferPool = new ByteBufferPool();

    private static final WriteHandler writeHandler = new WriteHandler();
    private static final ReadHandler readHandler = new ReadHandler();

    private final ReentrantLock writeLock;
    private final ReentrantLock readLock;

    private final Queue<ByteBuffer> writerBufferQueue;
    private final Queue<ByteBuffer> readerBufferQueue;

    private final ByteBuffer[] writerBufferArray;
    private final ByteBuffer[] readerBufferArray;

    public Connection(AsynchronousServerSocketChannel serverChannel, AsynchronousSocketChannel channel, Scheduler scheduler) {
        this.channel = channel;
        this.serverChannel = serverChannel;
        this.scheduler = scheduler;
        setHostId();
        this.partialMessage = new PartialMessage();
        this.writeLock = new ReentrantLock();
        this.readLock = new ReentrantLock();
        this.writerBufferQueue = new LinkedList<>();
        this.readerBufferQueue = new LinkedList<>();
        this.writerBufferArray = new ByteBuffer[BUFFER_ARRAY_SIZE];
        this.readerBufferArray = new ByteBuffer[BUFFER_ARRAY_SIZE];
    }

    public Connection(AsynchronousSocketChannel channel, Scheduler scheduler) {
        this(null, channel, scheduler);
    }

    private void setHostId() {
        try {
            InetSocketAddress address = (InetSocketAddress)this.channel.getRemoteAddress();
            String host = address.getAddress().getHostAddress().replace(".", "");
            String port = Integer.toString(address.getPort());
            this.hostId = Long.valueOf(host + port);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public long getHostId() {
        return hostId;
    }

    public String getRemoteAddress() {
        try {
            return ((InetSocketAddress)channel.getRemoteAddress()).getAddress().getHostAddress();
        } catch (IOException e) { e.printStackTrace(); }
        return "";
    }

    public int getRemotePort() {
        try {
            return ((InetSocketAddress)channel.getRemoteAddress()).getPort();
        } catch (IOException e) { e.printStackTrace(); }
        return 0;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }

    public ByteBufferPool getReaderBufferPool() {
        return readerBufferPool;
    }

    public ByteBufferPool getWriterBufferPool() {
        return writerBufferPool;
    }

    public ReentrantLock getWriteLock() {
        return writeLock;
    }

    public ReentrantLock getReadLock() {
        return readLock;
    }

    public Queue<ByteBuffer> getWriterBufferQueue() {
        return writerBufferQueue;
    }

    public Queue<ByteBuffer> getReaderBufferQueue() {
        return readerBufferQueue;
    }

    public ByteBuffer[] getWriterBufferArray() {
        return writerBufferArray;
    }

    public ByteBuffer[] getReaderBufferArray() {
        return readerBufferArray;
    }

    public PartialMessage getPartialMessage() {
        return partialMessage;
    }

    private boolean read() {

        ByteBuffer byteBuffer = readerBufferPool.pool();

        readLock.lock();

        try {
            if (isClosed()) { return false; }

            final boolean wasEmpty = readerBufferQueue.isEmpty();
            readerBufferQueue.add(byteBuffer);

            if (!wasEmpty) { return true; }
        }
        finally {
            readLock.unlock();
        }

        readerBufferArray[0] = byteBuffer;
        channel.read(readerBufferArray, 0, 1, 0, TimeUnit.SECONDS, this, readHandler);

        return true;
    }

    private boolean write(ByteBuffer byteBuffer) {

        writeLock.lock();

        try {
            if (isClosed()) { return false; }

            final boolean wasEmpty = writerBufferQueue.isEmpty();
            writerBufferQueue.add(byteBuffer);

            if (!wasEmpty) { return true; }
        }
        finally {
            writeLock.unlock();
        }

        writerBufferArray[0] = byteBuffer;
        channel.write(writerBufferArray, 0, 1, 0, TimeUnit.SECONDS, this, writeHandler );

        return true;
    }

    public void send(Message message) {
        // !todo ?
        //if (message.getDataLength() > (BUFFER_SIZE - HEADER_SIZE)) {
        //    System.out.println("Message too big");
        //}

        ByteBuffer buffer = writerBufferPool.pool();
        buffer.put(message.getType()).putShort(message.getDataLength()).put(message.getData());
        buffer.flip();
        write(buffer);
    }


    public void receive() {
        read();
    }

    public void handle(Message message) {
        scheduler.enqueue(new MessageFrom(this, message));
    };

    public void close() throws IOException {
        setClosed(true);
        channel.close();
    }

}
