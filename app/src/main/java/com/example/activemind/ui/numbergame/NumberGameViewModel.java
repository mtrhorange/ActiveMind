package com.example.activemind.ui.numbergame;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NumberGameViewModel extends ViewModel{

    private MutableLiveData<String> mText;

    public NumberGameViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is number game fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}