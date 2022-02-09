package com.example.activemind.ui.login;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.activemind.databinding.FragmentLoginBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;
    private FragmentLoginBinding binding;
    private TextInputLayout emailTextLayout, passwordTextLayout;
    private TextView textLogin, textForgotPassword;
    private TextInputEditText emailEdit, passwordEdit;
    private Button loginBtn, registerBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        loginViewModel =
                new ViewModelProvider(this).get(LoginViewModel.class);

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textLogin;
        loginViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        setup();

        loginBtn.setOnClickListener((new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getContext(), "LOG IN", Toast.LENGTH_SHORT).show();
            }
        }));


        return root;
    }

    //set up bindings and stuff
    public void setup() {
        emailTextLayout = binding.emailTextLayout;
        passwordTextLayout = binding.passwordTextLayout;
        textLogin = binding.textLogin;
        textForgotPassword = binding.textForgotPassword;
        emailEdit = binding.emailTextEntry;
        passwordEdit = binding.passwordTextEntry;
        loginBtn = binding.buttonLogin;
        registerBtn = binding.buttonRegister;

        SpannableString fpContent = new SpannableString("Forgot Password?");
        fpContent.setSpan(new UnderlineSpan(), 0, fpContent.length(), 0);
        textForgotPassword.setText(fpContent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}