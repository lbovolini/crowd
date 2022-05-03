package com.github.lbovolini.crowd.classloader.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class FileDownloader {

    private static final Logger log = LoggerFactory.getLogger(FileDownloader.class);

    public static final boolean CACHE = Boolean.parseBoolean(System.getProperty("cache", "false"));
    public static final int READ_TIMEOUT = 1000;

    private FileDownloader() {}

    public static boolean download(URL from, String destination, String name) throws IOException {
        URL url = new URL(from + name);
        return download(url, destination);
    }

    public static boolean download(URL from, String destination) throws IOException {

        URLConnection connection = from.openConnection();
        connection.setUseCaches(CACHE);
        connection.setReadTimeout(READ_TIMEOUT);

        long fileSize = connection.getContentLengthLong();

        if (fileSize == -1) {
            return false;
        }

        try (InputStream source = connection.getInputStream();
             ReadableByteChannel channel = Channels.newChannel(source);
             FileOutputStream fileOutputStream = new FileOutputStream(destination);
             FileChannel fileChannel = fileOutputStream.getChannel())
        {
            long transferredSize = 0L;
            while (transferredSize < fileSize) {
                transferredSize += fileChannel.transferFrom(channel, transferredSize, 1 << 24);
            }

            return true;
        } catch (IOException e) {
            log.error("Error while downloading file", e);
            throw e;
        } catch (Exception ex) {
            return false;
        }
    }
}
