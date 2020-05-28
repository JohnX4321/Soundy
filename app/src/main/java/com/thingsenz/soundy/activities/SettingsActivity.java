package com.thingsenz.soundy.activities;

import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.thingsenz.soundy.R;
import com.thingsenz.soundy.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

  //      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    //    toolbar.setPopupTheme(R.style.ThemeOverlay_AppCompat_Light);
//        setSupportActionBar(toolbar);
        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.action_settings);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }*/

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new SettingsFragment())
                .commit();
    }


}
