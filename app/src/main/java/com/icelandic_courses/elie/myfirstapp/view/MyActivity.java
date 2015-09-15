package com.icelandic_courses.elie.myfirstapp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.icelandic_courses.elie.myfirstapp.R;

public class MyActivity extends Activity {


    public final static String EXTRA_MESSAGE = "com.icelandic_courses.elie.myfirstapp.MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void launchClassicGame(View view){
        Intent intent = new Intent(this, ClassicGameActivity.class);
        startActivity(intent);
    }

    public void launchMovesGame(View view){
        Intent intent = new Intent(this, MovesGameActivity.class);
        startActivity(intent);
    }

    public void highScore(View view){

    }

    public void settings(View view){
        Intent intent = new Intent(this, PreferenceActivity.class);
        startActivity(intent);
    }
}
