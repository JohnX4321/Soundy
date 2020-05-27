package com.thingsenz.soundy.musicplayer.models;

import java.util.ArrayList;

public class Artist {
    public final ArrayList<Album> albums;

    public Artist(ArrayList<Album> albums) {
        this.albums = albums;
    }
    public ArrayList<Album> getAlbums() {
        return this.albums;
    }
    public String getArtistName() {
        if(albums.size()>0) {
            return albums.get(0).getArtistName();
        } else {
            return "";
        }
    }
    public String getArtistImage() {
        if(albums.size()>0) {
            return albums.get(0).getCoverArt();
        } else {
            return "";
        }
    }
    public int getAlbumCount() {
        if (albums.size() > 0) {
            return albums.size();
        } else {
            return 0;
        }
    }


    public int getSongCount() {
        int sum =0;
        for(Album albumModel:albums) {
            sum+=albumModel.getNoOfSong();
        }
        return sum;
    }
}
