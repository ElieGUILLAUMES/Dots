package com.icelandic_courses.elie.myfirstapp.logic;

import android.os.CountDownTimer;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Endos on 03.09.2015.
 */
public class TimedLogic extends AbstractLogic {

    private final CountDownTimer timer;

    private long durationMillis;
    private long remainingMillis;

    private final Collection<RemainingSecondsHandler> remainingSecondsHandlers;

    /**
     * Construct a timed logic with
     * @param duration in seconds
     * @param pitchSize
     */
    public TimedLogic(int duration, int pitchSize, int numberDotColors) {
        super(pitchSize, numberDotColors);

        remainingSecondsHandlers = new ArrayList<RemainingSecondsHandler>();

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

    public void registerRemainingSecondsHandler(RemainingSecondsHandler remainingSecondsHandler) {
        remainingSecondsHandlers.add(remainingSecondsHandler);
    }

    public void unregisterRemainingSecondsHandler(RemainingSecondsHandler remainingSecondsHandler) {
        remainingSecondsHandlers.remove(remainingSecondsHandler);
    }

    public int getRemainingSeconds() {
        return (int) remainingMillis/1000;
    }

    public void setRemainingSecondsLeft(int remainingMillis){
        for(RemainingSecondsHandler remainingSecondsHandler : remainingSecondsHandlers) {
            remainingSecondsHandler.remainingSecondsChanged(remainingMillis/1000);
        }
    }

    public void stop(){
        timer.cancel();
    }
}
