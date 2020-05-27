package com.thingsenz.soundy.musicplayer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thingsenz.soundy.R;
import com.thingsenz.soundy.musicplayer.adapters.ArtistAdapter;
import com.thingsenz.soundy.musicplayer.models.Artist;
import com.thingsenz.soundy.musicplayer.services.MusicService;

import java.util.ArrayList;
import java.util.List;

public class ArtistListFragment extends MusicServiceFragment {

    private MusicService musicService;
    private boolean musicServiceStatus = false;
    private RecyclerView recyclerView;
    private ArtistAdapter artistAdapter;
    private List<Artist> artists;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_list,container,false);
        recyclerView =  view.findViewById(R.id.rv_artist_list);
        artists = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        artistAdapter = new ArtistAdapter(artists,getContext());
        if(musicServiceStatus) {initFragment(); }
        return view;
    }

    @Override
    public void onServiceConnected(MusicService musicService) {
        this.musicService = musicService;
        musicServiceStatus=true;
        initFragment();
    }

    @Override
    public void onServiceDisconnected() {

    }

    public void initFragment() {
        artists = musicService.getArtists();
        artistAdapter = new ArtistAdapter(artists, getContext());
        recyclerView.setAdapter(artistAdapter);
    }

}