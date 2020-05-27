package com.thingsenz.soundy.musicplayer.models;

import java.util.ArrayList;

public class Album {

    public final ArrayList<Song> songs;
    public int id;

    public Album(ArrayList<Song> songs) {
        this.songs = songs;
    }
    public void setId(int i) {
        this.id=i;
    }
    public ArrayList<Song> getAlbumSongs() {
        return songs;
    }

    public  String getName() {
        if(songs.size()>0) {
            return songs.get(0).getAlbumName();
        }else {
            return " ";
        }
    }
    public int getNoOfSong() {
        return songs.size();
    }

    public String getCoverArt() {
        if(songs.size()>0) {
            return songs.get(0).getAlbumArt();
        }else {
            return " ";
        }
    }

    public int getArtistId() {
        if(songs.size()>0) {
            return songs.get(0).getArtistId();
        }else {
            return 0;
        }
    }
    public String getArtistName() {
        if(songs.size()>0) {
            return songs.get(0).getArtistName();
        }else {
            return "";
        }
    }

}
