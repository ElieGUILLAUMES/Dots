package com.icelandic_courses.elie.myfirstapp.view;

import android.os.Bundle;

import com.icelandic_courses.elie.myfirstapp.R;

public class PreferenceActivity extends android.preference.PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
