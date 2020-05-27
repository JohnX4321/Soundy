package com.thingsenz.soundy.musicplayer.loaders;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;

import com.thingsenz.soundy.musicplayer.db.SongCursorWrapper;
import com.thingsenz.soundy.musicplayer.db.SongHelper;
import com.thingsenz.soundy.musicplayer.models.Album;
import com.thingsenz.soundy.musicplayer.models.Artist;
import com.thingsenz.soundy.musicplayer.models.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SongLoader {

    private static SongLoader sSongDataLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;
    private List<Song> songs;

    private SongLoader(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new SongHelper(mContext).getWritableDatabase();
        songs = querySongs();
    }


    public static SongLoader get(Context context) {
        if (sSongDataLab == null) {
            sSongDataLab = new SongLoader(context);
        }
        return sSongDataLab;
    }

    public Song getSong(long id) {
        SongCursorWrapper cursorWrapper = querySong("_id=" + id, null);
        try {
            if (cursorWrapper.getCount() != 0) {
                cursorWrapper.moveToFirst();
                return cursorWrapper.getSong();
            }
            return Song.EMPTY();
        } finally {
            cursorWrapper.close();
        }
    }

    public Song getRandomSong() {
        Random r = new Random();
        return songs.get(r.nextInt(songs.size() - 1));
    }

    public Song getNextSong(Song currentSong) {
        try {
            return songs.get(songs.indexOf(currentSong) + 1);
        } catch (Exception e) {
            return getRandomSong();
        }
    }


    public Song getPreviousSong(Song currentSong) {
        try {
            return songs.get(songs.indexOf(currentSong) - 1);
        } catch (Exception e) {
            return getRandomSong();
        }
    }

    public List<Song> getSongs() {
        return songs;
    }

    public List<Song> querySongs() {
        List<Song> songs = new ArrayList();
        SongCursorWrapper cursor = querySong(null, null);
        try {
            cursor.moveToFirst();
            do {
                Song song = cursor.getSong();
                song = cursor.getSong();
                song.setAlbumArt(getAlbumUri(song.getAlbumId()).toString());
                songs.add(song);
            } while (cursor.moveToNext());
        } finally {
            cursor.close();
        }
        return songs;
    }

    // incomplete get artist context
    public ArrayList<Album> getAlbums() {
        List<Song> songs = getSongs();

        // Organize song as: {albumId=>{song,song,song},albumId=>{song,song}}
        Map<Integer, ArrayList<Song>> albumMap = new HashMap<>();
        ArrayList<Album> allAlbums = new ArrayList<>();
        for (Song song : songs) {
            if (albumMap.get(song.getAlbumId()) == null) {
                albumMap.put(song.getAlbumId(), new ArrayList<Song>());
                albumMap.get(song.getAlbumId()).add(song);
            } else {
                albumMap.get(song.getAlbumId()).add(song);
            }
        }

        // Extracting and mapping the songlist
        for (Map.Entry<Integer, ArrayList<Song>> entry : albumMap.entrySet()) {
            ArrayList<Song> albumSpecificSongs = new ArrayList<>(entry.getValue());
            allAlbums.add(new Album(albumSpecificSongs));
        }

//          Debugging Code
//        for(Album k:allAlbums) {
//            for(Song song: k.getAlbumSongs()) {
//                Log.i("Test","Album Title: "+song.getAlbumName()+"Album ID: "+song.getAlbumId()+" Song Name: "+song.getTitle());
//            }
//        }
//        Log.i("All Albums list: ",allAlbums.size()+"");
        return allAlbums;
    }

    public ArrayList<Artist> getArtists() {
        ArrayList<Album> albums = getAlbums();
        Map<Integer, ArrayList<Album>> artistMap = new HashMap<>();
        ArrayList<Artist> allArtists = new ArrayList<>();
        for (Album album : albums) {
            if (artistMap.get(album.getArtistId()) == null) {
                artistMap.put(album.getArtistId(), new ArrayList<Album>());
                artistMap.get(album.getArtistId()).add(album);
            } else {
                artistMap.get(album.getArtistId()).add(album);
            }
        }

        // Extracting and mapping the songlist
        for (Map.Entry<Integer, ArrayList<Album>> entry : artistMap.entrySet()) {
            ArrayList<Album> artistSpecificAlbum = new ArrayList<>();
            for (Album album : entry.getValue()) {
                artistSpecificAlbum.add(album);
            }
            allArtists.add(new Artist(artistSpecificAlbum));
        }

        //Debugging Code
//        for(Artist k:allArtists) {
//            for(Album album: k.getAlbums()) {
//                Log.i("Test "+album.getArtistId(),"Album Title: "+album.getName());
//            }
//        }
//        Log.i("All Albums list: ",allArtists.size()+"");
//
        return allArtists;
    }

    private SongCursorWrapper querySong(String whereClause, String[] whereArgs) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection;
        if (whereClause != null) {
            selection = MediaStore.Audio.Media.IS_MUSIC + "!=0 AND " + whereClause;
        } else {
            selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        }
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null,
                selection, // where clause
                whereArgs,       //whereargs
                sortOrder);
        return new SongCursorWrapper(cursor);
    }

    // returns the albumart uri
    private Uri getAlbumUri(int albumId) {
        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(albumArtUri, albumId);
    }

}
