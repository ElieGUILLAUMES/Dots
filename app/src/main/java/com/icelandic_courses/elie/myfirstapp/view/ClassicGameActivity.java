package com.icelandic_courses.elie.myfirstapp.view;

import android.app.Activity;
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
import com.icelandic_courses.elie.myfirstapp.logic.LimitedMovesLogic;
import com.icelandic_courses.elie.myfirstapp.logic.RemainingMovesHandler;
import com.icelandic_courses.elie.myfirstapp.trace.TrackingHandler;

public class ClassicGameActivity extends Activity {

    private GameView gameView;
    private TextView remainingMovesView;
    private TextView scoreView;
    private TextView bestScoreView;

    private TrackingHandler trackingHandler;
    private SharedPreferences prefs;

    private int remainingMoves = 30;

    private LimitedMovesLogic logic;

    final Handler remainingMovesHandler = new Handler();

    private Vibrator vibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int numberColors = Integer.parseInt(prefs.getString("numberColor", "3"));
        int pitchSize = Integer.parseInt(prefs.getString("pitchSize", "6"));

        setContentView(R.layout.activity_classic_game);
        gameView = (GameView) findViewById(R.id.gameView);
        remainingMovesView = (TextView) findViewById(R.id.remainingMoves);
        scoreView = (TextView) findViewById(R.id.score);
        bestScoreView = (TextView) findViewById(R.id.bestScore);

        remainingMovesView.setText(getResources().getString(R.string.remainingMoves, remainingMoves));
        scoreView.setText(getResources().getString(R.string.score, 0));
        bestScoreView.setText(getResources().getString(R.string.best_score, 0));

        //settings
        //int moves = 30;
        //int numberColors = 3;
        //int pitchSize = 6;

        //init logic with basic settings
        logic = new LimitedMovesLogic(
                remainingMoves,
                pitchSize,
                numberColors
        );

        //game state change handler
        logic.registerGameStateChangeHandler(new GameStateChangeHandler() {
            @Override
            public void gameStateChanged(GameState gameState) {
                Log.i("Game state changed", gameState.toString());
            }
        });

        vibe = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);

        logic.registerRemainingMoveHandler(new RemainingMovesHandler() {
            @Override
            public void remainingMovesChanged(int remainingMoves) {
                changeRemainingMovesView(remainingMoves);
                Log.i("RemainingMoves changed", getString(remainingMoves));
            }
        });

        //init the logic in game view, which sets up the animation logic and tracking handler
        gameView.initLogic(logic);

        //start game
        logic.start();

        //redraw
        gameView.invalidate();

        // check if the number of remainingMoves has changed every 30ms
        final Runnable remainingMovesRunnable = new Runnable() {
            public void run() {
                remainingMovesHandler.postDelayed(this, 30);
                // if a move is done ==> check if vibrate
                if(remainingMoves != logic.getRemainingMoves()){
                    if(prefs.getBoolean("vibration", false)){
                        vibe.vibrate(100);
                    }
                }
                remainingMovesView.setText(getResources().getString(R.string.remainingMoves, logic.getRemainingMoves()));
                remainingMoves = logic.getRemainingMoves();
            }
        };
        remainingMovesHandler.postDelayed(remainingMovesRunnable, 0000);
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

    private void changeRemainingMovesView(int remainingMoves){
        this.remainingMoves = remainingMoves;
    }

}
