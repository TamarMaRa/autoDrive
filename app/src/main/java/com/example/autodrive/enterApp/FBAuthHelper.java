package com.example.autodrive.enterApp;

import static com.example.autodrive.MainActivity.EMAIL_KEY;
import static com.example.autodrive.MainActivity.PASSWORD_KEY;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FBAuthHelper {
    private static final String TAG = "com.example.autodrive.enterApp.FBAuthHelper Tag";
    private Activity activity;
    private FirebaseAuth mAuth;
    private FBReply fbReplay;

    // Interface for handling auth result callbacks
    public interface FBReply {
        void createUserSuccsess(FirebaseUser user); // Called when user creation succeeds
        void loginSuccsess(FirebaseUser user); // Called when login succeeds
    }

    // Constructor receives activity context and callback interface
    public FBAuthHelper(Activity activity, FBReply fbReplay) {
        mAuth = FirebaseAuth.getInstance(); // Get FirebaseAuth instance
        this.fbReplay = fbReplay;
        this.activity = activity;
    }

    // Returns the currently logged-in Firebase user (if any)
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    // Checks if a user is currently logged in
    public Boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    // Attempts to create a new user with provided email and password
    public void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User creation successful
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            fbReplay.createUserSuccsess(user); // Trigger callback
                        } else {
                            // User creation failed
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(activity, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Attempts to log in an existing user
    public void login(String email, String password, Context context) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login successful
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Save login credentials in SharedPreferences
                            SharedPreferences prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(EMAIL_KEY, email);
                            editor.putString(PASSWORD_KEY, password);
                            editor.apply();

                            fbReplay.loginSuccsess(user); // Trigger callback
                        } else {
                            // Login failed
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(activity, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
