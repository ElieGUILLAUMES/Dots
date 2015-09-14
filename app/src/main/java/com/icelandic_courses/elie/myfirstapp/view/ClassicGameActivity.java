package com.icelandic_courses.elie.myfirstapp.view;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toolbar;

import com.icelandic_courses.elie.myfirstapp.R;
import com.icelandic_courses.elie.myfirstapp.animation.IAnimationLogic;
import com.icelandic_courses.elie.myfirstapp.animation.NoAnimationLogic;
import com.icelandic_courses.elie.myfirstapp.logic.GameState;
import com.icelandic_courses.elie.myfirstapp.logic.GameStateChangeHandler;
import com.icelandic_courses.elie.myfirstapp.logic.ILogic;
import com.icelandic_courses.elie.myfirstapp.logic.LimitedMovesLogic;
import com.icelandic_courses.elie.myfirstapp.logic.TimedLogic;
import com.icelandic_courses.elie.myfirstapp.trace.TraceChangeHandler;
import com.icelandic_courses.elie.myfirstapp.trace.TrackingHandler;
import com.icelandic_courses.elie.myfirstapp.transformation.PixelToPitchConverter;
import com.icelandic_courses.elie.myfirstapp.transformation.PixelToPitchConverterDescription;
import com.icelandic_courses.elie.myfirstapp.util.Position;

import java.util.List;

public class ClassicGameActivity extends Activity {

    private GameView gameView;
    private TrackingHandler trackingHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_classic_game);
        gameView = (GameView) findViewById(R.id.gameView);

        //settings
        int moves = 30;
        int numberColors = 3;
        int pitchSize = 6;

        //init logic with basic settings
        ILogic logic = new LimitedMovesLogic(
                moves,
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
}
