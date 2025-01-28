package com.example.autodrive;

import android.content.Intent;
import android.content.res.ColorStateList;
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
import com.example.autodrive.fragments.TimeManagerFragment;
import com.example.autodrive.fragments.ExpensesListFragment;
import com.example.autodrive.fragments.LessonListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

        replaceFragment(new TimeManagerFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.nav_time: {
                    replaceFragment(new TimeManagerFragment());
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
                                // Log out the user from Firebase
                                FirebaseAuth.getInstance().signOut();

                                // Navigate to login activity
                                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
                                startActivity(intent);

                                // Finish the current activity
                                finish();
                            })
                            .setNegativeButton("No", (dialog, which) -> {
                                // Dismiss the dialog
                                dialog.dismiss();
                            })
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
}