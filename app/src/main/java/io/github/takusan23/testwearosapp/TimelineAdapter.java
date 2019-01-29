package io.github.takusan23.testwearosapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.RecyclerViewHolder> {

    private ArrayList<TimelineMenuItem> dataSource = new ArrayList<TimelineMenuItem>();

    public interface AdapterCallback {
        void onItemClicked(Integer menuPosition);
    }

    private AdapterCallback callback;

    private String drawableIcon;
    private Context context;

    public TimelineAdapter(Context context, ArrayList<TimelineMenuItem> dataArgs, AdapterCallback callback) {
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_layout, parent, false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        LinearLayout menuContainer;
        TextView tootTextView;
        TextView nameTextView;
        ImageView timelineImageView;
        //Fav/BT
        TextView favButton;
        TextView btButton;

        public RecyclerViewHolder(View view) {
            super(view);
            menuContainer = view.findViewById(R.id.menu_container);
            tootTextView = view.findViewById(R.id.timeline_textView);
            nameTextView = view.findViewById(R.id.timeline_name_textView);
            timelineImageView = view.findViewById(R.id.timeline_imageView);

            favButton = view.findViewById(R.id.favTextView);
            btButton = view.findViewById(R.id.btTextView);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        final TimelineMenuItem data_provider = dataSource.get(position);

        holder.tootTextView.setText(data_provider.getText());
        holder.nameTextView.setText(data_provider.getName());
        //アバター
        Glide.with(context).load(data_provider.getAvatarURL()).into(holder.timelineImageView);

        //アイコン
        holder.favButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_border_black_24dp, 0, 0, 0);
        holder.btButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_repeat_black_24dp, 0, 0, 0);


        //Favる
        holder.favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://" + MainActivity.instanceName + "/api/v1/statuses/" + data_provider.getID() + "/favourite?access_token=" + MainActivity.accessToken;
                //ぱらめーたー
                RequestBody requestBody = new FormBody.Builder()
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
                        Toast.makeText(context, "ふぁぼったよ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        //ブースト
        holder.btButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://" + MainActivity.instanceName + "/api/v1/statuses/" + data_provider.getID() + "/reblog?access_token=" + MainActivity.accessToken;
                //ぱらめーたー
                RequestBody requestBody = new FormBody.Builder()
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
                        Toast.makeText(context, "ふぁぼったよ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.menuContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (callback != null) {
                    callback.onItemClicked(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}
