package com.example.activemind.games;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.activemind.R;
import com.example.activemind.databinding.ActivityEndNumberGameBinding;

public class EndSequenceActivity extends AppCompatActivity {

    private Button playAgainBtn, backBtn2;
    ActivityEndNumberGameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEndNumberGameBinding.inflate(getLayoutInflater());
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
                Intent intent = new Intent(EndSequenceActivity.this, NumberMemoryActivity.class);
                intent.putExtra("catId","Number Game");
                EndSequenceActivity.this.startActivity(intent);
            }
        });
    }
}