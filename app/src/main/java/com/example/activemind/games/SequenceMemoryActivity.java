package com.example.activemind.games;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.activemind.R;
import com.example.activemind.databinding.ActivitySequenceMemoryBinding;
import com.example.activemind.firebase.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SequenceMemoryActivity extends AppCompatActivity {

    private ConstraintLayout startPageGroup, gameGroup, resultGroup, helpGroup;
    private Button backBtn, startBtn, retryBtn, helpBtn, exitBtn;
    private TextView levelText, livesText, endLevelText;
    private View wrongOverlay, correctOverlay;
    private List<Button> gameBtnList = new ArrayList<>();
    ActivitySequenceMemoryBinding binding;
    private ButtonCountdown buttonCount;
    private StartCountdown startCountdown;
    private OverlayCountdown overlayCountdown;
    private List<Integer> buttonOrder = new ArrayList<>();
    private List<Integer> clickedOrder = new ArrayList<>();
    private int level = 0;
    private int lives = 3;
    private int buttonIdx = 0;
    private boolean isClickable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySequenceMemoryBinding.inflate(getLayoutInflater());
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
        backBtn = binding.backBtn;
        exitBtn = binding.exitBtn;
        startBtn = binding.startBtn;
        retryBtn = binding.retryBtn;
        helpBtn = binding.helpBtn;
        levelText = binding.levelText;
        livesText = binding.livesText;
        endLevelText = binding.endLevelText;
        wrongOverlay = binding.wrongOverlay;
        correctOverlay = binding.correctOverlay;

        startPageGroup.setVisibility(View.VISIBLE);
        gameGroup.setVisibility(View.GONE);
        resultGroup.setVisibility(View.GONE);
        helpGroup.setVisibility(View.GONE);
        setGameButtons();

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


        startCountdown = new StartCountdown(1000L, 100);
    }

    /**
     * Setup sequence buttons
     */
    private void setGameButtons() {
        for (int i=1; i <= 9; i++) {
            gameBtnList.add(findViewById(getResources().getIdentifier("num" + i + "Btn", "id", getPackageName())));
            int finalI = i-1;
            gameBtnList.get(i - 1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isClickable) {
                        clickedOrder.add(finalI);
                        checkInput();
                    }
                }
            });
        }
    }

    /**
     * Start a new game
     */
    private void newGame() {
        startPageGroup.setVisibility(View.GONE);
        gameGroup.setVisibility(View.VISIBLE);
        level = 0;
        lives = 3;
        String livesStr = "Lives: " + lives;
        livesText.setText(livesStr);
        buttonIdx = 0;
        buttonOrder.clear();
        newRound();

    }

    /**
     * Start next round
     */
    private void newRound() {
        level += 1;
        String levelStr = "Level: " + level;
        levelText.setText(levelStr);
        Random r = new Random();
        int num = r.nextInt(9);
        buttonOrder.add(num);
        clickedOrder.clear();
        startCountdown.start();
    }

    /**
     * End the game, goes to game over screen
     */
    private void endGame() {
        gameGroup.setVisibility(View.GONE);
        resultGroup.setVisibility(View.VISIBLE);
        String levelStr = "Level: " + level;
        endLevelText.setText(levelStr);
        FirebaseHelper.updateUserGameData("SequenceMemory", level);
    }

    /**
     * Go back to start screen from game over screen
     */
    private void restartGame() {
        startPageGroup.setVisibility(View.VISIBLE);
        resultGroup.setVisibility(View.GONE);
    }

    /**
     * When user clicks a wrong button
     */
    private void wrongInput() {
        lives -= 1;
        String livesStr = "Lives: " + lives;
        livesText.setText(livesStr);
        //wrongOverlayCountdown.activate();
        overlayCountdown = new OverlayCountdown(100L, 100, wrongOverlay);
        overlayCountdown.activate();
        if (lives >= 1) {
            clickedOrder.clear();
            startCountdown.start();
        } else {
            endGame();
        }

    }

    /**
     * Check user input if correct or wrong
     */
    private void checkInput() {
        int numInputs = clickedOrder.size();
        if (clickedOrder.get(numInputs - 1).equals(buttonOrder.get(numInputs - 1))) {
            if (clickedOrder.equals(buttonOrder)) {
                isClickable = false;
                overlayCountdown = new OverlayCountdown(150L, 150, correctOverlay);
                overlayCountdown.activate();
                newRound();
            }
        } else {
            isClickable = false;
            wrongInput();
        }
        /*if (clickedOrder.size() == buttonOrder.size()) {
            isClickable = false;
            if (clickedOrder.equals(buttonOrder)) {
                newRound();
            }
        }*/
    }

    /**
     * Timer to light up buttons
     */
    public class StartCountdown extends CountDownTimer {
        public StartCountdown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            //some script here
            lightNextButton();
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }
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
     * Timer for each button flash
     */
    public class ButtonCountdown extends CountDownTimer {
        Button gameBtn;
        public ButtonCountdown(long millisInFuture, long countDownInterval, Button btn) {
            super(millisInFuture, countDownInterval);
            gameBtn = btn;
            gameBtn.setForeground(getDrawable(R.drawable.my_button_silent));
        }

        @Override
        public void onFinish() {
            //some script here
            lightNextButton();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (millisUntilFinished <= 200)
                gameBtn.setForeground(getDrawable(R.drawable.my_button_active));
        }
    }

    /**
     * Light up the button
     * @param btn Sequence button to light up
     */
    public void lightButton(Button btn) {
        buttonCount = new ButtonCountdown( 750L, 100, btn);
        buttonCount.start();
    }

    /**
     * Light up next button in sequence order
     */
    public void lightNextButton() {
        if (buttonIdx < buttonOrder.size()){
            lightButton(gameBtnList.get(buttonOrder.get(buttonIdx)));
            buttonIdx += 1;
        } else {
            buttonIdx = 0;
            isClickable = true;
        }
    }
}

