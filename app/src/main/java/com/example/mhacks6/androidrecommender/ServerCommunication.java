package com.example.pranaygp.httprequest;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ServerCommunication {
    public static final String Logger = ServerCommunication.class.getName();
    private static final String API_KEY = "kz6PIIeDLSumU+QgSBJD2fYT/X/1C92sOnvMVc3jDCRJ4+P3LnaKV6VQ1cwJefQKqqX5XvWeD/HaMWve34+Bhg==";

    private static String slurp(InputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }
    public static String post_string(String url, JSONObject urlParameters) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
        } catch (MalformedURLException e) {
            Log.e(Logger, "MalformedURLException While Creating URL Connection - " + e.getMessage());
            throw e;
        } catch (IOException e) {
            Log.e(Logger, "IOException While Creating URL Connection - " + e.getMessage());
            throw e;
        }
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Content-Length", Integer.toString(urlParameters.toString().length()));
        conn.setRequestProperty("Authorization", "Bearer "+API_KEY);
        OutputStream os = null;
        try {
            os = conn.getOutputStream();
        } catch (IOException e) {
            Log.e(Logger, "IOException While Creating URL OutputStream - " + e.getMessage());
            throw e;
        }
        try {
            os.write(urlParameters.toString().getBytes());
        } catch (IOException e) {
            Log.e(Logger, "IOException While writting URL OutputStream - " + e.getMessage());
            throw e;
        }
        InputStream in = null;
        try {
            in = conn.getInputStream();
        } catch (IOException e) {

            Log.e(Logger, "IOException While Creating URL InputStream - " + conn.getResponseCode() + e.getMessage());
            throw e;
        }
        String output = null;
        try {
            output = slurp(in);
        } catch (IOException e) {
            Log.e(Logger, "IOException While Reading URL OutputStream - " + e.getMessage());
            throw e;
        } finally {
            try {
                os.close();
                in.close();
            } catch (IOException e) {
                Log.e(Logger, "IOException While Closing URL Output and Input Stream - " + e.getMessage());
            }
        }
        conn.disconnect();return output;
    }
}