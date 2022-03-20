package com.example.activemind.ui.login;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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

import com.example.activemind.R;
import com.example.activemind.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private ConstraintLayout loginGroup, loggedInGroup, forgotPassGroup, registerGroup, changePassGroup;
    //Login screen UI elements
    private TextView textForgotPassword;
    private EditText emailEdit, passwordEdit;
    private Button loginBtn, registerBtn;

    //Logged in screen UI elements
    private TextView textLoggedInName, textLoggedInEmail;
    private Button logoutBtn, changePassBtn;

    //Forgot Password screen UI elements
    private EditText forgotPassEmailEdit;
    private Button sendForgotBtn, backForgotBtn;

    //Register screen UI elements
    private EditText registerFNameEdit, registerLNameEdit, registerEmailEdit, registerPasswordEdit, registerConfirmPasswordEdit;
    private Button doRegisterBtn, alrHaveAccBtn;

    //Change Password screen UI elements
    private EditText oldPassEdit, newPassEdit, confirmNewPassEdit;
    private Button doChangePassBtn, cancelChangePassBtn;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    //States
    private enum LoginState { IN, OUT }
    private LoginState loginState;
    private enum ViewState { LOGINOUT, FORGOT, REGISTER, CHANGE }
    private ViewState viewState;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //setup
        setup();

        // create callback to override back button behaviour
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (viewState == ViewState.FORGOT || viewState == ViewState.REGISTER || viewState == ViewState.CHANGE) {
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

        changePassBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewState = ViewState.CHANGE;
                refreshUI();
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

        sendForgotBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordResetEmail(forgotPassEmailEdit.getText().toString().trim());
            }
        }));

        backForgotBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewState = ViewState.LOGINOUT;
                refreshUI();
            }
        }));

        registerBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewState = ViewState.REGISTER;
                refreshUI();
            }
        }));

        alrHaveAccBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewState = ViewState.LOGINOUT;
                refreshUI();
            }
        }));

        doRegisterBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        }));

        doChangePassBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFirebasePassword();
            }
        }));

        cancelChangePassBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewState = ViewState.LOGINOUT;
                refreshUI();
            }
        }));

        return root;
    }

    /**
     * Sets up stuff (UI, Firebase, etc.)
     */
    public void setup() {
        //UI
        loginGroup = binding.LoginGroup;
        textForgotPassword = binding.textForgotPassword;
        emailEdit = binding.emailTextEntry;
        passwordEdit = binding.passwordTextEntry;
        loginBtn = binding.buttonLogin;
        registerBtn = binding.buttonRegister;

        loggedInGroup = binding.LoggedInGroup;
        textLoggedInName = binding.textLoggedInName;
        textLoggedInEmail = binding.textLoggedInEmail;
        logoutBtn = binding.buttonLogout;
        changePassBtn = binding.buttonChangePassword;

        forgotPassGroup = binding.ForgotPasswordGroup;
        forgotPassEmailEdit = binding.forgotEmailTextEntry;
        sendForgotBtn = binding.buttonSendForgot;
        backForgotBtn = binding.buttonBackForgot;

        registerGroup = binding.RegisterGroup;
        registerFNameEdit = binding.registerFirstNameTextEntry;
        registerLNameEdit = binding.registerLastNameTextEntry;
        registerEmailEdit = binding.registerEmailTextEntry;
        registerPasswordEdit = binding.registerPasswordTextEntry;
        registerConfirmPasswordEdit = binding.registerConfirmPasswordTextEntry;
        doRegisterBtn = binding.buttonDoRegister;
        alrHaveAccBtn = binding.buttonAlreadyHaveAccount;

        changePassGroup = binding.ChangePasswordGroup;
        oldPassEdit = binding.oldPasswordTextEntry;
        newPassEdit = binding.newPasswordTextEntry;
        confirmNewPassEdit = binding.confirmNewPasswordTextEntry;
        doChangePassBtn = binding.buttonDoChangePassword;
        cancelChangePassBtn = binding.buttonCancelChangePassword;

        SpannableString fpContent = new SpannableString("Forgot Password?");
        fpContent.setSpan(new UnderlineSpan(), 0, fpContent.length(), 0);
        textForgotPassword.setText(fpContent);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

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
                forgotPassGroup.setVisibility(View.GONE);
                registerGroup.setVisibility(View.GONE);
                changePassGroup.setVisibility(View.GONE);
            }
            else if (loginState == LoginState.IN ) {
                loginGroup.setVisibility(View.GONE);
                loggedInGroup.setVisibility(View.VISIBLE);
                forgotPassGroup.setVisibility(View.GONE);
                registerGroup.setVisibility(View.GONE);
                changePassGroup.setVisibility(View.GONE);
                textLoggedInName.setText(firebaseUser.getDisplayName());
                textLoggedInEmail.setText(firebaseUser.getEmail());
                oldPassEdit.setText("");
                newPassEdit.setText("");
                confirmNewPassEdit.setText("");
            }
        }
        else if (viewState == ViewState.FORGOT) {
            loginGroup.setVisibility(View.GONE);
            loggedInGroup.setVisibility(View.GONE);
            forgotPassGroup.setVisibility(View.VISIBLE);
            registerGroup.setVisibility(View.GONE);
            changePassGroup.setVisibility(View.GONE);
        }
        else if (viewState == ViewState.REGISTER) {
            registerFNameEdit.setText("");
            registerLNameEdit.setText("");
            registerEmailEdit.setText("");
            registerPasswordEdit.setText("");
            registerConfirmPasswordEdit.setText("");
            loginGroup.setVisibility(View.GONE);
            loggedInGroup.setVisibility(View.GONE);
            forgotPassGroup.setVisibility(View.GONE);
            registerGroup.setVisibility(View.VISIBLE);
            changePassGroup.setVisibility(View.GONE);
        }
        else if (viewState == ViewState.CHANGE) {
            loginGroup.setVisibility(View.GONE);
            loggedInGroup.setVisibility(View.GONE);
            forgotPassGroup.setVisibility(View.GONE);
            registerGroup.setVisibility(View.GONE);
            changePassGroup.setVisibility(View.VISIBLE);
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
                        Toast.makeText(getContext(), "Verification email sent to " + user.getEmail(), Toast.LENGTH_LONG).show();
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

    /**
     * Sends a password reset email to the User
     * @param emailTo Email address to send the email to
     */
    private void sendPasswordResetEmail(String emailTo) {
        firebaseAuth.sendPasswordResetEmail(emailTo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Password reset email sent to " + emailTo, Toast.LENGTH_LONG).show();
                    forgotPassEmailEdit.setText("");
                    viewState = ViewState.LOGINOUT;
                    refreshUI();
                }
                else {
                    Toast.makeText(getContext(), "Error, email not sent", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Validate user inputs for registration, ensuring no blanks and proper format
     * @param fS First Name
     * @param lS Last Name
     * @param eS Email
     * @param pS Password
     * @param cPS Confirm Password
     * @return True if inputs are valid, False if invalid
     */
    private boolean validateRegisterInputs(String fS, String lS, String eS, String pS, String cPS) {
        boolean isBlankInputs = true;

        if (cPS.isEmpty()) { registerConfirmPasswordEdit.setError("Must not be empty"); registerConfirmPasswordEdit.requestFocus(); isBlankInputs = false;}
        if (pS.isEmpty()) { registerPasswordEdit.setError("Must not be empty"); registerPasswordEdit.requestFocus(); isBlankInputs = false;}
        if (eS.isEmpty()) { registerEmailEdit.setError("Must not be empty"); registerEmailEdit.requestFocus(); isBlankInputs = false;}
        if (lS.isEmpty()) { registerLNameEdit.setError("Must not be empty"); registerLNameEdit.requestFocus(); isBlankInputs = false;}
        if (fS.isEmpty()) { registerFNameEdit.setError("Must not be empty"); registerFNameEdit.requestFocus(); isBlankInputs = false;}

        if (!isBlankInputs) { return false; }

        //check if email is valid format
        if (!eS.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(eS).matches()) {
            registerEmailEdit.setError("Invalid Email address");
            registerEmailEdit.requestFocus();
            return false;
        }
        //password
        else if (!pS.isEmpty() && !cPS.isEmpty()) {
            //check if password matches confirm password
            if (!pS.equals(cPS)) {
                registerPasswordEdit.setError("Password and Confirm Password must match");
                registerConfirmPasswordEdit.setError("Password and Confirm Password must match");
                registerPasswordEdit.requestFocus();
                return false;
            }
            //check if there is whitespace in the password
            else if (pS.contains(" ")) {
                registerPasswordEdit.setError("Password must not contain spaces");
                registerPasswordEdit.requestFocus();
                return false;
            }
            //ensure password is at least 6 char long
            else if (pS.length() < 6) {
                registerPasswordEdit.setError("Password must contain at least 6 characters");
                registerPasswordEdit.requestFocus();
                return false;
            }
        }

        return true;
    }

    /**
     * Register Firebase user
     */
    private void registerUser() {
        String fS = registerFNameEdit.getText().toString().trim(),
                lS = registerLNameEdit.getText().toString().trim(),
                eS = registerEmailEdit.getText().toString().trim(),
                pS = registerPasswordEdit.getText().toString().trim(),
                cPS = registerConfirmPasswordEdit.getText().toString().trim();
        //input validation before sending to Firebase
        if (validateRegisterInputs(fS, lS, eS, pS, cPS)) {
            String s1 = registerEmailEdit.getText().toString().trim();
            String s2 = registerPasswordEdit.getText().toString().trim();
            firebaseAuth.createUserWithEmailAndPassword(s1, s2).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Toast.makeText(getContext(), "Registered", Toast.LENGTH_SHORT).show();
                        sendEmailVerification(user);

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(registerFNameEdit.getText().toString().trim() + " " + registerLNameEdit.getText().toString().trim()).build();
                        user.updateProfile(profileUpdates);

                        DocumentReference docRef = db.collection("Users").document(user.getUid().toString());
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("First Name", fS);
                        userInfo.put("Last Name", lS);
                        userInfo.put("Email", eS);
                        docRef.set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i("Register User", "Successfully added user doc to Firestore");
                            }
                        });

                        viewState = ViewState.LOGINOUT;
                        refreshUI();
                    }
                    else {
                        try {
                            throw task.getException();
                        }
                        catch(FirebaseAuthInvalidCredentialsException e) {
                            Toast.makeText(getContext(), "Registration Failed: Invalid email", Toast.LENGTH_SHORT).show();
                        }
                        catch(FirebaseAuthUserCollisionException e) {
                            Toast.makeText(getContext(), "Registration Failed: Email already registered", Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e) {
                            Log.e(null, e.getMessage());
                        }
                    }
                }
            });
        }
    }

    /**
     * Validate user inputs for registration, ensuring no blanks and proper format
     * @param pS Password
     * @param nPS New Password
     * @param cnPS Confirm New Password
     * @return True if inputs are valid, False if invalid
     */
    private boolean validateChangePasswordInputs(String pS, String nPS, String cnPS) {
        boolean isBlankInputs = true;

        if (cnPS.isEmpty()) { confirmNewPassEdit.setError("Must not be empty"); confirmNewPassEdit.requestFocus(); isBlankInputs = false;}
        if (nPS.isEmpty()) { newPassEdit.setError("Must not be empty"); newPassEdit.requestFocus(); isBlankInputs = false;}
        if (pS.isEmpty()) { oldPassEdit.setError("Must not be empty"); oldPassEdit.requestFocus(); isBlankInputs = false;}

        if (!isBlankInputs) { return false; }

        //check if password matches confirm password
        if (!nPS.equals(cnPS)) {
            newPassEdit.setError("Must Match Confirm New Password");
            confirmNewPassEdit.setError("Must Match New Password");
            newPassEdit.requestFocus();
            return false;
        }
        //check if there is whitespace in the password
        else if (nPS.contains(" ")) {
            newPassEdit.setError("Password must not contain spaces");
            newPassEdit.requestFocus();
            return false;
        }
        //ensure password is at least 6 char long
        else if (nPS.length() < 6) {
            newPassEdit.setError("Password must contain at least 6 characters");
            newPassEdit.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Re-Auths user based on current password, validates and set new password
     */
    private void changeFirebasePassword() {
        String pS = oldPassEdit.getText().toString().trim(),
                nPS = newPassEdit.getText().toString().trim(),
                cnPS = confirmNewPassEdit.getText().toString().trim();

        if (validateChangePasswordInputs(pS, nPS, cnPS)) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), pS);

            // Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(nPS).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Password changed", Toast.LENGTH_SHORT).show();
                                    viewState = ViewState.LOGINOUT;
                                    refreshUI();
                                }
                                else {
                                    Toast.makeText(getContext(), "Failed to change password", Toast.LENGTH_SHORT).show();
                                    Log.e(null, task.getException().toString());
                                }
                            }
                        });
                    }
                    else {
                        try {
                            throw task.getException();
                        }
                        catch(FirebaseAuthInvalidCredentialsException e) {
                            Toast.makeText(getContext(), "Old password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e) {
                            Toast.makeText(getContext(), "Failed to change password", Toast.LENGTH_SHORT).show();
                            Log.e(null, e.getMessage());
                        }
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