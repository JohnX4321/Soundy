package com.thingsenz.soundy.musicplayer.interfaces;

import com.thingsenz.soundy.musicplayer.models.Song;

public interface PlayerInterface {

    void start();
    void play(long songId);
    void play(Song song);
    void pause();
    void stop();
    void seekTo(int position);
    boolean isPlaying();
    long getDuration();
    int getCurrentStreamPosition();
    void setCallback(Callback callback);

    interface Callback {
        void onCompletion(Song song);
        void onTrackChange(Song song);
        void onPause();
    }
}