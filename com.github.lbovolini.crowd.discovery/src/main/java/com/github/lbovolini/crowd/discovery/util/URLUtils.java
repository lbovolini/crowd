package com.github.lbovolini.crowd.discovery.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class URLUtils {

    public static final String URL_SEPARATOR = System.getProperty("codebase.url.separator", " ");

    private URLUtils() {}

    public static String join(URL[] urls) {

        StringBuilder urlsString = new StringBuilder();

        for (URL url : urls) {
            urlsString.append(url.toString()).append(URL_SEPARATOR);
        }

        return urlsString.toString();
    }

    public static URL[] split(String urlsString) throws MalformedURLException {

        if (Objects.isNull(urlsString) || urlsString.trim().equals("")) {
            throw new IllegalArgumentException("Argument cannot be null");
        }

        String[] strURL = urlsString.split(URL_SEPARATOR);
        URL[] urls = new URL[strURL.length];

        for (int i = 0; i < strURL.length; i++) {
            if (Objects.isNull(strURL[i])) {
                throw new IllegalArgumentException("URL cannot be null");
            }
            urls[i] = new URL(strURL[i]);
        }

        return urls;
    }

}
