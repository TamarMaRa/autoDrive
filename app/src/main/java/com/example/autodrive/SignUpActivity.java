package com.example.autodrive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.autodrive.enterApp.FBAuthHelper;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity implements FBAuthHelper.FBReply {

    private EditText etEmail, etPwd, etPwd2;
    private FBAuthHelper fbAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // Initialize views and helpers
        initializeViews();
        setupAuthHelper();
        setupSignUpButton();
    }

    // Finds and assigns EditText views
    private void initializeViews() {
        etEmail = findViewById(R.id.email_input);
        etPwd = findViewById(R.id.password_input);
        etPwd2 = findViewById(R.id.confirm_password_input);
    }

    // Creates Firebase auth helper instance
    private void setupAuthHelper() {
        fbAuthHelper = new FBAuthHelper(this, this);
    }

    // Sets click listener for the sign-up button
    private void setupSignUpButton() {
        Button btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(v -> attemptSignUp());
    }

    // Attempts to create a new user with provided credentials
    private void attemptSignUp() {
        clearErrors();

        String email = etEmail.getText().toString().trim();
        String password = etPwd.getText().toString().trim();
        String confirmPassword = etPwd2.getText().toString().trim();

        if (!isInputValid(email, password, confirmPassword)) return;

        fbAuthHelper.createUser(email, password);
    }

    // Validates email and password fields
    private boolean isInputValid(String email, String password, String confirmPassword) {
        if (email.isEmpty()) {
            etEmail.setError("Email required");
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format");
            return false;
        }

        if (password.isEmpty()) {
            etPwd.setError("Password required");
            return false;
        }

        if (password.length() < 6) {
            etPwd.setError("Minimum 6 characters required");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etPwd2.setError("Passwords don't match");
            return false;
        }

        return true;
    }

    // Clears previous error messages from input fields
    private void clearErrors() {
        etEmail.setError(null);
        etPwd.setError(null);
        etPwd2.setError(null);
    }

    // Called when sign-up fails
    public void authError(String errorMessage) {
        Toast.makeText(this, "Sign up failed: " + errorMessage, Toast.LENGTH_LONG).show();
    }

    // Called when sign-up is successful
    @Override
    public void createUserSuccsess(FirebaseUser user) {
        Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // Not used here, but required by the interface
    @Override
    public void loginSuccsess(FirebaseUser user) {
    }
}
