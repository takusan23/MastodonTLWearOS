package io.github.takusan23.testwearosapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wear.widget.drawer.WearableActionDrawerView;
import android.support.wearable.activity.WearableActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends WearableActivity implements MessageClient.OnMessageReceivedListener {

    ImageButton button;
    TextView textView;
    ImageButton sendImageButton;
    private Set<Node> mWearNodeIds;
    private String mPhoneNodeId;

    public static final String TEST_TRANSCRIPTION_MESSAGE_PATH = "/test_transcription";

    //データ受け渡し
    //https://github.com/JimSeker/wearable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button2);
        textView = findViewById(R.id.textView);
        sendImageButton = findViewById(R.id.send_button);


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
                                            textView.append(name + " @" + id);
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



