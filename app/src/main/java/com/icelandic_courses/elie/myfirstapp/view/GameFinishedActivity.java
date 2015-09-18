package com.icelandic_courses.elie.myfirstapp.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
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
import com.icelandic_courses.elie.myfirstapp.logic.Difficulty;
import com.icelandic_courses.elie.myfirstapp.logic.GameMode;
import com.icelandic_courses.elie.myfirstapp.score.HighScoreManager;

import java.io.IOException;

public class GameFinishedActivity extends Activity {

    private Intent intent;
    private MediaPlayer mp = new MediaPlayer();
    private SharedPreferences prefs;

    private TextView newHighScoreTitle;
    private TextView newHighScore;

    private TextView scoreTitle;
    private TextView score;
    private TextView highScoreTitle;
    private TextView highScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_finished);

        newHighScoreTitle = (TextView) findViewById(R.id.newHighScoreTitle);
        newHighScore = (TextView) findViewById(R.id.newHighScore);
        scoreTitle = (TextView) findViewById(R.id.scoreTitle);
        score = (TextView) findViewById(R.id.score);
        highScoreTitle = (TextView) findViewById(R.id.highScoreTitle);
        highScore = (TextView) findViewById(R.id.highScore);

        intent = getIntent();

        prefs =  PreferenceManager.getDefaultSharedPreferences(MyActivity.getContext());

        checkNightMode();

        // If it is a new High Score
        Difficulty difficulty = Difficulty.get(prefs);
        GameMode gameMode = (GameMode) intent.getSerializableExtra("gameMode");
        int scoreValue = intent.getIntExtra("score",0);
        int highScoreValue = intent.getIntExtra("highScore", 0);

        if(scoreValue > highScoreValue){
            newHighScore.setText(String.valueOf(scoreValue));
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
            score.setText(String.valueOf(scoreValue));
            highScore.setText(String.valueOf(highScoreValue));
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void replay(View view){
        GameMode gameMode = (GameMode) intent.getSerializableExtra("gameMode");
        if(gameMode == GameMode.CLASSIC){
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
        if(mp.isPlaying()) {
            mp.stop();
        }
        this.finish();
    }

    private void checkNightMode(){
        if(prefs.getBoolean("nightmode", false)){
            this.findViewById(android.R.id.content).setBackgroundColor(Color.BLACK);
            newHighScore.setTextColor(Color.WHITE);
            newHighScoreTitle.setTextColor(Color.WHITE);
            score.setTextColor(Color.WHITE);
            scoreTitle.setTextColor(Color.WHITE);
            highScore.setTextColor(Color.WHITE);
            highScoreTitle.setTextColor(Color.WHITE);
        } else {
            this.findViewById(android.R.id.content).setBackgroundColor(Color.WHITE);

        }
    }
}
