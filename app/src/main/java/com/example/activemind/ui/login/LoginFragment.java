package com.example.activemind.ui.login;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;
    private FragmentLoginBinding binding;
    private TextInputLayout emailTextLayout, passwordTextLayout;
    private TextView textLogin, textForgotPassword;
    private TextInputEditText emailEdit, passwordEdit;
    private Button loginBtn, registerBtn;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private enum LoginState { IN, OUT }
    private LoginState loginState;

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

        loginBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFirebaseLogin(emailEdit.getText().toString().trim(), passwordEdit.getText().toString().trim());
            }
        }));


        return root;
    }

    /**
     * Sets up stuff (UI, Firebase, etc.)
     */
    public void setup() {
        //UI
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

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        loginState = firebaseUser != null ? LoginState.IN : LoginState.OUT;



    }

    /**
     * Validates Firebase login attempt
     * @param em Email login ID
     * @param pass Password
     */
    private void validateFirebaseLogin(String em, String pass)
    {
        boolean valid = true;
        //make sure not empty fields
        if (em.isEmpty()) { emailEdit.setError("Empty"); emailEdit.requestFocus(); valid = false;}
        if (pass.isEmpty()) { passwordEdit.setError("Empty"); passwordEdit.requestFocus(); valid = false;}
        //make sure user has verified email as well
        if (valid) {
            firebaseAuth.signInWithEmailAndPassword(em, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //check if user has verified email
                        firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser.isEmailVerified()) {
                            Toast.makeText(getContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();
                            //RefreshUI();
                        }
                        else {
                            //notify using snackbar with option to resend verification email
                            Snackbar.make(getView(), "Error: Email not yet verified", 6000).setAction("Resend Email", new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                    Toast.makeText(getContext(), "Resend email", Toast.LENGTH_SHORT).show();
                                    // TODO: send verification email
                                }
                            }).show();
                            firebaseAuth.signOut();
                        }
                    }
                    else {
                        try {
                            throw task.getException();
                        }
                        catch (FirebaseAuthInvalidUserException e) {
                            emailEdit.setError("Email Not Registered");
                            emailEdit.requestFocus();
                        }
                        catch (FirebaseAuthInvalidCredentialsException e) {
                            if (e.getMessage().equals("The email address is badly formatted.")) {
                                emailEdit.setError("Invalid Email");
                                emailEdit.requestFocus();
                            }
                            else if (e.getMessage().equals("The password is invalid or the user does not have a password.")) {
                                passwordEdit.setError("Password Incorrect");
                                passwordEdit.requestFocus();
                            }
                        }
                        catch (Exception e) {
                            Log.e("Login Error", e.getMessage());
                        }
                        Toast.makeText(getContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                        Log.e("Login exception", task.getException().toString());
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}