package com.icelandic_courses.elie.myfirstapp.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.icelandic_courses.elie.myfirstapp.R;
import com.icelandic_courses.elie.myfirstapp.logic.GameState;
import com.icelandic_courses.elie.myfirstapp.logic.GameStateChangeHandler;
import com.icelandic_courses.elie.myfirstapp.logic.RemainingSecondsHandler;
import com.icelandic_courses.elie.myfirstapp.logic.TimedLogic;
import com.icelandic_courses.elie.myfirstapp.score.ScoreChangeHandler;
import com.icelandic_courses.elie.myfirstapp.score.ScoreManager;
import com.icelandic_courses.elie.myfirstapp.trace.TrackingHandler;

public class ClassicGameActivity extends Activity {

    private GameView gameView;
    private TextView remainingSecondsView;
    private TextView scoreView;
    private TextView bestScoreView;

    private TrackingHandler trackingHandler;
    private SharedPreferences prefs;

    private int remainingSeconds = 30;
    private int score = 0;

    private TimedLogic logic;
    private ScoreManager scoreManager;

    final Handler remainingSecondsHandler = new Handler();
    private Vibrator vibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int numberColors = Integer.parseInt(prefs.getString("numberColor", "3"));
        int pitchSize = Integer.parseInt(prefs.getString("pitchSize", "6"));

        setContentView(R.layout.activity_classic_game);
        gameView = (GameView) findViewById(R.id.gameView);
        remainingSecondsView = (TextView) findViewById(R.id.remainingSeconds);
        scoreView = (TextView) findViewById(R.id.score);
        bestScoreView = (TextView) findViewById(R.id.bestScore);

        remainingSecondsView.setText(getResources().getString(R.string.remainingSeconds, remainingSeconds));
        scoreView.setText(getResources().getString(R.string.score, 0));
        bestScoreView.setText(getResources().getString(R.string.best_score, prefs.getInt("bestScoreClassicMode",0)));

        //settings
        //int moves = 30;
        //int numberColors = 3;
        //int pitchSize = 6;

        //init logic with basic settings
        logic = new TimedLogic(
                remainingSeconds,
                pitchSize,
                numberColors
        );

        //game state change handler
        logic.registerGameStateChangeHandler(new GameStateChangeHandler() {
            @Override
            public void gameStateChanged(GameState gameState) {
                Log.i("Game state changed", gameState.toString());
                if(gameState.toString().equals(GameState.FINISHED.toString())){
                    launchGameFinishedActivity();
                }
            }
        });

        vibe = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);

        logic.registerRemainingSecondsHandler(new RemainingSecondsHandler() {
            @Override
            public void remainingSecondsChanged(int seconds) {
                changeRemainingSecondsView(seconds);
                Log.i("RemainingSeconds :", getString(seconds));
            }
        });

        scoreManager = logic.getScoreManager();
        scoreManager.registerScoreChangeHandler(new ScoreChangeHandler() {
            @Override
            public void scoreChanged(int total, int additional) {
                changeScoreView(total);
                Log.i("Score changed", String.valueOf(total));
            }
        });

        //init the logic in game view, which sets up the animation logic and tracking handler
        gameView.initLogic(logic);

        //start game
        logic.start();

        //redraw
        gameView.invalidate();

        // initialization of the best score
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.contains("bestScoreClassicMode")){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("bestScoreClassicMode", 0);
            editor.commit();
        }

        // check if the number of remainingMoves has changed every 30ms
        final Runnable remainingSecondsRunnable = new Runnable() {
            public void run() {
                remainingSecondsHandler.postDelayed(this, 30);
                // if a move is done ==> check if vibrate
                if(remainingSeconds != logic.getRemainingSeconds()){
                    // update remaining moves
                    remainingSecondsView.setText(getResources().getString(R.string.remainingSeconds, logic.getRemainingSeconds()));
                    remainingSeconds = logic.getRemainingSeconds();
                }

                if(score != scoreManager.getTotalScore()){
                    if(prefs.getBoolean("vibration", false)){
                        vibe.vibrate(100);
                    }
                }

                // update score
                scoreView.setText(getResources().getString(R.string.score, score));
                if(remainingSeconds == 0){
                    if(prefs.getInt("bestScoreClassicMode", 0) < score){
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("bestScoreClassicMode", scoreManager.getTotalScore());
                        editor.commit();
                    };
                }
            }
        };
        remainingSecondsHandler.postDelayed(remainingSecondsRunnable, 0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_classic_game, menu);
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

    private void changeRemainingSecondsView(int seconds){
        remainingSeconds = seconds;
    }

    private void changeScoreView(int score){
        this.score = score;
    }

    private void launchGameFinishedActivity(){
        Intent intent = new Intent(this, GameFinishedActivity.class);
        intent.putExtra("gameType", "Classic");
        intent.putExtra("score", score);
        intent.putExtra("highScore", prefs.getInt("bestScoreClassicMode",0));
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        logic.stop();
        this.finish();
    }

}
