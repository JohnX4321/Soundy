package com.thingsenz.soundy.videostreaming;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection.Factory;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.thingsenz.soundy.R;

public class ExoPlayerActivity extends AppCompatActivity implements Player.EventListener {

    PlayerView videoFullScreenPlayer;
    ProgressBar spinnerVideoDetails;
    ImageView imageViewExit;

    private static final String TAG = "ExoPlayerActivity";
    private static final String KEY_VIDEO_URI = "video_uri";
    String videoUri;
    SimpleExoPlayer player;
    Handler mHandler;
    Runnable mRunnable;

    public static Intent getStartIntent(Context context,String uri) {
        Intent intent=new Intent(context,ExoPlayerActivity.class);
        intent.putExtra(KEY_VIDEO_URI,uri);
        return intent;
    }


    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_player);
        videoFullScreenPlayer=findViewById(R.id.videoFullScreenPlayer);
        spinnerVideoDetails=findViewById(R.id.spinnerVideoDetails);
        imageViewExit=findViewById(R.id.imageViewExit);
        imageViewExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        if (getSupportActionBar()!=null)
            getSupportActionBar().hide();
        if (getIntent().hasExtra(KEY_VIDEO_URI))
            videoUri=getIntent().getStringExtra(KEY_VIDEO_URI);
        initPlayer();
        if (videoUri==null)
            return;
        buildMediaSource(Uri.parse(videoUri));
    }

    private void initPlayer() {
        if (player==null) {
            LoadControl loadControl=new DefaultLoadControl(
                    new DefaultAllocator(true,16),
                    VideoPlayerConfig.MIN_BUFFER_DURATION,
                    VideoPlayerConfig.MAX_BUFFER_DURATION,
                    VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
                    VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER,-1,
                    true
            );
            BandwidthMeter bandwidthMeter=new DefaultBandwidthMeter();
            TrackSelection.Factory videoTracksSelFactory=new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector=new DefaultTrackSelector(videoTracksSelFactory);
            player= ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this),trackSelector,loadControl);
            videoFullScreenPlayer.setPlayer(player);
        }
    }



    private void buildMediaSource(Uri uri) {
        DefaultBandwidthMeter bandwidthMeter=new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory=new DefaultDataSourceFactory(this, Util.getUserAgent(this,getString(R.string.app_name)),bandwidthMeter);
        MediaSource vidSource=new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
        player.prepare(vidSource);
        player.setPlayWhenReady(true);
        player.addListener(this);
    }


    private void releasePlayer() {
        if (player!=null) {
            player.release();
            player=null;
        }
    }


    private void pausePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }
    private void resumePlayer() {
        if (player != null) {
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer();
        if (mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        resumePlayer();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
    }
    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }
    @Override
    public void onLoadingChanged(boolean isLoading) {
    }
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case Player.STATE_BUFFERING:
                spinnerVideoDetails.setVisibility(View.VISIBLE);
                break;
            case Player.STATE_ENDED:
                // Activate the force enable
                break;
            case Player.STATE_IDLE:
                break;
            case Player.STATE_READY:
                spinnerVideoDetails.setVisibility(View.GONE);
                break;
            default:
                // status = PlaybackStatus.IDLE;
                break;
        }
    }
    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }
    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }
    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }
    @Override
    public void onPositionDiscontinuity(int reason) {
    }
    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }
    @Override
    public void onSeekProcessed() {
    }



}
