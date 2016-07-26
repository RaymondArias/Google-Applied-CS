package com.example.raymond.unit2_ui_implementatioin;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class ScarneDiceActivity extends AppCompatActivity {
    private Button rollBtn;
    private Button holdBtn;
    private Button resetBtn;
    private ImageView diceImage;
    private TextView score;

    private Handler computerHandler = new Handler();
    private Runnable computerTurnTask = new Runnable() {
        @Override
        public void run() {
            if(!computersTurn())
                return;
            diceImage.startAnimation(testAnim);
            computerHandler.postDelayed(this, 500);
        }
    };

    private int userScore;
    private int computerScore;
    private int overallUserScore;
    private int overallComputerScore;
    private Random rand;
    private Animation testAnim;
    private final int[] images = {R.drawable.dice1,
            R.drawable.dice2,
            R.drawable.dice3,
            R.drawable.dice4,
            R.drawable.dice5,
            R.drawable.dice6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scarne_dice);
        rollBtn = (Button) findViewById(R.id.roll_btn);
        holdBtn = (Button) findViewById(R.id.hold_btn);
        resetBtn = (Button) findViewById(R.id.reset_btn);
        diceImage = (ImageView) findViewById(R.id.dice_view);

        testAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
        diceImage.setAnimation(testAnim);
        score = (TextView) findViewById(R.id.score_textView);
        rand = new Random();
        rollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int randRoll = rand.nextInt(6);
                Drawable dicePic = getResources().getDrawable(images[randRoll]);
                diceImage.startAnimation(testAnim);
                diceImage.setImageDrawable(dicePic);
                gameLogic(randRoll);

            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userScore = 0;
                computerScore = 0;
                overallComputerScore = 0;
                overallUserScore = 0;
            }
        });
        holdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overallUserScore += userScore;
                overallComputerScore += computerScore;
                userScore = 0;
                computerScore = 0;
                score.setText("User Score: " + overallUserScore
                        + " Computer Score: " + overallComputerScore);
                if(overallUserScore >= 100)
                {

                    score.setText("User Wins!");
                    overallComputerScore = 0;
                    overallUserScore = 0;
                }
                else {
                    computerHandler.postDelayed(computerTurnTask, 500);
                }
            }
        });
    }

    public void gameLogic(int usersRoll) {
        if (usersRoll == 0) {
            userScore = 0;
            computerHandler.postDelayed(computerTurnTask, 500);
        } else {
            usersRoll++;
            userScore += usersRoll;
        }

    }

    public boolean computerGameLogic(int compRoll) {
        if (compRoll == 1) {
            computerScore = 0;
            return false;
        } else {
            computerScore += compRoll;
            return true;
        }

    }

    public boolean computersTurn() {
        holdBtn.setEnabled(false);
        rollBtn.setEnabled(false);
        resetBtn.setEnabled(false);
        if (computerScore >= 20) {
            overallComputerScore += computerScore;
            computerScore = 0;
            score.setText("User Score: " + overallUserScore + " Computer Score: " + overallComputerScore);
            holdBtn.setEnabled(true);
            rollBtn.setEnabled(true);
            resetBtn.setEnabled(true);
            if(overallComputerScore >= 100)
            {
                score.setText("Computer wins!");
                userScore = 0;
                computerScore = 0;
                overallComputerScore = 0;
                overallUserScore = 0;
            }
            return false;
        }
        int randNum = rand.nextInt(6);
        diceImage.startAnimation(testAnim);
        Drawable dicePic = getResources().getDrawable(images[randNum]);
        diceImage.setImageDrawable(dicePic);
        if (!computerGameLogic(randNum + 1)) {

            score.setText("User Score:" + overallUserScore + " Computer Score: " + overallComputerScore);
            holdBtn.setEnabled(true);
            rollBtn.setEnabled(true);
            resetBtn.setEnabled(true);
            return false;
        }
        return true;
    }
}

