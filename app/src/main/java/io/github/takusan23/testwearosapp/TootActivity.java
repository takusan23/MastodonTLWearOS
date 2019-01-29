package io.github.takusan23.testwearosapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.wear.widget.CircularProgressLayout;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TootActivity extends WearableActivity {

    EditText editText;
    ImageButton sendButton;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toot);

        editText = findViewById(R.id.tootEditText);
        sendButton = findViewById(R.id.tootsendButton);
        frameLayout = findViewById(R.id.tootFrameLayout);
        //トゥート
        //長押し
        sendButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                String url = "https://" + MainActivity.instanceName + "/api/v1/statuses/?access_token=" + MainActivity.accessToken;
                //ぱらめーたー
                RequestBody requestBody = new FormBody.Builder()
                        .add("status", editText.getText().toString())
                        .add("visibility", "direct")
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                //POST
                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //EditTextを空にする
                                editText.setText("");
                                //トースト
                                Toast.makeText(TootActivity.this, "トゥートできたよ", Toast.LENGTH_SHORT).show();
                                //TLに戻る
                                Intent intent = new Intent(TootActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                });
                return false;
            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }
}
