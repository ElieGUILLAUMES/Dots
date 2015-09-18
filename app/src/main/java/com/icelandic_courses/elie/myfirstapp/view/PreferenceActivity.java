package com.icelandic_courses.elie.myfirstapp.view;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.widget.CheckBox;

import com.icelandic_courses.elie.myfirstapp.R;

public class PreferenceActivity extends android.preference.PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences prefs;
    private boolean nightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        nightMode = prefs.getBoolean("nightmode",false);
        checkNightMode();
    }

    private void checkNightMode(){
        if(prefs.getBoolean("nightmode", false)){
            this.findViewById(android.R.id.content).setBackgroundColor(Color.BLACK);
            setTheme(R.style.LightText);
        } else {
            this.findViewById(android.R.id.content).setBackgroundColor(Color.WHITE);
            setTheme(R.style.DarkText);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        Preference p = findPreference(key);
        if(prefs.getBoolean("nightmode",false) != nightMode){
            nightMode = prefs.getBoolean("nightmode",false);
            this.onCreate(null);
        }
    }
}
