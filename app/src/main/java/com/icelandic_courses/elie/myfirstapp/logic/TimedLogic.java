package com.icelandic_courses.elie.myfirstapp.logic;

import android.os.CountDownTimer;
import android.util.Log;

/**
 * Created by Endos on 03.09.2015.
 */
public class TimedLogic extends AbstractLogic {

    private final CountDownTimer timer;

    private long durationMillis;
    private long remainingMillis;

    /**
     * Construct a timed logic with
     * @param duration in seconds
     * @param pitchSize
     */
    public TimedLogic(int duration, int pitchSize, int numberDotColors) {
        super(pitchSize, numberDotColors);

        durationMillis = duration * 1000;
        remainingMillis = durationMillis;
        timer = new CountDownTimer(remainingMillis, 1000) {

            public void onTick(long millisUntilFinished) {
                remainingMillis = millisUntilFinished;
                Log.i("Remaining time", remainingMillis + " milliseconds");
                //TODO notify GUI
            }

            public void onFinish() {
                remainingMillis = 0;
                Log.i("Finished!", " Remaining time: " + remainingMillis + " milliseconds");
                finish();
            }
        };
    }

    @Override
    public void start() {
        super.start();

        timer.start();
    }
}
