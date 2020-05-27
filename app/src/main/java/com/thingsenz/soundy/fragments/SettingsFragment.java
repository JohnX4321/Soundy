package com.thingsenz.soundy.fragments;

import android.os.Bundle;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.thingsenz.soundy.BuildConfig;
import com.thingsenz.soundy.MySharedPreferences;
import com.thingsenz.soundy.R;
import com.thingsenz.soundy.activities.SettingsActivity;

public class SettingsFragment extends PreferenceFragment {


    public static SettingsFragment newInstance(int position) {
        SettingsFragment f = new SettingsFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        CheckBoxPreference highQualityPref = (CheckBoxPreference) findPreference(getResources().getString(R.string.pref_high_quality_key));
        highQualityPref.setChecked(MySharedPreferences.getPrefHighQuality(getActivity()));
        highQualityPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                MySharedPreferences.setPrefHighQuality(getActivity(), (boolean) newValue);
                return true;
            }
        });


        SwitchPreference wavPref=(SwitchPreference)findPreference("wav_format");
        wavPref.setChecked(MySharedPreferences.getPrefWavFormat(getActivity()));
        wavPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                MySharedPreferences.setPrefHighQuality(getActivity(),(boolean)o);
                return true;
            }
        });

        ListPreference themePref=(ListPreference)findPreference(getString(R.string.theme_key));
        themePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                themePref.setValue(newValue.toString());
                String theme=String.valueOf(themePref.getValue());
                final String themes[]=getResources().getStringArray(R.array.dark_mode_values);
                Log.d("THEMEs",themes.toString()+"selected  "+theme);
                if (themes[0].equalsIgnoreCase(theme))
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                else if (themes[1].equalsIgnoreCase(theme))
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                return true;
            }

        });



        Preference aboutPref = findPreference(getString(R.string.pref_about_key));
        aboutPref.setSummary(getString(R.string.pref_about_desc, BuildConfig.VERSION_NAME));
        aboutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                LicensesFragment licensesFragment = new LicensesFragment();
                licensesFragment.show(((SettingsActivity)getActivity()).getSupportFragmentManager().beginTransaction(), "dialog_licenses");
                return true;
            }
        });
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //super.onCreatePreferences(savedInstanceState, rootKey);
    }




}
