package com.example.autodrive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autodrive.enterApp.FBAuthHelper;
import com.example.autodrive.views.itemExpanse.MoneySpentManager;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements FBAuthHelper.FBReply {

    // Keys for saving login info in SharedPreferences
    public static final String EMAIL_KEY = "EMAIL";
    public static final String PASSWORD_KEY = "PASSWORD";
    public static final String NO_STRING_AVAILABLE = "";

    EditText etEmail, etPwd;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize input fields and sign-up button
        etEmail = findViewById(R.id.email_input);
        etPwd = findViewById(R.id.password_input);
        btnSignUp = findViewById(R.id.btnSignUp);

        // Go to SignUpActivity when Sign Up button is clicked
        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // Firebase authentication helper
        FBAuthHelper fbAuthHelper = new FBAuthHelper(this, this);

        // Log in when button is clicked
        findViewById(R.id.btnLogIn).setOnClickListener(v -> {
            if (etEmail.getText().toString().isEmpty()) {
                etEmail.setError("Invalid email address");
                return;
            }
            if (etPwd.getText().toString().isEmpty()) {
                etPwd.setError("Password must be at least 6 characters long");
                return;
            }

            checkEmailValidity(etEmail.getText().toString());
            checkPasswordValidity(etPwd.getText().toString());

            fbAuthHelper.login(
                    etEmail.getText().toString(),
                    etPwd.getText().toString(),
                    this);
        });

        // Try auto-login using stored credentials from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String email = prefs.getString(EMAIL_KEY, NO_STRING_AVAILABLE);
        String password = prefs.getString(PASSWORD_KEY, NO_STRING_AVAILABLE);

        if (!(email.equals(NO_STRING_AVAILABLE) || password.equals(NO_STRING_AVAILABLE))) {
            fbAuthHelper.login(email, password, this);
        }
    }

    // Validates password length
    private void checkPasswordValidity(String password) {
        if (password.length() < 6) {
            etPwd.setError("Password must be at least 6 characters long");
        }
    }

    // Validates email format
    private void checkEmailValidity(String email) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email address");
        }
    }

    @Override
    public void loginSuccsess(FirebaseUser user) {
        // Initialize lesson tracking after login
        MoneySpentManager.init();

        // Go to the main menu
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    @Override
    public void createUserSuccsess(FirebaseUser user) {
        // Called on successful user creation (unused here)
    }
}
