package com.example.activemind.games;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.activemind.R;
import com.example.activemind.databinding.ActivitySequenceMemoryBinding;

public class SequenceMemoryActivity extends AppCompatActivity {

    private Button backBtn, retryBtn;
    private Button gameBtn1, gameBtn2, gameBtn3, gameBtn4, gameBtn5, gameBtn6, gameBtn7, gameBtn8, gameBtn9;
    ActivitySequenceMemoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySequenceMemoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        backBtn = findViewById(R.id.backBtn);
        retryBtn = findViewById(R.id.retryBtn);
        gameBtn1 = findViewById(R.id.gameBtn1);
        gameBtn2 = findViewById(R.id.gameBtn2);
        gameBtn3 = findViewById(R.id.gameBtn3);
        gameBtn4 = findViewById(R.id.gameBtn4);
        gameBtn5 = findViewById(R.id.gameBtn5);
        gameBtn6 = findViewById(R.id.gameBtn6);
        gameBtn7 = findViewById(R.id.gameBtn7);
        gameBtn8 = findViewById(R.id.gameBtn8);
        gameBtn9 = findViewById(R.id.gameBtn9);

        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }


}

