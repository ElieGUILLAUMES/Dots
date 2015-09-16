package com.icelandic_courses.elie.myfirstapp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icelandic_courses.elie.myfirstapp.R;
import com.icelandic_courses.elie.myfirstapp.logic.GameMode;

public class GameFinishedActivity extends Activity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_finished);

        intent = getIntent();

        // If it is a new High Score
        if(intent.getIntExtra("score",0) > intent.getIntExtra("highScore",0)){
            TextView highScoreView = (TextView) findViewById(R.id.newHighScore);
            highScoreView.setText(String.valueOf(intent.getIntExtra("score", 0)));
            LinearLayout newHighScoreLayout = (LinearLayout) findViewById(R.id.newHighScoreLayout);
            newHighScoreLayout.setVisibility(View.VISIBLE);
            LinearLayout scoreLayout = (LinearLayout) findViewById(R.id.scoreLayout);
            scoreLayout.setVisibility(View.GONE);
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
        this.finish();
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
