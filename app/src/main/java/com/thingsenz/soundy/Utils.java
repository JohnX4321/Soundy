package com.thingsenz.soundy;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.MediaMetadataRetriever;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.Arrays;

public class Utils {

   /* public void addShortcut(Context context) {

        Intent shortcutIntent=new Intent(context,MusicActivity.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);
        Intent addIntent=new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"Musicam");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,Intent.ShortcutIconResource.fromContext(context,R.drawable.aar_ic_pause));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra("duplicate",false);
        context.sendBroadcast(addIntent);;

    }


    @RequiresApi(25)
    public void addShortcutV8(Context context){
        ShortcutManager shortcutManager=context.getSystemService(ShortcutManager.class);

        ShortcutInfo shortcutInfo=new ShortcutInfo.Builder(context,"musicam")
                .setShortLabel("Musicam").setLongLabel("Music Player")
                .setIcon(Icon.createWithResource(context,R.drawable.aar_ic_pause))
                .setIntent(new Intent(context,MusicActivity.class)).build();
        shortcutManager.setDynamicShortcuts(Arrays.asList(shortcutInfo));
    }*/

   public Bitmap fetchCoverArt(File file){
       MediaMetadataRetriever mmr=new MediaMetadataRetriever();
       byte[] data=mmr.getEmbeddedPicture();
       return BitmapFactory.decodeByteArray(data,0,data.length);
   }

}
