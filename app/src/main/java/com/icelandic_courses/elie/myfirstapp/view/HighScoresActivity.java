package com.icelandic_courses.elie.myfirstapp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.icelandic_courses.elie.myfirstapp.R;
import com.icelandic_courses.elie.myfirstapp.logic.Difficulty;
import com.icelandic_courses.elie.myfirstapp.logic.GameMode;
import com.icelandic_courses.elie.myfirstapp.logic.time.TimedLogic;
import com.icelandic_courses.elie.myfirstapp.score.HighScore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HighScoresActivity extends Activity {

    private ListView highScoreListView;
    private ArrayList<HighScore> highScores = new ArrayList<HighScore>();
    private HighScoreAdapter highScoreAdapter;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        highScoreListView = (ListView) findViewById(R.id.highScores);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        createHighScoreList();

        highScoreAdapter = new HighScoreAdapter(this, highScores);
        highScoreListView.setAdapter(highScoreAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_high_scores, menu);
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

    private class HighScoreAdapter extends ArrayAdapter<HighScore> {

        private final Context context;
        private final List<HighScore> values;

        public HighScoreAdapter(Context context, List<HighScore> objects) {
            super(context, -1, objects);
            this.context = context;
            this.values = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.high_scores_row, parent, false);

            TextView gameModeView = (TextView) rowView.findViewById(R.id.gameMode);
            gameModeView.setText(values.get(position).getGameMode());

            TextView gameScoreView = (TextView) rowView.findViewById(R.id.gameScore);
            gameScoreView.setText(String.valueOf(values.get(position).getHighScore()));

            return rowView;
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

    private void resetHighScores(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("highscore" + GameMode.CLASSIC.toString() + Difficulty.EASY.toString(), 0);
        editor.putInt("highscore" + GameMode.CLASSIC.toString() + Difficulty.MIDDLE.toString(), 0);
        editor.putInt("highscore" + GameMode.CLASSIC.toString() + Difficulty.HARD.toString(), 0);
        editor.putInt("highscore" + GameMode.MOVES.toString() + Difficulty.EASY.toString(), 0);
        editor.putInt("highscore" + GameMode.MOVES.toString() + Difficulty.MIDDLE.toString(), 0);
        editor.putInt("highscore" + GameMode.MOVES.toString() + Difficulty.HARD.toString(), 0);
        editor.commit();
        createHighScoreList();
        highScoreAdapter.notifyDataSetChanged();
    }

    private void createHighScoreList(){
        highScores.clear();
        highScores.add(new HighScore(GameMode.CLASSIC.toString() + " " + Difficulty.EASY.toString(), prefs.getInt("highscore" + GameMode.CLASSIC.toString() + Difficulty.EASY.toString(), 0)));
        highScores.add(new HighScore(GameMode.CLASSIC.toString() + " " + Difficulty.MIDDLE.toString(), prefs.getInt("highscore" + GameMode.CLASSIC.toString() + Difficulty.MIDDLE.toString(), 0)));
        highScores.add(new HighScore(GameMode.CLASSIC.toString() + " " + Difficulty.HARD.toString(), prefs.getInt("highscore" + GameMode.CLASSIC.toString() + Difficulty.HARD.toString(), 0)));
        highScores.add(new HighScore(GameMode.MOVES.toString() + " " + Difficulty.EASY.toString(), prefs.getInt("highscore" + GameMode.MOVES.toString() + Difficulty.EASY.toString(), 0)));
        highScores.add(new HighScore(GameMode.MOVES.toString() + " " + Difficulty.MIDDLE.toString(), prefs.getInt("highscore" + GameMode.MOVES.toString() + Difficulty.MIDDLE.toString(), 0)));
        highScores.add(new HighScore(GameMode.MOVES.toString() + " " + Difficulty.HARD.toString(), prefs.getInt("highscore" + GameMode.MOVES.toString() + Difficulty.HARD.toString(), 0)));
    }

    public void alertBeforeDeleteHighScores(View view){
        new AlertDialog.Builder(this)
                .setTitle("HighScores Suppression")
                .setMessage("Are you sure you want to delete all your highscores?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        resetHighScores();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
