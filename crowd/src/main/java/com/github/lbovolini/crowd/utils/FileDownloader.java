package com.github.lbovolini.crowd.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class FileDownloader {

    public static void download(URL from, String destination, String name) throws IOException {
        URL url = new URL(from + name);
        download(url, destination);
    }

    public static void download(URL from, String destination) throws IOException {

        URLConnection connection = from.openConnection();
        long fileSize = connection.getContentLength();

        try (InputStream source = connection.getInputStream();
             ReadableByteChannel channel = Channels.newChannel(source);
             FileOutputStream fileOutputStream = new FileOutputStream(destination);
             FileChannel fileChannel = fileOutputStream.getChannel())
        {
            long transferredSize = 0L;
            while(transferredSize < fileSize) {
                transferredSize += fileChannel.transferFrom(channel, transferredSize, 1 << 24);
            }
        }
    }
}
