package com.example.activemind;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.activemind.R;
import com.example.activemind.databinding.FragmentHomeBinding;
import com.example.activemind.databinding.FragmentNewHomeBinding;

import java.util.ArrayList;

public class NewHomeFragment extends Fragment {

    public NewHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    FragmentNewHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentNewHomeBinding.inflate(inflater, container, false);

        ArrayList<CategoryModel> categories = new ArrayList<>();
        categories.add(new CategoryModel("Number Game","Memory Game",""));
        categories.add(new CategoryModel("More Game","More Games Coming",""));

        CategoryAdapter adapter = new CategoryAdapter(getContext(), categories);
        binding.categoryList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.categoryList.setAdapter(adapter);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}