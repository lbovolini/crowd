package com.github.lbovolini.crowd.core.connection;

import com.github.lbovolini.crowd.core.message.Flags;
import com.github.lbovolini.crowd.core.message.PartialMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class MessageBufferUtils {

    private static final Logger log = LoggerFactory.getLogger(MessageBufferUtils.class);

    public static void readTypeFromBuffer(ByteBuffer buffer, PartialMessage partialMessage) {
        partialMessage.setType(buffer.get());
        partialMessage.getFlags().setHasType(true);
    }

    public static boolean readSizeFromBuffer(ByteBuffer byteBuffer, PartialMessage partialMessage) {

        Flags flags = partialMessage.getFlags();

        if (!flags.hasSizeChunk()) {
            flags.setHasSizeChunk(true);
            if (byteBuffer.remaining() < Short.BYTES) {
                partialMessage.setSizeFirstByte(byteBuffer.get());
            } else {
                short size = byteBuffer.getShort();
                partialMessage.setSize(size);
                flags.setHasSize(true);
                return true;
            }
        } else {
            partialMessage.setSizeLastByte(byteBuffer.get());
            flags.setHasSize(true);
            return true;
        }
        return false;
    }

    public static boolean readDataFromBuffer(ByteBuffer buffer, PartialMessage partialMessage) {

        Flags flags = partialMessage.getFlags();

        if (!flags.hasMessageChunk()) {
            // !todo ?
            //if (partialMessage.getSize() > (MAX_MESSAGE_SIZE - HEADER_SIZE)) {
            //    System.out.println("Message size limit exceeded");
            //}
            partialMessage.allocate();

            int read = Math.min(buffer.remaining(), partialMessage.getSize());
            partialMessage.setReadSize(read);
            partialMessage.read(buffer, 0, read);
            flags.setHasMessageChunk(true);

            if (read == partialMessage.getSize()) {
                flags.setHasMessage(true);
                //flags.resetAll();
                return true;
            }
        } else {

            int remaining = partialMessage.getSize() - partialMessage.getReadSize();

            if (remaining > buffer.remaining()) {
                int read = partialMessage.getReadSize();

                int inBufferSize = buffer.remaining();
                partialMessage.read(buffer, partialMessage.getReadSize(), inBufferSize);
                partialMessage.setReadSize(read + inBufferSize);

                return false;
            }

            partialMessage.read(buffer, partialMessage.getReadSize(), remaining);
            flags.setHasMessage(true);
            //flags.resetAll();
            return true;
        }

        return false;
    }
}
