package com.example.toshiba.youtubedemo;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YoutubeApiCardViewVolleyActivity extends AppCompatActivity implements ApiViewVolleyInterface {
    final String JsonURL = "http://codemobiles.com/adhoc/youtubes/index_new.php?username=admin&password=password&type=foods";
    @BindView(R.id.cardview_youtube_volley) private RecyclerView mRecycleViewVolley;
    @BindView(R.id.swipe_youtube_cardview_volley) private SwipeRefreshLayout mSwipeRefreshLayout;
    List<VideoClip> clips;
    ApiVolleyModel apiVolleyModel;
    ApiVolleyPresenter apiVolleyPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_api_card_view_volley);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        apiVolleyModel = new ApiVolleyModel(getApplicationContext());
        apiVolleyPresenter = new ApiVolleyPresenter(apiVolleyModel,JsonURL);
        apiVolleyPresenter.bind(this);
        apiVolleyPresenter.displayAllResult();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void updateUi(JSONObject response) {
        Gson gson = new Gson();
        Youtube youtube = gson.fromJson(String.valueOf(response),Youtube.class);
        clips = youtube.getClips();
        Log.i("JsonClips",String.valueOf(clips));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecycleViewVolley.setLayoutManager(layoutManager);

        YoutubeApiCardViewVolleyAdapter adapter = new YoutubeApiCardViewVolleyAdapter(getApplicationContext(),clips);
        mRecycleViewVolley.setAdapter(adapter);
    }

    @Override
    public void updateErrorResponse(VolleyError volleyError) {
        Toast.makeText(getApplicationContext(),volleyError.toString(),Toast.LENGTH_LONG).show();
    }

    private class YoutubeApiCardViewVolleyAdapter extends RecyclerView.Adapter<ViewHolder>{
        Context ctx;
        List<VideoClip> clips;

        YoutubeApiCardViewVolleyAdapter(Context ctx, List<VideoClip> clips) {
            this.ctx = ctx;
            this.clips = clips;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ctx).inflate(R.layout.item_cardview_clips,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            VideoClip videoClip = clips.get(position);

            Glide.with(ctx).load(videoClip.getYoutube_image()).fitCenter().into(holder.youtube_image);
            Glide.with(ctx).load(videoClip.getAvatar_image()).fitCenter().into(holder.avatar_image);
            holder.title.setText(videoClip.getTitle());
            holder.subtitle.setText(videoClip.getSubtitle());
        }

        @Override
        public int getItemCount() {
            return clips.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.image_youtube_cardview) ImageView youtube_image;
        @BindView(R.id.image_avatar_cardview) ImageView avatar_image;
        @BindView(R.id.text_title_cardview) TextView title;
        @BindView(R.id.text_subtiltle_cardview) TextView subtitle;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
