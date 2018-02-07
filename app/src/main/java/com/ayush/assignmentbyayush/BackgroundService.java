package com.ayush.assignmentbyayush;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Amit Yadav on 2/6/2018.
 */

public class BackgroundService extends IntentService {
    public BackgroundService(String name) {
        super(name);
    }

    public BackgroundService() {
        super("Worker Thread");

    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // Do work here
        URL url = null;
        try {
            url = new URL("https://test-api.nevaventures.com/");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append('\n');
            }
            String json_string = total.toString();
            JSONObject jsonObject = new JSONObject(json_string);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            int length = jsonArray.length();
            ArrayList id = new ArrayList();
            ArrayList name = new ArrayList();
            ArrayList skills = new ArrayList();
            ArrayList imagesRef = new ArrayList();
            int count = 0;
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                if (jsonObject1.has("id") && jsonObject1.has("name") && jsonObject1.has("skills") && jsonObject1.has("image")) {
                    if (jsonObject1.getString("id").isEmpty() || jsonObject1.getString("name").isEmpty() || jsonObject1.getString("skills").isEmpty() || jsonObject1.getString("image").isEmpty()) {

                    } else {
                        if (!(id.contains(jsonObject1.getString("id")))) {
                            id.add(jsonObject1.getString("id"));
                            name.add(jsonObject1.getString("name"));
                            skills.add(jsonObject1.getString("skills"));
                            imagesRef.add(jsonObject1.getString("image"));
                            count++;
                        }

                    }
                }
            }
            for(int i=0;i<count;i++){
                Log.e("link--->",""+(String) imagesRef.get(i));
            }
            //sending local Broadcast
            Intent intent1 = new Intent("send");
            intent1.putCharSequenceArrayListExtra("id", id);
            intent1.putCharSequenceArrayListExtra("name", name);
            intent1.putCharSequenceArrayListExtra("skills", skills);
            intent1.putCharSequenceArrayListExtra("image", imagesRef);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
