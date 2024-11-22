package com.example.autodrive.enterApp;

import android.app.Activity;
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

    public interface FBReply{
        public void createUserSuccsess(FirebaseUser user);
        public void loginSuccsess(FirebaseUser user);
    }

    public FBAuthHelper( Activity activity, FBReply fbReplay) {
        mAuth = FirebaseAuth.getInstance();
        this.fbReplay = fbReplay;
        this.activity = activity;

    }

    public FirebaseUser getCurrentUser(){return mAuth.getCurrentUser();}
    public Boolean isLoggedIn(){return getCurrentUser()!=null;}

    public void createUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity,(OnCompleteListener<AuthResult>) new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            fbReplay.createUserSuccsess(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            fbReplay.loginSuccsess(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
