package com.example.autodrive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autodrive.enterApp.FBAuthHelper;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity implements FBAuthHelper.FBReply {
    Button btnSignUp;
    EditText etEmail, etPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);

                //TODO: add code above that saves the info into the database, only than change screens
            }
        });
        etEmail = findViewById(R.id.email_input);
        etPwd = findViewById(R.id.password_input);

        FBAuthHelper fbAuthHelper = new FBAuthHelper(this, this);

        findViewById(R.id.btnSignUp).setOnClickListener(v -> {
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

            fbAuthHelper.createUser(
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
    public void createUserSuccsess(FirebaseUser user) {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void loginSuccsess(FirebaseUser user) {
    }
}
