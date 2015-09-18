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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.icelandic_courses.elie.myfirstapp.R;
import com.icelandic_courses.elie.myfirstapp.logic.Difficulty;
import com.icelandic_courses.elie.myfirstapp.logic.GameMode;
import com.icelandic_courses.elie.myfirstapp.score.HighScoreManager;

import java.util.ArrayList;
import java.util.List;

public class HighScoresActivity extends Activity {

    private ListView highScoreListView;
    private List<Integer> scoreList;
    private ArrayAdapter<Integer> listAdapter;


    private SharedPreferences preferences;

    private Spinner difficultySpinner;
    private Spinner gameModeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        highScoreListView = (ListView) findViewById(R.id.highScores);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //init array adapter
        scoreList = new ArrayList<>();
        listAdapter = new ArrayAdapter<Integer>(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                scoreList);
        highScoreListView.setAdapter(listAdapter);

        //get views
        difficultySpinner = (Spinner) findViewById(R.id.difficulty);
        gameModeSpinner = (Spinner) findViewById(R.id.gameMode);

        //set current difficulty
        Difficulty difficulty = Difficulty.get(preferences);
        difficultySpinner.setSelection( difficulty == Difficulty.EASY ? 0 : difficulty ==Difficulty.HARD ? 2 : 1);

        //on item select listener
        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                createHighScoreList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        difficultySpinner.setOnItemSelectedListener(onItemSelectedListener);
        gameModeSpinner.setOnItemSelectedListener(onItemSelectedListener);

        //init with values
        createHighScoreList();
    }

    private void createHighScoreList() {
        //get difficulty
        Difficulty difficulty;
        switch ((int) difficultySpinner.getSelectedItemId()) {
            case 0: difficulty = Difficulty.EASY; break;
            case 2: difficulty = Difficulty.HARD; break;
            case 1:default: difficulty = Difficulty.MIDDLE;
        }

        //get game mode
        GameMode gameMode;
        switch ((int) gameModeSpinner.getSelectedItemId()) {
            case 1: gameMode = GameMode.MOVES; break;
            case 0:default: gameMode = GameMode.CLASSIC;
        }

        //update score list
        scoreList.clear();
        List<Integer> scores = HighScoreManager.getList(preferences, gameMode, difficulty);
        scoreList.addAll(scores);
        listAdapter.notifyDataSetChanged();
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

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

    private void resetHighScores(){
        HighScoreManager.resetHighScore(preferences, GameMode.CLASSIC, Difficulty.EASY);
        HighScoreManager.resetHighScore(preferences, GameMode.CLASSIC, Difficulty.MIDDLE);
        HighScoreManager.resetHighScore(preferences, GameMode.CLASSIC, Difficulty.HARD);
        HighScoreManager.resetHighScore(preferences, GameMode.MOVES, Difficulty.EASY);
        HighScoreManager.resetHighScore(preferences, GameMode.MOVES, Difficulty.MIDDLE);
        HighScoreManager.resetHighScore(preferences, GameMode.MOVES, Difficulty.HARD);
        listAdapter.notifyDataSetChanged();
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
