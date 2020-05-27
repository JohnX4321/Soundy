package com.thingsenz.soundy.musicplayer.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thingsenz.soundy.R;
import com.thingsenz.soundy.musicplayer.adapters.SongAdapter;
import com.thingsenz.soundy.musicplayer.models.Song;
import com.thingsenz.soundy.musicplayer.services.MusicService;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity  extends AppCompatActivity {

    RecyclerView recyclerView;
    SongAdapter adapter;
    List<Song> songs;
    MusicService musicService;
    boolean boundStatus=false;
    Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_search);
        songs = new ArrayList<>();
        adapter = new SongAdapter(songs,this);
        recyclerView = (RecyclerView)findViewById(R.id.rv_search_song_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        serviceIntent = new Intent(this,MusicService.class);
        serviceIntent.setAction("");
        bindService(serviceIntent,serviceConnection, Context.BIND_AUTO_CREATE);
        startService(serviceIntent);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) iBinder;
            musicService=binder.getService();
            boundStatus=true;
            initSearch();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            boundStatus=false;
        }
    };

    public void initSearch() {
        songs = musicService.getSongs();
        adapter = new SongAdapter(songs,this);
        recyclerView.setAdapter(adapter);
        handleSongClick();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = this.getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.searchSongItem);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setIconified(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void handleSongClick() {

        //click on song item title and artist name listener
        adapter.setOnSongItemClickListener(new SongAdapter.SongItemClickListener() {
            @Override
            public void onSongItemClick(View v, Song song, final int pos) {
                musicService.play(song);
            }
        });

        // long click on song title and artist listener
        adapter.setOnSongItemLongClickListener(new SongAdapter.SongItemLongClickListener() {

            @Override
            public void onSongItemLongClickListener(View v, Song song, int pos) {
            }
        });

        // song menu click listener
        adapter.setOnSongBtnClickListener(new SongAdapter.SongBtnClickListener() {
            @Override
            public void onSongBtnClickListener(ImageButton btn, View v, final Song song, final int pos) {
                final PopupMenu popupMenu=new PopupMenu(getApplicationContext(),btn);
                popupMenu.getMenuInflater().inflate(R.menu.song_action_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("Play")) {
                            musicService.play(song);
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }
}