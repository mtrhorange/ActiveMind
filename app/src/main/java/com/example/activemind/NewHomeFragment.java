package com.example.activemind;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.activemind.databinding.FragmentNewHomeBinding;
import com.example.activemind.gameController.CategoryAdapter;
import com.example.activemind.gameController.CategoryModel;

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
        categories.add(new CategoryModel("Number Memory Game","Number Memory Game",R.drawable.brain_training));
        categories.add(new CategoryModel("Sequence Memory Game","Sequence Memory Game",R.drawable.brain_sequence));
        categories.add(new CategoryModel("Word Memory Game","Word Memory Game",R.drawable.brain_word));
        categories.add(new CategoryModel("More Game","More \n Games Coming",R.drawable.brain_more));

        CategoryAdapter adapter = new CategoryAdapter(getContext(), categories);
        binding.categoryList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.categoryList.setAdapter(adapter);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}