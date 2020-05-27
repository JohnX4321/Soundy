package com.thingsenz.soundy.musicplayer.activities;

import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.thingsenz.soundy.R;
import com.thingsenz.soundy.musicplayer.services.MusicService;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_settings);
        ListView settingListView = (ListView)findViewById(R.id.lv_settings);
        final ArrayList<String> settings = new ArrayList<>();
        settings.add("Equalizer");
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,settings);
        settingListView.setAdapter(adapter);
        settingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0) {
                    Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                    intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, "com.thingsenz.soundy");
                    intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, MusicService.SESSION_ID);
                    intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                    if(intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent,0);
                    } else {
                        Toast.makeText(getApplicationContext(),"Sorry ! Cannot find equilizer in your system",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

}