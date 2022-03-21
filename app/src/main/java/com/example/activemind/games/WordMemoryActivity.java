package com.example.activemind.games;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
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

    private ConstraintLayout startPageGroup, gameGroup, resultGroup;
    private Button backBtn, startBtn, retryBtn, seenWordBtn, newWordBtn;
    private TextView endLevelText, levelText, livesText, gameWordText;
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

    private void setup() {
        startPageGroup = binding.StartPageGroup;
        gameGroup = binding.GameGroup;
        resultGroup = binding.ResultGroup;

        startPageGroup.setVisibility(View.VISIBLE);
        gameGroup.setVisibility(View.GONE);
        resultGroup.setVisibility(View.GONE);

        startBtn = binding.startBtn;
        retryBtn = binding.retryBtn;
        seenWordBtn = binding.seenWordBtn;
        newWordBtn = binding.newWordBtn;
        endLevelText = binding.endLevelText;
        levelText = binding.levelText;
        livesText = binding.livesText;
        gameWordText = binding.gameWordText;
        backBtn = binding.backBtn;

        seenWordList = new ArrayList<>();
        newWordList = new ArrayList<>();

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

    private void newGame() {
        startPageGroup.setVisibility(View.GONE);
        gameGroup.setVisibility(View.VISIBLE);

        level = 0;
        lives = 3;
        seenWordList.clear();
        newWordList = new ArrayList<String>(Arrays.asList(wordArr));
        newRound();
    }

    private void newRound() {
        level += 1;
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

    private void seenWordBtnClick() {
        if (!isSeenWord) {
            minusLife();
        }
        if (lives > 0)
            newRound();
        else
            endGame();

    }

    private void newWordBtnClick() {
        if (isSeenWord) {
            minusLife();
        }
        if (lives > 0)
            newRound();
        else
            endGame();
    }

    private void restartGame() {
        startPageGroup.setVisibility(View.VISIBLE);
        resultGroup.setVisibility(View.GONE);
    }

    private void endGame() {
        gameGroup.setVisibility(View.GONE);
        resultGroup.setVisibility(View.VISIBLE);
        String levelStr = "Level: " + level;
        endLevelText.setText(levelStr);
        FirebaseHelper.updateUserGameData("WordMemory", level);
    }

    private void minusLife() {
        lives -= 1;
        String livesStr = "Lives: " + lives;
        livesText.setText(livesStr);
    }
}