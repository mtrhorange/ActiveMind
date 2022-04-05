package com.example.activemind.games;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.activemind.R;
import com.example.activemind.databinding.ActivityNumberMemoryBinding;
import com.example.activemind.databinding.ActivityWordMemoryBinding;
import com.example.activemind.firebase.FirebaseHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WordMemoryActivity extends AppCompatActivity {

    private ConstraintLayout startPageGroup, gameGroup, resultGroup, helpGroup;
    private Button backBtn, startBtn, retryBtn, seenWordBtn, newWordBtn, exitBtn, helpBtn;
    private TextView endLevelText, levelText, livesText, gameWordText;
    private View wrongOverlay, correctOverlay;
    private OverlayCountdown overlayCountdown;
    private String curWord = "";
    private List<String> seenWordList, newWordList;
    private String[] wordArr;
    private int level, lives;
    private boolean isSeenWord = false;
    ActivityWordMemoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWordMemoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setup();
    }

    /**
     * Initialize variables and buttons
     */
    private void setup() {
        startPageGroup = binding.StartPageGroup;
        gameGroup = binding.GameGroup;
        resultGroup = binding.ResultGroup;
        helpGroup = binding.HelpGroup;

        startPageGroup.setVisibility(View.VISIBLE);
        gameGroup.setVisibility(View.GONE);
        resultGroup.setVisibility(View.GONE);
        helpGroup.setVisibility(View.GONE);

        startBtn = binding.startBtn;
        retryBtn = binding.retryBtn;
        helpBtn = binding.helpBtn;
        seenWordBtn = binding.seenWordBtn;
        newWordBtn = binding.newWordBtn;
        endLevelText = binding.endLevelText;
        levelText = binding.levelText;
        livesText = binding.livesText;
        gameWordText = binding.gameWordText;
        backBtn = binding.backBtn;
        exitBtn = binding.exitBtn;
        wrongOverlay = binding.wrongOverlay;
        correctOverlay = binding.correctOverlay;

        seenWordList = new ArrayList<>();
        newWordList = new ArrayList<>();

        // Creates word list
        try {
            InputStream inputStream = getAssets().open("Words.txt");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            String string = new String(buffer);
            wordArr = string.split("\r\n");
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startPageGroup.setVisibility(View.VISIBLE);
                helpGroup.setVisibility(View.GONE);
                exitBtn.setVisibility(View.VISIBLE);
            }
        });

        helpBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startPageGroup.setVisibility(View.GONE);
                helpGroup.setVisibility(View.VISIBLE);
                exitBtn.setVisibility(View.GONE);
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                newGame();
            }
        });

        retryBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                restartGame();
            }
        });

        seenWordBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                seenWordBtnClick();
            }
        });

        newWordBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                newWordBtnClick();
            }
        });
    }

    /**
     * Setup and start new game
     */
    private void newGame() {
        startPageGroup.setVisibility(View.GONE);
        gameGroup.setVisibility(View.VISIBLE);

        level = 1;
        setLives(3);
        seenWordList.clear();
        newWordList = new ArrayList<String>(Arrays.asList(wordArr));
        newRound();
    }

    /**
     * Start next round
     */
    private void newRound() {
        String levelStr = "Level: " + level;
        levelText.setText(levelStr);

        Random r = new Random();
        int num = r.nextInt(10);

        if (num <= 6 || seenWordList.size() == 0) {
            isSeenWord = false;
            num = r.nextInt(newWordList.size());
            curWord = newWordList.remove(num);
            seenWordList.add(curWord);
        } else {
            isSeenWord = true;
            num = r.nextInt(seenWordList.size());
            curWord = seenWordList.get(num);
        }

        gameWordText.setText(curWord);
    }

    /**
     * Seen word button click
     */
    private void seenWordBtnClick() {
        if (!isSeenWord) {
            wrongInput();
        } else
            level += 1;
        if (lives > 0)
            newRound();
        else
            endGame();

    }

    /**
     * New word button click
     */
    private void newWordBtnClick() {
        if (isSeenWord) {
            wrongInput();
        } else
            level += 1;
        if (lives > 0)
            newRound();
        else
            endGame();
    }

    /**
     * When user inputs the wrong answer
     */
    private void wrongInput() {
        minusLife();
        overlayCountdown = new OverlayCountdown(100L, 100, wrongOverlay);
        overlayCountdown.activate();
    }

    /**
     * Timer to flash overlay for correct and wrong inputs
     */
    public class OverlayCountdown extends CountDownTimer {
        View overlay;
        public OverlayCountdown(long millisInFuture, long countDownInterval, View ov) {
            super(millisInFuture, countDownInterval);
            overlay = ov;
        }

        public void activate() {
            //some script here
            overlay.setVisibility(View.VISIBLE);
            this.start();
        }

        @Override
        public void onFinish() {
            //some script here
            overlay.setVisibility(View.GONE);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }
    }

    /**
     * Go back to start screen from game over screen
     */
    private void restartGame() {
        startPageGroup.setVisibility(View.VISIBLE);
        resultGroup.setVisibility(View.GONE);
    }

    /**
     * End the game, goes to game over screen
     */
    private void endGame() {
        gameGroup.setVisibility(View.GONE);
        resultGroup.setVisibility(View.VISIBLE);
        String levelStr = "Level: " + level;
        endLevelText.setText(levelStr);
        FirebaseHelper.updateUserGameData("WordMemory", level);
    }

    /**
     * Minus a life
     */
    private void minusLife() {
        setLives(lives-1);
    }

    /**
     * Set current lives
     * @param i Value to set lives
     */
    private void setLives(int i) {
        lives = i;
        String livesStr = "Lives: " + lives;
        livesText.setText(livesStr);
    }
}