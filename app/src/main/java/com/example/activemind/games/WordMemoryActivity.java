package com.example.activemind.games;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.activemind.R;
import com.example.activemind.databinding.ActivityNumberMemoryBinding;
import com.example.activemind.databinding.ActivityWordMemoryBinding;

public class WordMemoryActivity extends AppCompatActivity {

    private Button backBtn;
    ActivityWordMemoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWordMemoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }
}