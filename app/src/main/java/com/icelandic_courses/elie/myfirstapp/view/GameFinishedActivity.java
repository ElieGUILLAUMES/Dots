package com.icelandic_courses.elie.myfirstapp.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icelandic_courses.elie.myfirstapp.R;
import com.icelandic_courses.elie.myfirstapp.logic.GameMode;

import java.io.IOException;

public class GameFinishedActivity extends Activity {

    private Intent intent;
    private MediaPlayer mp = new MediaPlayer();
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_finished);

        intent = getIntent();

        prefs =  PreferenceManager.getDefaultSharedPreferences(MyActivity.getContext());

        // If it is a new High Score
        if(intent.getIntExtra("score",0) > intent.getIntExtra("highScore",0)){
            TextView highScoreView = (TextView) findViewById(R.id.newHighScore);
            highScoreView.setText(String.valueOf(intent.getIntExtra("score", 0)));
            LinearLayout newHighScoreLayout = (LinearLayout) findViewById(R.id.newHighScoreLayout);
            newHighScoreLayout.setVisibility(View.VISIBLE);
            LinearLayout scoreLayout = (LinearLayout) findViewById(R.id.scoreLayout);
            scoreLayout.setVisibility(View.GONE);

            if(!prefs.getBoolean("silence", false)){
                try {
                    mp.reset();
                    AssetFileDescriptor afd;
                    afd = getAssets().openFd("applause.mp3");
                    mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                    mp.prepare();
                    mp.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            TextView scoreView = (TextView) findViewById(R.id.score);
            scoreView.setText(String.valueOf(intent.getIntExtra("score", 0)));
            TextView highScoreView = (TextView) findViewById(R.id.highScore);
            highScoreView.setText(String.valueOf(intent.getIntExtra("highScore", 0)));
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_finished, menu);
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

    public void replay(View view){
        if(intent.getStringExtra("gameType").equals(GameMode.CLASSIC.toString())){
            Intent intent = new Intent(this, ClassicGameActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MovesGameActivity.class);
            startActivity(intent);
        }
        this.finish();
    }

    public void menu(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if(mp.isPlaying())
        {
            mp.stop();
        }
        this.finish();
    }
}
