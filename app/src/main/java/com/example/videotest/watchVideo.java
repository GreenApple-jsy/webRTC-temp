package com.example.videotest;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.spherical.SphericalSurfaceView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class watchVideo extends AppCompatActivity {
    SimpleExoPlayer player;
    private PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);
        playerView = findViewById(R.id.exoPlayerView);

        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "videotest")); //마지막 항목은 어플리케이션 이름

        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse("http://1.234.38.211/Video_dir/1.mp4")); //동영상 링크


        player.prepare(videoSource);
        player.setPlayWhenReady(player.getPlayWhenReady());

        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                switch (playbackState) {

                    case Player.STATE_IDLE: // 1
                        //재생 실패
                        break;
                    case Player.STATE_BUFFERING: // 2
                        // 재생 준비
                        break;
                    case Player.STATE_READY: // 3
                        // 재생 준비 완료
                        break;
                    case Player.STATE_ENDED: // 4
                        // 재생 마침
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onStop() { //액티비티에서 벗어날 때, 플레이어 release 필수
        super.onStop();
        releasePlayer();
    }

    private void releasePlayer() {
        if (player != null) {
            playerView.setPlayer(null);
            player.release();
            player = null;
        }
    }


}