package com.eweware.phototoss.api;



import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.net.URL;
import java.net.HttpURLConnection;


/**
 * Created by Dave on 2/7/2015.
 */
public class NotifyTest extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyStr = request.getParameter("key");
        String titleStr = request.getParameter("title");
        String bodyStr = request.getParameter("body");


        if ((keyStr != null) && (!keyStr.isEmpty())) {
            Set<String> tags = new HashSet<String>();
            tags.add(keyStr);

            SendNotification(titleStr, bodyStr, tags);
        }
        else
            SendNotification(titleStr, bodyStr, null);

        PrintWriter out = response.getWriter();
        out.write("ok");
        out.flush();
        out.close();

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }


    private void SendNotification(String titleStr, String bodyStr, Set<String> tagMap) throws ServletException, IOException {
        String url = "https://phototossnotify-ns.servicebus.windows.net/phototossnotify/messages/?api-version=2013-08";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type","application/json;charset=utf-8");
        con.setRequestProperty("Authorization", generateSasToken(url));
        con.setRequestProperty("ServiceBusNotification-Format", "gcm");

        if (tagMap != null) {
            StringBuffer exp = new StringBuffer();
            for (Iterator<String> iterator = tagMap.iterator(); iterator.hasNext();) {
                exp.append(iterator.next());
                if (iterator.hasNext())
                    exp.append(" || ");
            }
            con.setRequestProperty("ServiceBusNotification-Tags", exp.toString());

        }

        // Send post request
        String message = "{\"data\":{\"title\":\"" + titleStr + "\", \"body\":\"" + bodyStr + "\"}}";

        con.setDoInput(true);
        con.setDoOutput(true);
        byte[] outputInBytes = message.getBytes("UTF-8");
        OutputStream os = con.getOutputStream();
        os.write(outputInBytes );
        os.close();

        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer notifyresponse = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            notifyresponse.append(inputLine);
        }
        in.close();

        //print result
        String theResult = notifyresponse.toString();
    }

    private String generateSasToken(String uri) {
        String targetUri;
        String SasKeyValue = "9aJhh1pJMXAerRYCv6Kk1feyNHsn+g584gr7xX1N6xI=";
        String SasKeyName = "DefaultFullSharedAccessSignature";
        try {
            targetUri = URLEncoder
                    .encode(uri.toLowerCase(), "UTF-8")
                    .toLowerCase();

            long expiresOnDate = System.currentTimeMillis();
            int expiresInMins = 60; // 1 hour
            expiresOnDate += expiresInMins * 60 * 1000;
            long expires = expiresOnDate / 1000;
            String toSign = targetUri + "\n" + expires;

// Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = SasKeyValue.getBytes("UTF-8");
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA256");

// Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
// Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(toSign.getBytes("UTF-8"));

// using Apache commons codec for base64
            String signature = URLEncoder.encode(
                    Base64.encodeBase64String(rawHmac), "UTF-8");

// construct authorization string
            String token = "SharedAccessSignature sr=" + targetUri + "&sig="
                    + signature + "&se=" + expires + "&skn=" + SasKeyName;
            return token;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
