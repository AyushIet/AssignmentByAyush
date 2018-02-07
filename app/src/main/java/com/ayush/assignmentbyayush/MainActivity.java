package com.ayush.assignmentbyayush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView textView;
    ArrayList id, name, skills, image;
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.main_activity_text_view);
        recyclerView = findViewById(R.id.main_activity_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            textView.setVisibility(View.GONE);
            id = intent.getCharSequenceArrayListExtra("id");
            name = intent.getCharSequenceArrayListExtra("name");
            skills = intent.getCharSequenceArrayListExtra("skills");
            image = intent.getCharSequenceArrayListExtra("image");
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(new MyAdapter());
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("send"));
    }

    public void viewList(View view) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            textView.setText("Please Wait few seconds........");
            Intent intent = new Intent(this, BackgroundService.class);
            startService(intent);
        }else{
            textView.setText("No Internet connection!! Tap Again To Retry");
        }

    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.ui_for_person, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.nameTextView.setText((String) name.get(position));
            holder.skillsTextView.setText((String) skills.get(position));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL((String) image.get(position));
                        //Log.e("child thread---->",(String) image.get(position));
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        InputStream inputStream = httpURLConnection.getInputStream();
                        bmp = BitmapFactory.decodeStream(inputStream);
                        if (bmp != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    holder.imageView.setImageBitmap(bmp);
                                }
                            });
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public int getItemCount() {
            return id.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView nameTextView, skillsTextView;

            public MyViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.pic);
                nameTextView = itemView.findViewById(R.id.name);
                skillsTextView = itemView.findViewById(R.id.skills);
            }
        }
    }
}
