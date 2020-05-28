package com.thingsenz.soundy.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.core.app.NotificationCompat;

import com.thingsenz.soundy.R;
import com.thingsenz.soundy.musicplayer.activities.PlayerActivity;
import com.thingsenz.soundy.musicplayer.models.Song;
import com.thingsenz.soundy.musicplayer.services.MusicService;

public class NotificationHandler {


    public static String PREVIOUS="action.prev",PLAY="action.prev",
        PAUSE="action.pause",NEXT="action.next";


    public static Notification createNotification(Context context, Song currSong,boolean playStatus) {

        NotificationCompat.Builder builder=null;
        Intent openAppIntent=new Intent(context, PlayerActivity.class);
        PendingIntent pendingOAIntent=PendingIntent.getActivity(context,0,openAppIntent,0);


        Intent prevIntent=new Intent(context,MusicService.class);
        prevIntent.setAction(PREVIOUS);
        PendingIntent pprevInt=PendingIntent.getService(context,0,prevIntent,0);

        Intent playIntent=new Intent(context,MusicService.class);
        playIntent.setAction(PLAY);
        PendingIntent pplayInt=PendingIntent.getService(context,0,playIntent,0);

        Intent pauseIntent = new Intent(context,MusicService.class);
        pauseIntent.setAction(PAUSE);
        PendingIntent ppauseInt=PendingIntent.getService(context,0,pauseIntent,0);

        Intent nextIntent=new Intent(context, MusicService.class);
        nextIntent.setAction(NEXT);
        PendingIntent pnextIntent=PendingIntent.getService(context,0,nextIntent,0);

        Bitmap bitmap=getBitmap(context,currSong.getAlbumArt());

        builder=new NotificationCompat.Builder(context,MusicService.CHANNEL_ID)
        .setContentTitle(currSong.getTitle()).setTicker(currSong.getTitle())
        .setContentText(currSong.getAlbumName()).setSmallIcon(R.drawable.music_icon).setContentIntent(pendingOAIntent)
        .setOngoing(true).setLargeIcon(bitmap).addAction(android.R.drawable.ic_media_previous,"Previous",pprevInt);
        if (playStatus)
            builder.addAction(android.R.drawable.ic_media_pause,"Play",ppauseInt);
        else
            builder.addAction(android.R.drawable.ic_media_play,"Pause",pplayInt);
        builder.addAction(android.R.drawable.ic_media_next,"Next",pnextIntent).setLargeIcon(bitmap);
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle());
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            builder.setChannelId(MusicService.CHANNEL_ID);

        return builder.build();


    }


    public static Bitmap getBitmap(Context context,String uri){
        Bitmap bitmap=null;
        try {
            bitmap= MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(uri));
        } catch (Exception e){
            bitmap= BitmapFactory.decodeResource(context.getResources(),R.drawable.album_default);
        }
        return bitmap;
    }

}
