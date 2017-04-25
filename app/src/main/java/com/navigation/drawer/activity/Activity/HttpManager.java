package com.navigation.drawer.activity.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by amine on 4/2/2017.
 */

public class HttpManager {
    public static String getDatas(String jsonURL) throws IOException {
        URL url;
        String response = "";
        try {
            url = new URL(jsonURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null)
                    response += line;
            } else
                response = "";

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
    public static User JSONUser(String content) throws JSONException {

        JSONObject jsonObject = new JSONObject(content);

            User user = new User();
            user.setId(jsonObject.get("id").toString());
            user.setDate_permi(jsonObject.get("date_permi").toString());
            user.setNom(jsonObject.get("nom").toString());
            user.setPassword(jsonObject.get("password").toString());
            user.setPrenom(jsonObject.get("prenom").toString());
            user.setNum_permi(jsonObject.get("num_permi").toString());
            user.setSexe(jsonObject.get("sexe").toString());
            user.setTel(jsonObject.get("tel").toString());
            user.setEmail(jsonObject.get("email").toString());

        return user ;
    }
}
