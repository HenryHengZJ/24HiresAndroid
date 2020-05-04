package com.zjheng.jobseed.jobseed.Mlab;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.MalformedInputException;

/**
 * Created by zhen on 6/4/2018.
 */

public class HTTPDataHandler {

    static String stream = null;

    public HTTPDataHandler() {

    }

    public String GetHTTPData(String urlString) {

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setReadTimeout(1000);
            urlConnection.setConnectTimeout(1000);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            //Check connection status
            if (urlConnection.getResponseCode() == 200) {
                Log.e("urlConnection", "getResponseCode 200" );
                //If Successful
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                //Read the BufferedInputStream
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line=r.readLine()) != null) {
                    Log.e("SBLINE", "sb line " + line);
                    sb.append(line);
                    stream = sb.toString();
                    Log.e("stream", "stream line " + stream);
                    urlConnection.disconnect();
                }

            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return stream;

    }


}
