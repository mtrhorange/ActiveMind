package com.example.activemind.games;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.activemind.R;
import com.example.activemind.databinding.ActivityNumberMemoryBinding;
import com.example.activemind.firebase.FirebaseHelper;

public class NumberMemoryActivity extends AppCompatActivity {

    private Button backBtn;
    ActivityNumberMemoryBinding binding;
    private ConstraintLayout startPageGroup, showNumberGroup, queryGroup, resultGroup, helpGroup;
    private TextView numberText, numberText2, answerText, answerInputText, levelText;
    private ImageView gameOverImage;
    private Button submitBtn, nextBtn, startBtn, exitBtn, helpBtn, backspaceBtn;
    private List<Button> numberBtns;
    private Countdown timerCount;
    private ProgressBar progressBar;
    private int progressTime = 4;
    private int level = 1;
    private String playerAnswer, answerString;
    private boolean isGameEnded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNumberMemoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setup();
        newGame();
    }

    public void setup() {
        startPageGroup = binding.StartPageGroup;
        showNumberGroup = binding.ShowNumberGroup;
        queryGroup = binding.QueryGroup;
        resultGroup = binding.ResultGroup;
        helpGroup = binding.HelpGroup;

        backBtn = binding.backBtn;
        startBtn = binding.startBtn;
        progressBar = binding.countdownbar;
        numberText = binding.numberText;
        numberText2 = binding.numberText2;
        answerText = binding.answerTextView;
        submitBtn = binding.submitBtn;
        levelText = binding.levelText;
        nextBtn = binding.nextBtn;
        exitBtn = binding.exitBtn;
        helpBtn = binding.helpBtn;
        backspaceBtn = binding.backspaceBtn;
        answerInputText = binding.answerInputText;
        gameOverImage = binding.gameOverImage;

        startPageGroup.setVisibility(View.VISIBLE);
        showNumberGroup.setVisibility(View.GONE);
        queryGroup.setVisibility(View.GONE);
        resultGroup.setVisibility(View.GONE);
        helpGroup.setVisibility(View.GONE);

        setAnswerButtons();

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
                newRound();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                submitAnswer();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!isGameEnded)
                    newRound();
                else
                    newGame();
            }
        });

        backspaceBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String str = answerInputText.getText().toString();
                if (str.length() > 0) {
                    String newStr = str.substring(0, str.length() - 1);
                    answerInputText.setText(newStr);
                }
            }
        });

    }

    private void setAnswerButtons() {
        numberBtns = new ArrayList<>();
        for (int i=1; i <= 9; i++) {
            numberBtns.add(findViewById(getResources().getIdentifier("num" + i + "Btn", "id", getPackageName())));
            int finalI = i;
            numberBtns.get(i - 1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String str = answerInputText.getText() + Integer.toString(finalI);
                    answerInputText.setText(str);
                }
            });
        }

    }

    public void newGame() {
        level = 0;
        isGameEnded = false;
        gameOverImage.setImageDrawable(getDrawable(R.drawable.good));
        displayStartPage();
    }

    public class Countdown extends CountDownTimer {
        public Countdown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            //some script here
            answerRound();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (millisUntilFinished / progressTime / 10);
            progressBar.setProgress(progress, true);
            //Log.i("NumberGame", Integer.toString(progress));
        }
    }

    public void newRound() {
        displayShowNumberPage();

        level += 1;
        String lvlTxt = "Level " + Integer.toString(level);
        levelText.setText(lvlTxt);

        Random r = new Random();
        answerString = "";
        int num;
        for (int i=0; i<level; i++) {
            num = r.nextInt(9)+1;
            answerString += Integer.toString(num);
        }
        numberText.setText(answerString);
        numberText2.setText(answerString);
        timerCount = new Countdown(progressTime * 1000L, 100);
        timerCount.start();
    }

    public void answerRound() {
        answerInputText.setText("");
        displayQueryPage();
    }

    public void submitAnswer() {
        playerAnswer = answerInputText.getText().toString();
        answerText.setText(playerAnswer);

        if (playerAnswer.equals(answerString))
            nextBtn.setText(R.string.text_next);
        else {
            endGame();
        }
        displayResultPage();
    }

    public void endGame() {
        nextBtn.setText(R.string.text_retry);
        gameOverImage.setImageDrawable(getDrawable(R.drawable.gameover));
        isGameEnded = true;
        FirebaseHelper.updateUserGameData("NumberMemory", level);
    }

    public void displayStartPage() {
        startPageGroup.setVisibility(View.VISIBLE);
        showNumberGroup.setVisibility(View.GONE);
        queryGroup.setVisibility(View.GONE);
        resultGroup.setVisibility(View.GONE);
    }

    public void displayShowNumberPage() {
        startPageGroup.setVisibility(View.GONE);
        showNumberGroup.setVisibility(View.VISIBLE);
        queryGroup.setVisibility(View.GONE);
        resultGroup.setVisibility(View.GONE);
    }

    public void displayQueryPage() {
        startPageGroup.setVisibility(View.GONE);
        showNumberGroup.setVisibility(View.GONE);
        queryGroup.setVisibility(View.VISIBLE);
        resultGroup.setVisibility(View.GONE);
    }

    public void displayResultPage() {
        startPageGroup.setVisibility(View.GONE);
        showNumberGroup.setVisibility(View.GONE);
        queryGroup.setVisibility(View.GONE);
        resultGroup.setVisibility(View.VISIBLE);
    }
}