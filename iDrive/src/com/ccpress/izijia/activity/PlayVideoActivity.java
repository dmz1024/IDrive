package com.ccpress.izijia.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import com.ccpress.izijia.R;
import com.ccpress.izijia.view.FullScreenVideoView;
import com.trs.app.TRSFragmentActivity;
import com.trs.util.StringUtil;

/**
 * Created by WLH on 2015/10/13 15:14.
 * viode播放
 */
public class PlayVideoActivity extends TRSFragmentActivity{

    public static final String EXTRA_URL = "videourl";

    private FullScreenVideoView videoView;

    private View  loadView;

    private MediaController mediaController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        String url = getIntent().getStringExtra(EXTRA_URL);

        videoView = (FullScreenVideoView) findViewById(R.id.play);
        loadView = findViewById(R.id.loading_view);
        loadView.setVisibility(View.VISIBLE);

        if(StringUtil.isEmpty(url)){
            Toast.makeText(this, "视频源有误", Toast.LENGTH_SHORT).show();
            finish();
        }

        Uri uri = Uri.parse(url);
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.requestFocus();
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {

                loadView.setVisibility(View.GONE);
                return false;
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                loadView.setVisibility(View.GONE);
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        videoView.stopPlayback();
        super.onDestroy();
    }
}
