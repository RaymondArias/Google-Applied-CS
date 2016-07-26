/*
PAIR PROGRAMMING PROJECT
Raymond Arias & Connor Haskins
7/23/2016
Ghost Text Game
Google Android Applied CS
 */


package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private Random random = new Random();

    // keys for our saved instance bundles
    static final String GAME_STATUS = "gameStatus";
    static final String GHOST_TEXT = "ghostText";
    static final String USER_SCORE = "userScore";
    static final String COMP_SCORE = "compScore";

    // global UI components
    private TextView ghostText;
    private TextView gameStatus;
    private TextView scoreText;
    private Button challengeButton;
    private Button restartButton;

    // scores for extension and scoreText
    private int userScore = 0;
    private int compScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);

        // acquire access to text views
        ghostText = (TextView) findViewById(R.id.ghostText);
        gameStatus = (TextView) findViewById(R.id.gameStatus);
        scoreText = (TextView) findViewById(R.id.scoreText);

        // get challenge button and implement onclicklistener
        challengeButton = (Button)findViewById(R.id.challengeButton);
        challengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the current fragment from ghostText
                String currentFragment = ghostText.getText().toString();
                // if the word is at least minimum length and the word is in the dictionary
                // of if the opponent created a fragment that does not work (will never happen w/ comp)
                if((currentFragment.length() >= GhostDictionary.MIN_WORD_LENGTH && dictionary.isWord(currentFragment)) ||
                        dictionary.getAnyWordStartingWith(currentFragment) == null){
                    // user wins, increase userScore
                    gameStatus.setText("You Win!");
                    userScore++;
                } else {
                    // else the computer wins, increase compScore
                    gameStatus.setText("Computer Wins.");
                    compScore++;
                }
                // update the scoreText textview
                scoreText.setText("Your Score: " + userScore + " Computer Score: " + compScore);
            }
        });

        // access the restart button and add onClickListener
        restartButton = (Button)findViewById(R.id.restartButton);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call onStart to restart the game
                onStart(view);
            }
        });


        // user the asset manager to access words.txt dictionary file
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            // get the singleton SimpleDictionary object
            dictionary = SimpleDictionary.get(inputStream);
        } catch (IOException e) {
            // throw exception if txt file could not load properly
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }

        // check if instance is being recreated and needs to be recovered
        if(savedInstanceState == null) {
            // if not, just call onStart and start a new game
            onStart(null);
        } else {
            // else, load all the member variables from the bundle
            gameStatus.setText(savedInstanceState.getCharSequence(GAME_STATUS));
            ghostText.setText(savedInstanceState.getCharSequence(GHOST_TEXT));
            compScore = savedInstanceState.getInt(COMP_SCORE);
            userScore = savedInstanceState.getInt(USER_SCORE);
            scoreText.setText("Your Score: " + userScore + " Computer Score: " + compScore);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent kEvent){
        // new character to be added
        // set to '0' since it will not appear in a word
        char newChar = '0';

        // if keyCode is between KEYCODE_A and KEYCODE_Z
        if(keyCode > 28 && keyCode < 55){
            // convert to lowercase ascii
            newChar = (char)(keyCode + 68);
        }
        // else if keyCode is between capital A and capital Z
        else if (keyCode > 58 && keyCode < 85){
            // convert to lowercase ascii
            newChar = (char)(keyCode + 38);
        }

        // if keyCode was an upper or lowercase alphabetic character
        if(newChar != '0'){

            String newGhostText = ghostText.getText().toString() + newChar;
            ghostText.setText(newGhostText);
            gameStatus.setText(COMPUTER_TURN);
            computerTurn();
        }
        // call super.onKeyUp and pretty much do nothing
        return super.onKeyUp(keyCode,kEvent);
    }

    // do not touch
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    // do not touch
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

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        // update the scoreText
        scoreText.setText("Your Score: " + userScore + " Computer Score: " + compScore);
        boolean userTurn = random.nextBoolean();
        ghostText.setText("");
        if (userTurn) {
            gameStatus.setText(USER_TURN);
        } else {
            gameStatus.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {
        // get the current fragment string
        String fragmentText = ghostText.getText().toString();
        // get a good word to create with current fragment
        String anyWord = dictionary.getGoodWordStartingWith(fragmentText);
        // if the fragment is not a prefix for any word in the dictionary OR
        // the fragment is already a word in the dictionary
        if(anyWord == null || dictionary.isWord(fragmentText)){
            // comp wins, increase compScore, update scoreText
            gameStatus.setText("Computer Wins.");
            compScore++;
            scoreText.setText("Your Score: " + userScore + " Computer Score: " + compScore);
        } else {
            // else the computer will add a valid character to the ghostText fragment
            ghostText.setText(anyWord.substring(0,fragmentText.length() + 1));
            // Do computer turn stuff then make it the user's turn again
            gameStatus.setText(USER_TURN);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        // save the user's current game state
        savedInstanceState.putCharSequence(GAME_STATUS, gameStatus.getText());
        savedInstanceState.putCharSequence(GHOST_TEXT, ghostText.getText());
        savedInstanceState.putInt(USER_SCORE, userScore);
        savedInstanceState.putInt(COMP_SCORE, compScore);

        super.onSaveInstanceState(savedInstanceState);
    }
}
