package com.ljm.server.io.connection.channel;

import com.ljm.server.io.connection.ChannelConnection;
import com.ljm.server.io.connector.ConnectorException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author 李佳明 https://github.com/pkpk1234
 * @date 2018-01-2018/1/10
 */
public class SelectableChannelConnection implements ChannelConnection {

    private final SelectionKey selectionKey;

    public SelectableChannelConnection(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }


    @Override
    public void write(byte[] bytes) throws IOException {
        this.write(bytes, 0, bytes.length);
    }

    @Override
    public void write(byte[] bytes, int offset, int length) throws IOException {
        if (selectionKey.isWritable()) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer output = ByteBuffer.wrap(bytes, 0, bytes.length);
            output.flip();
            while (output.hasRemaining()) {
                socketChannel.write(output);
            }
        }
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        throw new ConnectorException("not support BIO");
    }

    @Override
    public int read(byte[] bytes, int start, int end) {
        throw new ConnectorException("not support BIO");
    }

    @Override
    public byte[] read(ByteBuffer byteBuffer) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(64);
        if (selectionKey.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer buffer = ByteBuffer.allocate(64);
            int readLenght = -1;
            byteArrayOutputStream = new ByteArrayOutputStream(64);
            while ((readLenght = socketChannel.read(buffer)) != -1) {
                buffer.flip();
                byte[] bytes = new byte[buffer.remaining()];
                byteBuffer.clear();
                byteArrayOutputStream.write(byteBuffer.get(bytes).array());
            }
        }
        return byteArrayOutputStream.toByteArray();
    }
}
