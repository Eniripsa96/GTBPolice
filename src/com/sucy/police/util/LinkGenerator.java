package com.sucy.police.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/**
 * Generates short links for URLs
 */
public class LinkGenerator implements Runnable
{
    private static final String url = "https://www.googleapis.com/urlshortener/v1/url?shortUrl=http://goo.gl/fbsS&key=AIzaSyAyL_2Nf11eKdWjGWSqix5QbO-cPjKJwYE";
    private String longUrl;
    private HashMap<String, String> target;
    private String key;

    /**
     * Constructor
     *
     * @param target target map to put the link in
     * @param key    key for the map
     * @param u      url to generate a shorter one for
     * @throws MalformedURLException
     */
    public LinkGenerator(HashMap<String, String> target, String key, String u)
            throws MalformedURLException
    {
        this.target = target;
        this.key = key;
        this.longUrl = u;
    }

    /**
     * Shortens the link and informs the player
     */
    @Override
    public void run() {
        String shortURL = null;
        try
        {
            // Send the request
            URLConnection conn = new URL(url).openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write("{\"longUrl\":\"" + this.longUrl + "\"}");
            wr.flush();

            // Receive the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null)
            {
                if (line.contains("id")) {
                    shortURL = line.substring(8, line.length() - 2);
                    break;
                }
            }

            // Close the stream
            wr.close();
            rd.close();
        }
        catch (Exception ex) {
            // Do nothing
        }

        // Send the message
        if (shortURL == null) {
            shortURL = longUrl;
        }
        target.put(key, shortURL);
    }
}