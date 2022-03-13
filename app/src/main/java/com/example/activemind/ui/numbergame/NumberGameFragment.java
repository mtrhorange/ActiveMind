package com.example.activemind.ui.numbergame;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.activemind.R;
import com.example.activemind.databinding.FragmentNumbergameBinding;

import android.os.CountDownTimer;

import java.io.Console;
import java.util.Random;

public class NumberGameFragment extends Fragment {

    private NumberGameViewModel numbergameViewModel;
    private FragmentNumbergameBinding binding;
    private TextView textView0, textView1, textView2, textView3, textView4, titleText;
    private TextView numberText, numberText2, answerText, levelText;
    private EditText answerEdit;
    private Button submitBtn, nextBtn, startBtn;
    private Countdown timerCount;
    private ProgressBar progressBar;
    private int progressTime = 4;
    private int level = 1;
    private String playerAnswer, answerString;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        numbergameViewModel =
                new ViewModelProvider(this).get(NumberGameViewModel.class);

        binding = FragmentNumbergameBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNumbergame;
        numbergameViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        newGame();

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
                if (playerAnswer.equals(answerString))
                    newRound();
                else
                    newGame();
            }
        });

        return root;
    }

    public void setup() {
        titleText = binding.titleText;
        startBtn = binding.startBtn;
        progressBar = binding.countdownbar;
        textView0 = binding.textView0;
        textView3 = binding.textView3;
        textView4 = binding.textView4;
        numberText = binding.numberText;
        numberText2 = binding.numberText2;
        answerText = binding.answerText;
        submitBtn = binding.submitBtn;
        levelText = binding.levelText;
        nextBtn = binding.nextBtn;

        titleText.setVisibility(View.VISIBLE);
        startBtn.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        textView0.setVisibility(View.INVISIBLE);
        textView1.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.INVISIBLE);
        textView3.setVisibility(View.INVISIBLE);
        textView4.setVisibility(View.INVISIBLE);
        answerEdit.setVisibility(View.INVISIBLE);
        numberText.setVisibility(View.INVISIBLE);
        numberText2.setVisibility(View.INVISIBLE);
        answerText.setVisibility(View.INVISIBLE);
        submitBtn.setVisibility(View.INVISIBLE);
        levelText.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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

    public void newGame() {
        level = 0;
        setup();
    }

    public void newRound() {
        titleText.setVisibility(View.INVISIBLE);
        startBtn.setVisibility(View.INVISIBLE);
        textView3.setVisibility(View.INVISIBLE);
        numberText2.setVisibility(View.INVISIBLE);
        textView4.setVisibility(View.INVISIBLE);
        answerText.setVisibility(View.INVISIBLE);
        levelText.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);

        textView0.setVisibility(View.VISIBLE);
        numberText.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
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
        textView0.setVisibility(View.INVISIBLE);
        numberText.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        textView1.setVisibility(View.VISIBLE);
        textView2.setVisibility(View.VISIBLE);
        answerEdit.setVisibility(View.VISIBLE);
        submitBtn.setVisibility(View.VISIBLE);

    }

    public void submitAnswer() {
        playerAnswer = answerEdit.getText().toString();
        answerText.setText(playerAnswer);
        answerEdit.setText("");
        textView1.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.INVISIBLE);
        answerEdit.setVisibility(View.INVISIBLE);
        submitBtn.setVisibility(View.INVISIBLE);

        textView3.setVisibility(View.VISIBLE);
        numberText2.setVisibility(View.VISIBLE);
        textView4.setVisibility(View.VISIBLE);
        answerText.setVisibility(View.VISIBLE);
        levelText.setVisibility(View.VISIBLE);
        nextBtn.setVisibility(View.VISIBLE);

    }

    public void endRound() {
        textView1.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.INVISIBLE);
        answerText.setVisibility(View.INVISIBLE);
        submitBtn.setVisibility(View.INVISIBLE);
    }
}