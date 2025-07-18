package com.example.haminavodayaho.ServerRequest;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

public class _HttpPostRequest {

    public static class PostRequestTask extends AsyncTask<Void, Void, String> {

        private String urlString;
        private Map<String, String> data;
        private PostRequestCallback callback;

        public PostRequestTask(String urlString, Map<String, String> data, PostRequestCallback callback) {
            this.urlString = urlString;
            this.data = data;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                return sendPostRequest(urlString, data);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("TAGA", "PostRequestTask: "+e.toString());
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            callback.onPostRequestComplete(result);
        }

        private String sendPostRequest(String urlString, Map<String, String> data) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

            StringBuilder postData = new StringBuilder();
            Set<Map.Entry<String, String>> entrySet = data.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                if (postData.length() > 0) {
                    postData.append("&");
                }
                postData.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                postData.append("=");
                postData.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(postData.toString().getBytes("UTF-8"));
            }

            int responseCode = connection.getResponseCode();
            InputStream inputStream = (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST)
                    ? connection.getErrorStream()
                    : connection.getInputStream();

            StringBuilder response = new StringBuilder();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            return response.toString();
        }
    }

    public interface PostRequestCallback {
        void onPostRequestComplete(String response);
    }
}




/*
//===========================================================================
        String url = MAIN_URL + "index/";

        Map<String, String> data = new HashMap<>();
        data.put("curr_user", USER);

        new _HttpPostRequest.PostRequestTask(url, data, new _HttpPostRequest.PostRequestCallback() {
            @Override
            public void onPostRequestComplete(String response) {
                Log.d("TAGA", "onPostRequestComplete: "+response);
            }
        }).execute();
//===========================================================================
 */
