package com.github.lbovolini.crowd.utils;

import com.github.lbovolini.crowd.configuration.Config;

import java.net.MalformedURLException;
import java.net.URL;

public class URLUtils {

    public static String join(URL[] urls) {

        StringBuilder urlsString = new StringBuilder();

        for (URL url : urls) {
            urlsString = urlsString.append(url.toString()).append(Config.URL_SEPARATOR));
        }

        return urlsString.toString();
    }

    public static URL[] split(String urlsString) {

        if (urlsString == null || urlsString.equals("")) {
            return null;
        }

        String[] strURL = urlsString.split(Config.URL_SEPARATOR);
        URL[] urls = new URL[strURL.length];

        for (int i = 0; i < strURL.length; i++) {
            try {
                urls[i] = new URL(strURL[i]);
            } catch (MalformedURLException e) { e.printStackTrace(); }
        }

        return urls;
    }

}
