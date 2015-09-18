package com.icelandic_courses.elie.myfirstapp.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
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
import com.icelandic_courses.elie.myfirstapp.score.HighScoreManager;
import com.icelandic_courses.elie.myfirstapp.score.ScoreChangeHandler;
import com.icelandic_courses.elie.myfirstapp.score.ScoreManager;
import com.icelandic_courses.elie.myfirstapp.trace.Trace;
import com.icelandic_courses.elie.myfirstapp.trace.TraceChangeHandler;
import com.icelandic_courses.elie.myfirstapp.util.Position;

public class ClassicGameActivity extends Activity {

    public final static String TAG = ClassicGameActivity.class.getSimpleName();

    private final int SECONDS = 3;
    private int pitchSize;
    private int numberColors;

    private Difficulty difficulty;

    private GameView gameView;
    private TextView remainingSecondsView;
    private TextView scoreView;
    private TextView bestScoreView;
    private TextView addScoreView;
    private SharedPreferences preferences;

    private TimedLogic logic;
    private ScoreManager scoreManager;
    private Vibrator vibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Block the orientation
        if(getIntent().getIntExtra("orientation", Configuration.ORIENTATION_PORTRAIT) == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        setContentView(R.layout.activity_moves_game);

        //settings
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        difficulty = Difficulty.get(preferences);
        pitchSize = difficulty.getPitchSize();
        numberColors = difficulty.getNumberColors();

        setContentView(R.layout.activity_classic_game);
        gameView = (GameView) findViewById(R.id.gameView);
        remainingSecondsView = (TextView) findViewById(R.id.remainingSeconds);
        scoreView = (TextView) findViewById(R.id.score);
        bestScoreView = (TextView) findViewById(R.id.bestScore);
        addScoreView = (TextView) findViewById(R.id.additionalScore);

        remainingSecondsView.setText(getResources().getString(R.string.remainingSeconds, SECONDS));
        scoreView.setText(getResources().getString(R.string.score, 0));

        vibe = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);

        //init logic with basic settings
        logic = new TimedLogic(
                SECONDS,
                pitchSize,
                numberColors
        );

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

        scoreManager = new ScoreManager(logic, preferences);
        scoreManager.registerScoreChangeHandler(new ScoreChangeHandler() {
            @Override
            public void scoreChanged(int total, int additional) {

                //vibration
                if (preferences.getBoolean("vibration", false)) {
                    vibe.vibrate(100);
                }

                // update score
                scoreView.setText(getResources().getString(R.string.score, total));
            }
        });

        //init the logic in game view, which sets up the animation logic and tracking handler
        gameView.initLogic(logic);

        //trace handler additional score feedback
        gameView.getTraceHandler().registerTraceChangeHandler(new TraceChangeHandler() {
            @Override
            public void onTraceChanged(Trace trace) {
                //show additional score
                int additionalScore = ScoreManager.getAdditionalScore(trace);
                addScoreView.setText(getResources().getString(R.string.add_score, additionalScore));
            }

            @Override
            public void onLastTrackingPointChanged(Position<Float> lastTrackingPoint) {
                //do nothing
            }
        });

        //start game
        logic.start();

        //redraw
        gameView.invalidate();

        //set texts
        remainingSecondsView.setText(getResources().getString(R.string.remainingSeconds, SECONDS));
        scoreView.setText(getResources().getString(R.string.score, 0));
        int highScore = HighScoreManager.getHighScore(preferences, logic.getMode(), difficulty);
        bestScoreView.setText(getResources().getString(R.string.best_score, highScore));
        addScoreView.setText(getResources().getString(R.string.add_score, 0));
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
        intent.putExtra("gameMode", logic.getMode());
        intent.putExtra("score", scoreManager.getScore());
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
