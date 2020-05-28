package com.thingsenz.soundy.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.thingsenz.soundy.R;
import com.thingsenz.soundy.fragments.FileViewerFragment;
import com.thingsenz.soundy.fragments.RecordFragment;
import com.thingsenz.soundy.fragments.SettingsFragment;
import com.thingsenz.soundy.musicplayer.activities.PlayerActivity;
import com.thingsenz.soundy.ui.PagerSlidingTabStrip;
import com.thingsenz.soundy.videostreaming.VSActivity;

public class MainActivity extends AppCompatActivity {





    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        /*if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_NO)
            setTheme(R.style.LightTheme);
        else
            setTheme(R.style.DarkTheme);*/
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode());

       // android.support.v7.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setPopupTheme(R.style.ThemeOverlay_AppCompat_Light);
       /* if (getSupportActionBar() != null) {
           setSupportActionBar(toolbar);
           getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
           getSupportActionBar().setTitle("Soundy");
        }*/
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null) {

            actionBar.setBackgroundDrawable(new ColorDrawable(this.getApplicationContext().getColor(R.color.primary)));
            actionBar.setHideOffset(0);
            actionBar.setElevation(0);

        }
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.primary_dark));


        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[],int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this,"Permission denied. App Cannot function",Toast.LENGTH_SHORT).show();
                    finish();
                }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.action_music_player:
                startActivity(new Intent(this, PlayerActivity.class));
                return true;
            case R.id.action_video_streamer:
                startActivity(new Intent(this, VSActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class MyAdapter extends FragmentPagerAdapter {
        private String[] titles = { getString(R.string.tab_title_record),
                getString(R.string.tab_title_saved_recordings) };

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:{
                    return RecordFragment.newInstance(position);
                }
                case 1:{
                    return FileViewerFragment.newInstance(position);
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    public MainActivity() {
    }
}
