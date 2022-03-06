package com.example.activemind;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.activemind.databinding.ActivityEndGameBinding;
import com.example.activemind.databinding.ActivityGameBinding;

public class EndGameActivity extends AppCompatActivity {

    private Button playAgainBtn, backBtn2;
    ActivityEndGameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEndGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        playAgainBtn = findViewById(R.id.playAgainBtn);
        backBtn2 = findViewById(R.id.backBtn2);

        backBtn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });

        playAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(EndGameActivity.this, GameActivity.class);
                intent.putExtra("catId","Number Game");
                EndGameActivity.this.startActivity(intent);
            }
        });
    }
}