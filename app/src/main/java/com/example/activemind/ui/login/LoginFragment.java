package com.example.activemind.ui.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.activemind.R;
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
import com.google.firebase.auth.UserInfo;

public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;
    private FragmentLoginBinding binding;

    private ConstraintLayout loginGroup, loggedInGroup, ForgotPassGroup;
    //Login screen UI elements
    private TextInputLayout emailTextLayout, passwordTextLayout;
    private TextView textLogin, textForgotPassword;
    private TextInputEditText emailEdit, passwordEdit;
    private Button loginBtn, registerBtn;

    //Logged in screen UI elements
    private TextView textLoggedIn;
    private Button logoutBtn;

    //Forgot Password screen UI elements
    private EditText forgotPassEmailEdit;
    private Button sendForgotBtn;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    //States
    private enum LoginState { IN, OUT }
    private LoginState loginState;
    private enum ViewState { LOGINOUT, REGISTER, FORGOT }
    private ViewState viewState;

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

        //setup
        setup();

        // create callback to override back button behaviour
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (viewState == ViewState.FORGOT) {
                    Navigation.findNavController(getView()).navigate(R.id.action_nav_login_self);
                } else {
                    setEnabled(false);
                    requireActivity().onBackPressed();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        //button events
        loginBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFirebaseLogin(emailEdit.getText().toString().trim(), passwordEdit.getText().toString().trim());
            }
        }));

        logoutBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseLogout();
            }
        }));

        textForgotPassword.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewState = ViewState.FORGOT;
                refreshUI();
            }
        }));

        forgotPassEmailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Patterns.EMAIL_ADDRESS.matcher(forgotPassEmailEdit.getText().toString().trim()).matches()) {
                    sendForgotBtn.setEnabled(true);
                }
                else {
                    sendForgotBtn.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        return root;
    }

    /**
     * Sets up stuff (UI, Firebase, etc.)
     */
    public void setup() {
        //UI
        loginGroup = binding.LoginGroup;
        emailTextLayout = binding.emailTextLayout;
        passwordTextLayout = binding.passwordTextLayout;
        textLogin = binding.textLogin;
        textForgotPassword = binding.textForgotPassword;
        emailEdit = binding.emailTextEntry;
        passwordEdit = binding.passwordTextEntry;
        loginBtn = binding.buttonLogin;
        registerBtn = binding.buttonRegister;

        loggedInGroup = binding.LoggedInGroup;
        textLoggedIn = binding.textLoggedIn;
        logoutBtn = binding.buttonLogout;

        ForgotPassGroup = binding.ForgotPasswordGroup;
        forgotPassEmailEdit = binding.forgotEmailTextEntry;
        sendForgotBtn = binding.buttonSendForgot;

        SpannableString fpContent = new SpannableString("Forgot Password?");
        fpContent.setSpan(new UnderlineSpan(), 0, fpContent.length(), 0);
        textForgotPassword.setText(fpContent);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //set states
        loginState = firebaseUser != null ? LoginState.IN : LoginState.OUT;
        viewState = ViewState.LOGINOUT;

        refreshUI();
    }

    /**
     * sets the UI elements according to viewState & loginState
     */
    private void refreshUI() {
        if (viewState == ViewState.LOGINOUT) {
            if (loginState == LoginState.OUT ) {
                loginGroup.setVisibility(View.VISIBLE);
                loggedInGroup.setVisibility(View.GONE);
                ForgotPassGroup.setVisibility(View.GONE);
            }
            else if (loginState == LoginState.IN ) {
                loginGroup.setVisibility(View.GONE);
                loggedInGroup.setVisibility(View.VISIBLE);
                ForgotPassGroup.setVisibility(View.GONE);

                textLoggedIn.setText("Logged in: " + firebaseUser.getDisplayName() + "\n" + firebaseUser.getEmail());
            }
        }
        else if (viewState == ViewState.REGISTER) {

        }
        else if (viewState == ViewState.FORGOT) {
            loginGroup.setVisibility(View.GONE);
            loggedInGroup.setVisibility(View.GONE);
            ForgotPassGroup.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Validates Firebase login attempt
     * @param em Email login ID
     * @param pass Password
     */
    private void validateFirebaseLogin(String em, String pass) {
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
                            loginState = LoginState.IN;
                            refreshUI();
                        }
                        else {
                            //notify using Snackbar with option to resend verification email
                            Snackbar.make(getView(), "Error: Email not yet verified", 6000).setAction("Resend Email", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendEmailVerification(firebaseUser);
                                }
                            }).show();
                            firebaseAuth.signOut();
                            loginState = LoginState.OUT;
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

    /**
     * Sends a verification email for Firebase Authentication to the user
     * @param user The Firebase User to send the email to
     */
    private void sendEmailVerification(FirebaseUser user) {
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Email sent to " + user.getEmail(), Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getContext(), "Failed to send email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * Logout from Firebase
     */
    private void firebaseLogout() {
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.signOut();
        Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        emailEdit.setText("");
        passwordEdit.setText("");
        loginState = LoginState.OUT;
        refreshUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}