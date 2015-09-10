package com.icelandic_courses.elie.myfirstapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

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
import com.icelandic_courses.elie.myfirstapp.util.Position;

public class MyActivity extends Activity {

    public final static String EXTRA_MESSAGE = "com.icelandic_courses.elie.myfirstapp.MESSAGE";

    private TrackingHandler trackingHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //settings
        int seconds = 5;
        int pitchSize = 8;
        int numberColors = 2;
        int pixelSize = 660;
        int padding = 10;

        ILogic logic = new TimedLogic(
                seconds,
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

        //start game
        logic.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //forward touch events
        return trackingHandler.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
