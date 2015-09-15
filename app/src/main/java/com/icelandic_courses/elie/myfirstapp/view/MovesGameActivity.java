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
import com.icelandic_courses.elie.myfirstapp.logic.ILogic;
import com.icelandic_courses.elie.myfirstapp.logic.moves.LimitedMovesLogic;
import com.icelandic_courses.elie.myfirstapp.logic.moves.RemainingMovesHandler;
import com.icelandic_courses.elie.myfirstapp.score.ScoreChangeHandler;
import com.icelandic_courses.elie.myfirstapp.score.ScoreManager;
import com.icelandic_courses.elie.myfirstapp.trace.TraceHandler;

public class MovesGameActivity extends Activity {

    private GameView gameView;
    private TextView remainingMovesView;
    private TextView scoreView;
    private TextView bestScoreView;

    private SharedPreferences prefs;

    private LimitedMovesLogic logic;
    private ScoreManager scoreManager;

    private Vibrator vibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moves_game);

        //settings
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int totalMoves = 30;
        int numberColors = Integer.parseInt(prefs.getString("numberColor", "3"));
        int pitchSize = Integer.parseInt(prefs.getString("pitchSize", "6"));

        //get views
        gameView = (GameView) findViewById(R.id.gameView);
        remainingMovesView = (TextView) findViewById(R.id.remainingMoves);
        scoreView = (TextView) findViewById(R.id.score);
        bestScoreView = (TextView) findViewById(R.id.bestScore);

        //init logic with basic settings
        logic = new LimitedMovesLogic(
                totalMoves,
                pitchSize,
                numberColors
        );

        //game state change handler
        logic.registerGameStateChangeHandler(new GameStateChangeHandler() {
            @Override
            public void gameStateChanged(GameState gameState, ILogic logic) {
                if(gameState == GameState.FINISHED){
                    launchGameFinishedActivity();
                }
            }
        });

        vibe = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);

        logic.registerRemainingMoveHandler(new RemainingMovesHandler() {
            @Override
            public void remainingMovesChanged(int remainingMoves) {
                remainingMovesView.setText(getResources().getString(R.string.remainingMoves, remainingMoves));
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

        //set texts
        remainingMovesView.setText(getResources().getString(R.string.remainingMoves, totalMoves));
        scoreView.setText(getResources().getString(R.string.score, 0));
        bestScoreView.setText(getResources().getString(R.string.best_score, prefs.getInt("highscore"+logic.getMode(), 0)));
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
        intent.putExtra("highScore", prefs.getInt("highscore"+logic.getMode(),0));
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        logic.finish();
        this.finish();
    }

}
