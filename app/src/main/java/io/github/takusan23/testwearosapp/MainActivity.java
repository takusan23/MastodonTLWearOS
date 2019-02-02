package io.github.takusan23.testwearosapp;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wear.ambient.AmbientModeSupport;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wear.widget.drawer.WearableActionDrawerView;
import android.support.wear.widget.drawer.WearableNavigationDrawerView;
import android.support.wearable.activity.WearableActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class MainActivity extends WearableActivity implements MessageClient.OnMessageReceivedListener,
        AmbientModeSupport.AmbientCallbackProvider,
        MenuItem.OnMenuItemClickListener,
        WearableNavigationDrawerView.OnItemSelectedListener {


    WearableRecyclerView wearableRecyclerView;
    private WearableActionDrawerView mWearableActionDrawer;

    private WearableNavigationDrawerView mWearableNavigationDrawer;
    private int mSelectedPlanet;
    private ArrayList<String> mSolarSystem;

    private String timelineURL = "timelines/public?local=true";


    public static String instanceName = "friends.nico";
    public static String accessToken = "";


    //データ受け渡し
    //https://github.com/JimSeker/wearable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mSolarSystem = initializeSolarSystem();
        mSelectedPlanet = 0;

        //RecyclerView
        wearableRecyclerView = findViewById(R.id.wear_recyclerView);
        wearableRecyclerView.setHasFixedSize(true);
        wearableRecyclerView.setEdgeItemsCenteringEnabled(true);
        wearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        //NavigationDrawer
        mWearableNavigationDrawer =
                (WearableNavigationDrawerView) findViewById(R.id.top_navigation_drawer);
        mWearableNavigationDrawer.setAdapter(new NavigationAdapter(this));
        // Peeks navigation drawer on the top.
        mWearableNavigationDrawer.getController().peekDrawer();
        mWearableNavigationDrawer.addOnItemSelectedListener(new WearableNavigationDrawerView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                //コンテンツを切り替えたときはここ
                reloadTL();
                //Toast.makeText(MainActivity.this, "値 : " + String.valueOf(i), Toast.LENGTH_SHORT).show();
            }
        });
        // Bottom Action Drawer
        mWearableActionDrawer =
                (WearableActionDrawerView) findViewById(R.id.bottom_action_drawer);
        // Peeks action drawer on the bottom.
        mWearableActionDrawer.getController().peekDrawer();
        mWearableActionDrawer.setOnMenuItemClickListener(this);


        final ArrayList<TimelineMenuItem> timelineMenuItem = new ArrayList<>();

        //更新ボタン押す
/*
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
*/

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

    //配列のメニュー
    private ArrayList<String> initializeSolarSystem() {
        ArrayList<String> menuList = new ArrayList<>();
        String[] menu = {"ホーム", "通知", "ローカルTL", "連合TL", "設定"};

        for (int i = 0; i < menu.length; i++) {
            menuList.add(menu[i]);
        }

        return menuList;
    }

    //下からのメニュー
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        final int itemId = menuItem.getItemId();

        String toastMessage = "";

        switch (itemId) {
            case R.id.menu_toot:
                Intent intent = new Intent(MainActivity.this, TootActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_reload:
                reloadTL();
                break;

        }

        mWearableActionDrawer.getController().closeDrawer();
        return true;
    }

    // 上に書いたからいらなくなった
    @Override
    public void onItemSelected(int position) {
    }

    //メニューにあるアイコン、タイトルの設定
    private final class NavigationAdapter
            extends WearableNavigationDrawerView.WearableNavigationDrawerAdapter {

        private final Context mContext;

        public NavigationAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mSolarSystem.size();
        }

        @Override
        public String getItemText(int pos) {
            String title = "ホーム";
            switch (pos) {
                case 0:
                    title = "ホーム";
                    break;
                case 1:
                    title = "通知";
                    break;
                case 2:
                    title = "ローカルTL";
                    break;
                case 3:
                    title = "連合TL";
                    break;
                case 4:
                    title = "設定";
                    break;
            }


            return title;
        }

        @Override
        public Drawable getItemDrawable(int pos) {
            //String navigationIcon = mSolarSystem.get(pos).getNavigationIcon();


            System.out.println("なにこれ : " + String.valueOf(pos));
            Drawable drawable = getDrawable(R.drawable.ic_send_black_24dp);
            switch (pos) {
                case 0:
                    drawable = getDrawable(R.drawable.ic_home_black_24dp);
                    timelineURL = "timelines/home?access_token=" + accessToken;
                    break;
                case 1:
                    drawable = getDrawable(R.drawable.ic_notifications_black_24dp);
                    break;
                case 2:
                    drawable = getDrawable(R.drawable.ic_train_black_24dp);
                    timelineURL = "timelines/public?access_token=" + accessToken + "&local=true";
                    break;
                case 3:
                    drawable = getDrawable(R.drawable.ic_flight_black_24dp);
                    timelineURL = "timelines/public?access_token=" + accessToken + "&local=true";
                    break;
                case 4:
                    drawable = getDrawable(R.drawable.ic_settings_black_24dp);
                    break;
            }

            return drawable;
        }
    }

    @Override
    public AmbientModeSupport.AmbientCallback getAmbientCallback() {
        return new MyAmbientCallback();
    }

    private class MyAmbientCallback extends AmbientModeSupport.AmbientCallback {
        /**
         * Prepares the UI for ambient mode.
         */
        @Override
        public void onEnterAmbient(Bundle ambientDetails) {
            super.onEnterAmbient(ambientDetails);

            //mPlanetFragment.onEnterAmbientInFragment(ambientDetails);
            mWearableNavigationDrawer.getController().closeDrawer();
            mWearableActionDrawer.getController().closeDrawer();
        }

        /**
         * Restores the UI to active (non-ambient) mode.
         */
        @Override
        public void onExitAmbient() {
            super.onExitAmbient();

            //mPlanetFragment.onExitAmbientInFragment();
            mWearableActionDrawer.getController().peekDrawer();
        }
    }

    //TL更新
    private void reloadTL(){
        final ArrayList<TimelineMenuItem> timelineMenuItem = new ArrayList<>();
        //List空に
        timelineMenuItem.clear();

        String url = "https://friends.nico/api/v1/" + timelineURL;
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
}



