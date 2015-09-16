package com.icelandic_courses.elie.myfirstapp.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.icelandic_courses.elie.myfirstapp.R;
import com.icelandic_courses.elie.myfirstapp.logic.Difficulty;
import com.icelandic_courses.elie.myfirstapp.logic.GameState;
import com.icelandic_courses.elie.myfirstapp.logic.GameStateChangeHandler;
import com.icelandic_courses.elie.myfirstapp.logic.ILogic;
import com.icelandic_courses.elie.myfirstapp.logic.time.RemainingSecondsHandler;
import com.icelandic_courses.elie.myfirstapp.logic.time.TimedLogic;
import com.icelandic_courses.elie.myfirstapp.score.ScoreChangeHandler;
import com.icelandic_courses.elie.myfirstapp.score.ScoreManager;
import com.icelandic_courses.elie.myfirstapp.trace.TraceHandler;

public class ClassicGameActivity extends Activity {

    public final static String TAG = ClassicGameActivity.class.getSimpleName();
    
    private final int SECONDS = 30;
    private int pitchSize;
    private int numberColors;

    private String difficulty;

    private GameView gameView;
    private TextView remainingSecondsView;
    private TextView scoreView;
    private TextView bestScoreView;

    private TraceHandler traceHandler;
    private SharedPreferences prefs;

    private TimedLogic logic;
    private ScoreManager scoreManager;

    final Handler remainingSecondsHandler = new Handler();
    private Vibrator vibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        //settings
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        difficulty = prefs.getString("difficulty", Difficulty.MIDDLE.toString());
        Log.i("Difficulty", " = " + difficulty);
        if (difficulty.equals(Difficulty.EASY.toString())){
            pitchSize = 7;
            numberColors = 3;
        } else if (difficulty.equals(Difficulty.MIDDLE.toString())){
            pitchSize = 6;
            numberColors = 4;
        } else if (difficulty.equals(Difficulty.HARD.toString())){
            pitchSize = 5;
            numberColors = 5;
        }
        //int numberColors = Integer.parseInt(prefs.getString("numberColor", "3"));
        //int pitchSize = Integer.parseInt(prefs.getString("pitchSize", "6"));

        setContentView(R.layout.activity_classic_game);
        gameView = (GameView) findViewById(R.id.gameView);
        remainingSecondsView = (TextView) findViewById(R.id.remainingSeconds);
        scoreView = (TextView) findViewById(R.id.score);
        bestScoreView = (TextView) findViewById(R.id.bestScore);

        remainingSecondsView.setText(getResources().getString(R.string.remainingSeconds, SECONDS));
        scoreView.setText(getResources().getString(R.string.score, 0));

        vibe = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);

        //init logic with basic settings
        logic = new TimedLogic(
                SECONDS,
                pitchSize,
                numberColors
        );

        bestScoreView.setText(getResources().getString(R.string.best_score, prefs.getInt("highscore" + logic.getMode() + difficulty,0)));

        //game state change handler
        logic.registerGameStateChangeHandler(new GameStateChangeHandler() {
            @Override
            public void gameStateChanged(GameState gameState, ILogic logic) {
                //game finished
                if (gameState == GameState.FINISHED) {
                    launchGameFinishedActivity();
                }
            }
        });

        logic.registerRemainingSecondsHandler(new RemainingSecondsHandler() {
            @Override
            public void remainingSecondsChanged(int seconds) {
                // update remaining seconds
                remainingSecondsView.setText(getResources().getString(R.string.remainingSeconds, seconds));
            }
        });

        scoreManager = new ScoreManager(logic, prefs);
        scoreManager.registerScoreChangeHandler(new ScoreChangeHandler() {
            @Override
            public void scoreChanged(int total, int additional) {

                //vibration
                if (prefs.getBoolean("vibration", false)) {
                    vibe.vibrate(100);
                }

                // update score
                scoreView.setText(getResources().getString(R.string.score, total));
            }
        });

        //init the logic in game view, which sets up the animation logic and tracking handler
        gameView.initLogic(logic);

        //start game
        logic.start();

        //redraw
        gameView.invalidate();

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

    private void launchGameFinishedActivity(){
        Intent intent = new Intent(this, GameFinishedActivity.class);
        intent.putExtra("gameType", logic.getMode());
        intent.putExtra("score", scoreManager.getScore());
        intent.putExtra("highScore", prefs.getInt("highscore" + logic.getMode() + difficulty,0));
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        //TODO maybe pause and not finish the logic
        logic.finish();
        this.finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_classic_game);
    }
}
