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
import com.icelandic_courses.elie.myfirstapp.trace.TrackingHandler;
import com.icelandic_courses.elie.myfirstapp.transformation.PixelToPitchConverter;
import com.icelandic_courses.elie.myfirstapp.transformation.PixelToPitchConverterDescription;

public class ClassicGameActivity extends Activity {

    private GameView gameView;
    private TrackingHandler trackingHandler;

    private static final int pitchSize = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_classic_game);
        gameView = (GameView) findViewById(R.id.gameView);
        RelativeLayout relativeLayout = (RelativeLayout) gameView.getParent();


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //settings
        int moves = 5;
        int numberColors = 3;
        int padding = 10;

        int pixelSize;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            pixelSize = (int) ( Math.min(size.x, size.y) - relativeLayout.getPaddingLeft() - relativeLayout.getPaddingRight() );
        } else {
            pixelSize = (int) ( Math.min(size.x, size.y) - relativeLayout.getPaddingBottom() - relativeLayout.getPaddingTop() );
        }

        ILogic logic = new LimitedMovesLogic(
                moves,
                pitchSize,
                numberColors
        );

        PixelToPitchConverterDescription converterDescription = new PixelToPitchConverterDescription(
                pitchSize,
                pixelSize,
                padding
        );

        PixelToPitchConverter converter = new PixelToPitchConverter(converterDescription);

        IAnimationLogic animationLogic = new NoAnimationLogic(logic, converter);

        //game state change handler
        logic.registerGameStateChangeHandler(new GameStateChangeHandler() {
            @Override
            public void gameStateChanged(GameState gameState) {
                Log.i("Game state changed", gameState.toString());
            }
        });

        //tracking handler
        trackingHandler = new TrackingHandler(logic, converter);
        gameView.setTrackingHandler(trackingHandler);

        //start game
        logic.start();

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

    public static int getPitchSize(){
        return pitchSize;
    }
}
