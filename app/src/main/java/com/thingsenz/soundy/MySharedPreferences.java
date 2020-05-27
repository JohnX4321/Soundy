package com.thingsenz.soundy;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class MySharedPreferences {

    private static String PREF_HIGH_QUALITY="pref_high_quality";
    private static String PREF_WAV_FORMAT="wav_format";

    public static void setPrefWavFromat(Context context,boolean isEnabled) {
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(PREF_WAV_FORMAT,isEnabled);
        editor.apply();
    }

    public static boolean getPrefWavFormat(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_WAV_FORMAT,false);
    }

    public static void setPrefHighQuality(Context context,boolean isEnabled) {

        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(PREF_HIGH_QUALITY,isEnabled);
        editor.apply();

    }


    public static boolean getPrefHighQuality(Context context) {
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PREF_HIGH_QUALITY,false);
    }

}
