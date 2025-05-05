package com.example.autodrive;

import static com.example.autodrive.MainActivity.EMAIL_KEY;
import static com.example.autodrive.MainActivity.NO_STRING_AVAILABLE;
import static com.example.autodrive.MainActivity.PASSWORD_KEY;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.autodrive.databinding.ActivityMainBinding;
import com.example.autodrive.fragments.ExpenseManagerFragment;
import com.example.autodrive.fragments.EditLessonNote;
import com.example.autodrive.fragments.ExpensesListFragment;
import com.example.autodrive.fragments.LessonListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {

//    Button btnSignUp, btnLogIn;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

// Set the background color
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.light_green));

// Set icon and text tint colors
        ColorStateList colorStateList = ContextCompat.getColorStateList(this, R.color.dark_green);
        bottomNavigationView.setItemIconTintList(colorStateList);
        bottomNavigationView.setItemTextColor(colorStateList);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new EditLessonNote());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.nav_time: {
                    replaceFragment(new EditLessonNote());
                    break;
                }
                case R.id.nav_time_list: {
                    replaceFragment(new LessonListFragment());
                    break;
                }
                case R.id.nav_expanses: {
                    replaceFragment(new ExpenseManagerFragment());
                    break;
                }
                case R.id.nav_expanses_list: {
                    replaceFragment(new ExpensesListFragment());
                    break;
                }
                case R.id.nav_logout: {
                    new AlertDialog.Builder(MenuActivity.this)
                            .setTitle("Logout")
                            .setMessage("Are you sure you want to logout?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                // 1. Sign out from Firebase
                                FirebaseAuth.getInstance().signOut();

                                // 2. Clear any local user data/cache (add this)
                                clearUserPreferences();  // Implement this method if needed

                                SharedPreferences prefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString(EMAIL_KEY, NO_STRING_AVAILABLE);
                                editor.putString(PASSWORD_KEY, NO_STRING_AVAILABLE);
                                editor.apply();


                                // 3. Navigate to login screen with FLAG_ACTIVITY_CLEAR_TASK
                                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                                // 4. Force finish all activities
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    finishAndRemoveTask();  // More aggressive cleanup
                                } else {
                                    finish();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                    break;
                }
            }

            return true;
        });
    }

    private void replaceFragment(Fragment toReplace) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.linearLayout, toReplace);
        transaction.commit();
    }

    private void clearUserPreferences() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        preferences.edit().clear().apply();
    }
}