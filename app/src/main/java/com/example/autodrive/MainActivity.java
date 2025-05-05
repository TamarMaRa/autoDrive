package com.example.autodrive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autodrive.enterApp.FBAuthHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//https://github.com/belindaatschool/noteslist
public class MainActivity extends AppCompatActivity implements FBAuthHelper.FBReply {

    public static final String EMAIL_KEY = "EMAIL";
    public static final String PASSWORD_KEY = "PASSWORD";

    public static final String NO_STRING_AVAILABLE = "";

    EditText etEmail, etPwd;
    Button btnSignUp;

    // FloatingActionButton butterflyBtn = findViewById(R.id.butterfly_button);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        etEmail = findViewById(R.id.email_input);
        etPwd = findViewById(R.id.password_input);
        btnSignUp = findViewById(R.id.btnSignUp);

//        butterflyBtn.setOnClickListener(v -> {
//            // Open help screen, or show dialog, or send support email
//            new AlertDialog.Builder(this)
//                    .setTitle("Need Help?")
//                    .setMessage("Contact support at support@yourapp.com or check the FAQ.")
//                    .setPositiveButton("OK", null)
//                    .show();
//        });


        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        FBAuthHelper fbAuthHelper = new FBAuthHelper(this, this);

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

        SharedPreferences prefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);

        String email = prefs.getString(EMAIL_KEY, NO_STRING_AVAILABLE);
        String password = prefs.getString(PASSWORD_KEY, NO_STRING_AVAILABLE);

        if (!(email.equals(NO_STRING_AVAILABLE) || password.equals(NO_STRING_AVAILABLE))) {
            fbAuthHelper.login(email, password, this);
        }

    }

    private void checkPasswordValidity(String password) {
        if (password.length() >= 6) {        // Password is valid
        } else {        // Password is invalid, show an error message
            etPwd.setError("Password must be at least 6 characters long");
        }
    }

    private void checkEmailValidity(String email) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Email is valid
        } else {        // Email is invalid, show an error message
            etEmail.setError("Invalid email address");
        }
    }

    @Override
    public void loginSuccsess(FirebaseUser user) {
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    @Override
    public void createUserSuccsess(FirebaseUser user) {
    }
}