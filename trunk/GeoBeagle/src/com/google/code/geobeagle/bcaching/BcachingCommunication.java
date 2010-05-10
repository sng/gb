/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.google.code.geobeagle.bcaching;

import org.apache.http.HttpException;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Communicates with the bacching.com server to fetch geocaches as a GPX
 * InputStream.
 * 
 * @author Mark Bastian
 */
public class BCachingCommunication {
    private final String mUsername;
    private final String mHashword;
    private final String mBaseUrl = "http://www.bcaching.com/api";
    private final int mTimeout = 20000; // millisec

    public BCachingCommunication(String username, String password) {
        mUsername = username;
        String hashword = "";
        try {
            hashword = encodeHashword(username, password);
        } catch (Exception ex) {
            Log.e("GeoBeagle", ex.toString());
        }
        mHashword = hashword;
    }

    public void validateCredentials() throws Exception {
        // attempt to login at server
        // failure will throw an exception
        sendRequest("a=login&app=geohunter");
    }

    public String encodeHashword(String username, String password) throws Exception {
        return encodeMd5Base64(password + username);
    }

    private String encodeQueryString(String username, String hashword, String params)
            throws NoSuchAlgorithmException, DigestException {

        if (username == null)
            throw new IllegalArgumentException("username is required.");
        if (hashword == null)
            throw new IllegalArgumentException("hashword is required.");
        if (params == null || params.length() == 0)
            throw new IllegalArgumentException("params are required.");

        StringBuffer sb = new StringBuffer();
        sb.append("u=");
        sb.append(URLEncoder.encode(username));
        sb.append("&");
        sb.append(params);
        sb.append("&time=");
        java.util.Date date = java.util.Calendar.getInstance().getTime();
        sb.append(date.getTime());
        String signature = encodeMd5Base64(sb.toString() + hashword);
        sb.append("&sig=");
        sb.append(URLEncoder.encode(signature));
        return sb.toString();
    }

    public InputStream sendRequest(Hashtable<String, String> params) throws IOException,
            HttpException, NoSuchAlgorithmException, DigestException {
        if (params == null || params.size() == 0)
            throw new IllegalArgumentException("params are required.");
        if (!params.containsKey("a"))
            throw new IllegalArgumentException("params must include an action (key=a)");

        StringBuffer sb = new StringBuffer();
        Enumeration<String> keys = params.keys();
        while (keys.hasMoreElements()) {
            if (sb.length() > 0)
                sb.append('&');
            String k = keys.nextElement();
            sb.append(k);
            sb.append('=');
            sb.append(URLEncoder.encode(params.get(k)));
        }

        String request = sb.toString();
        Log.d("GeoBeagle", "sending request: " + request);
        return sendRequest(request);
    }

    public InputStream sendRequest(String query) throws IOException, HttpException,
            NoSuchAlgorithmException, DigestException {
        if (query == null || query.length() == 0)
            throw new IllegalArgumentException("query is required");

        final URL url = getURL(mUsername, mHashword, query);
        Log.d("GeoBeagle", "sending url: " + url);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setReadTimeout(mTimeout);
        conn.setConnectTimeout(mTimeout);
        conn.addRequestProperty("Accept-encoding", "gzip");
        int responseCode = conn.getResponseCode();
        InputStream in = conn.getInputStream();
        Log.d("GeoBeagle", "BCachingCommunication response length=" + conn.getContentLength());
        if (conn.getContentEncoding().equalsIgnoreCase("gzip")) {
            in = new java.util.zip.GZIPInputStream(in);
        }
        if (responseCode != HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            throw new HttpException(sb.toString());
        }
        return in;
    }

    private URL getURL(String username, String hashword, String params)
            throws MalformedURLException, NoSuchAlgorithmException, DigestException {
        return new URL(mBaseUrl + "/q.ashx?" + encodeQueryString(username, hashword, params));
    }

    public String encodeMd5Base64(String s) throws NoSuchAlgorithmException, DigestException {
        byte[] buf = s.getBytes();
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        md.update(buf, 0, buf.length);
        buf = new byte[16];
        md.digest(buf, 0, buf.length);
        return base64Encode(buf);
    }

    private static char[] map1 = new char[64];
    static {
        int i = 0;
        for (char c = 'A'; c <= 'Z'; c++) {
            map1[i++] = c;
        }
        for (char c = 'a'; c <= 'z'; c++) {
            map1[i++] = c;
        }
        for (char c = '0'; c <= '9'; c++) {
            map1[i++] = c;
        }
        map1[i++] = '+';
        map1[i++] = '/';
    }

    public static String base64Encode(byte[] in) {
        int iLen = in.length;
        int oDataLen = (iLen * 4 + 2) / 3;// output length without padding
        int oLen = ((iLen + 2) / 3) * 4;// output length including padding
        char[] out = new char[oLen];
        int ip = 0;
        int op = 0;
        int i0, i1, i2, o0, o1, o2, o3;
        while (ip < iLen) {
            i0 = in[ip++] & 0xff;
            i1 = ip < iLen ? in[ip++] & 0xff : 0;
            i2 = ip < iLen ? in[ip++] & 0xff : 0;
            o0 = i0 >>> 2;
            o1 = ((i0 & 3) << 4) | (i1 >>> 4);
            o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
            o3 = i2 & 0x3F;
            out[op++] = map1[o0];
            out[op++] = map1[o1];
            out[op] = op < oDataLen ? map1[o2] : '=';
            op++;
            out[op] = op < oDataLen ? map1[o3] : '=';
            op++;
        }
        return new String(out);
    }
}
