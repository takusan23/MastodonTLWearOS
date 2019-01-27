package io.github.takusan23.testwearosapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends WearableActivity {

    ImageButton button;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button2);
        textView = findViewById(R.id.textView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TextView空に
                textView.setText("");

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
                                    final String toot = Html.fromHtml(jsonArray.getJSONObject(i).getString("content"), Html.FROM_HTML_MODE_COMPACT).toString();

                                    //TextView
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textView.append("---------");
                                            textView.append("\n");
                                            textView.append(name +" @" + id);
                                            textView.append("\n");
                                            textView.append(toot);
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
}
