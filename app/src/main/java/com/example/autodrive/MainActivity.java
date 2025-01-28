package com.example.autodrive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autodrive.enterApp.FBAuthHelper;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements FBAuthHelper.FBReply {

    EditText etEmail, etPwd;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        etEmail = findViewById(R.id.email_input);
        etPwd = findViewById(R.id.password_input);
        btnSignUp = findViewById(R.id.btnSignUp);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
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
                    etPwd.getText().toString());
        });

    }

    private void checkPasswordValidity(String password) {
        if (password.length() >= 6) {        // Password is valid
        } else {        // Password is invalid, show an error message
            etPwd.setError("Password must be at least 6 characters long");
        }
    }

    private void checkEmailValidity(String email) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {        // Email is valid
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