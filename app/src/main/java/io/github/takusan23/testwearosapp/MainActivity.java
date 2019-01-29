package io.github.takusan23.testwearosapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends WearableActivity implements MessageClient.OnMessageReceivedListener {

    ImageButton button;
    //TextView textView;
    ImageButton sendImageButton;

    WearableRecyclerView wearableRecyclerView;

    public static String instanceName = "friends.nico";
    public static String accessToken = "";


    //データ受け渡し
    //https://github.com/JimSeker/wearable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button2);
        //textView = findViewById(R.id.textView);
        sendImageButton = findViewById(R.id.send_button);
        //RecyclerView
        wearableRecyclerView = findViewById(R.id.wear_recyclerView);
        wearableRecyclerView.setHasFixedSize(true);
        wearableRecyclerView.setEdgeItemsCenteringEnabled(true);
        wearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));

        final ArrayList<TimelineMenuItem> timelineMenuItem = new ArrayList<>();

        //トゥートボタン？
        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TootActivity.class);
                startActivity(intent);
            }
        });


        //更新ボタン押す
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //List空に
                timelineMenuItem.clear();

                String url = "https://friends.nico/api/v1/timelines/public?local=true&limit=40";
                //作成
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();

                //GETリクエスト
                OkHttpClient client_1 = new OkHttpClient();
                client_1.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String response_string = response.body().string();
                        try {
                            JSONArray jsonArray = new JSONArray(response_string);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                    final String name = jsonArray.getJSONObject(i).getJSONObject("account").getString("display_name");
                                    final String id = jsonArray.getJSONObject(i).getJSONObject("account").getString("acct");
                                    final String avatar = jsonArray.getJSONObject(i).getJSONObject("account").getString("avatar");
                                    final String toot = Html.fromHtml(jsonArray.getJSONObject(i).getString("content"), Html.FROM_HTML_MODE_COMPACT).toString();
                                    final String toot_id = jsonArray.getJSONObject(i).getString("id");
                                    //TextView
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //追加
                                            timelineMenuItem.add(new TimelineMenuItem(toot_id, name + " @" + id, toot, avatar));
                                            //入れる
                                            wearableRecyclerView.setAdapter(new TimelineAdapter(MainActivity.this, timelineMenuItem, new TimelineAdapter.AdapterCallback() {
                                                @Override
                                                public void onItemClicked(Integer menuPosition) {

                                                }
                                            }));

                                        }
                                    });


                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    public void onResume() {
        super.onResume();
        Wearable.getMessageClient(this).addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Wearable.getMessageClient(this).removeListener(this);
    }

    //受け取り
    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        //sendMessage var1 にいれた名前をequalsに入れる
        if (messageEvent.getPath().equals("/message")) {
            String message = new String(messageEvent.getData());
            // とーすと
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

        }
    }
}



