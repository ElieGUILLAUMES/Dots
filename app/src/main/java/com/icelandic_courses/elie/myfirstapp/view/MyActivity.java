package com.icelandic_courses.elie.myfirstapp.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.icelandic_courses.elie.myfirstapp.R;
import com.icelandic_courses.elie.myfirstapp.logic.Difficulty;

public class MyActivity extends Activity {

    public final static String EXTRA_MESSAGE = "com.icelandic_courses.elie.myfirstapp.MESSAGE";
    private static Context context;
    private SharedPreferences prefs;
    private ImageView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title = (ImageView) findViewById(R.id.title);
        context = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        checkNightMode();
    }

    @Override
    protected void onResume() {
        checkNightMode();
        super.onResume();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, PreferenceActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void launchClassicGame(View view){
        Intent intent = new Intent(this, ClassicGameActivity.class);
        intent.putExtra("orientation", getResources().getConfiguration().orientation);
        startActivity(intent);
    }

    public void launchMovesGame(View view){
        Intent intent = new Intent(this, MovesGameActivity.class);
        intent.putExtra("orientation", getResources().getConfiguration().orientation);
        startActivity(intent);
    }

    public void highScore(View view){
        Intent intent = new Intent(this, HighScoresActivity.class);
        startActivity(intent);
    }

    public void settings(View view){
        Intent intent = new Intent(this, PreferenceActivity.class);
        startActivity(intent);
    }

    public static Context getContext(){
        return context;
    }

    private void checkNightMode(){
        if(prefs.getBoolean("nightmode", false)){
            this.findViewById(android.R.id.content).setBackgroundColor(Color.BLACK);
            title.setBackgroundColor(Color.BLACK);
            title.setImageResource(R.drawable.title_night_mode);
        } else {
            this.findViewById(android.R.id.content).setBackgroundColor(Color.WHITE);
            ImageView title = (ImageView) findViewById(R.id.title);
            title.setBackgroundColor(Color.WHITE);
            title.setImageResource(R.drawable.title);
        }
    }

}
