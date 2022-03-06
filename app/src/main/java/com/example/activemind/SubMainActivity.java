package com.example.activemind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;

import com.example.activemind.databinding.ActivityMainBinding;
import com.example.activemind.databinding.ActivitySubMainBinding;

import java.util.ArrayList;

public class SubMainActivity extends AppCompatActivity {

    ActivitySubMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<CategoryModel> categories = new ArrayList<>();
        categories.add(new CategoryModel("","Memory Game",""));
        categories.add(new CategoryModel("","More Games Coming",""));

        CategoryAdapter adapter = new CategoryAdapter(this, categories);
        binding.categoryList.setLayoutManager(new GridLayoutManager(this, 2));
        binding.categoryList.setAdapter(adapter);


    }
}