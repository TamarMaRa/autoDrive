package com.example.autodrive.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.autodrive.R;
import com.example.autodrive.views.itemExpanse.ExpenseItem;
import com.example.autodrive.views.itemExpanse.FireStoreExpanseHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;

public class ExpenseManagerFragment extends Fragment{

    private EditText tvAmount, tvDateExpanse, tvDescription, counterET;
    private Button btn_add_payment, btn_counter;
    private FireStoreExpanseHelper fireStoreExpanseHelper;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expense_manager, container, false);

        // Find UI components
        tvAmount = rootView.findViewById(R.id.expanses_input);
        tvDateExpanse = rootView.findViewById(R.id.payment_date_input);
        tvDescription = rootView.findViewById(R.id.describe_input);
        btn_add_payment = rootView.findViewById(R.id.btn_add_payment);
        btn_add_payment.setOnClickListener(v -> addPayment());
        // Save when button is clicked

        // Counter
        counterET = rootView.findViewById(R.id.counterET);
        btn_counter = rootView.findViewById(R.id.btn_counter);

        // Get current user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Save counter value per user
        btn_counter.setOnClickListener(view -> {
            String text = counterET.getText().toString();
            SharedPreferences prefs = getContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
            prefs.edit().putString("editTextValue_" + userId, text).apply();
            Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
        });

        // Load saved value on screen load for this user
        SharedPreferences prefs = getContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String savedText = prefs.getString("editTextValue_" + userId, "");
        counterET.setText(savedText);


        fireStoreExpanseHelper = new FireStoreExpanseHelper(this); // Initialize helper

        // ⬇️ Added date picker on tvDateExpanse click
        tvDateExpanse.setInputType(android.text.InputType.TYPE_NULL); // Disable keyboard

        tvDateExpanse.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, month1, dayOfMonth) -> {
                String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                tvDateExpanse.setText(selectedDate);
            }, year, month, day);

            datePickerDialog.show();
        });
        // ⬆️ End of date picker addition

        return rootView;
    }
    private void addPayment() {
        String amountStr = tvAmount.getText().toString().trim();
        String dateExpanse = tvDateExpanse.getText().toString().trim();
        String description = tvDescription.getText().toString().trim();

        if (amountStr.isEmpty() || dateExpanse.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int expense = Integer.parseInt(amountStr);
        ExpenseItem newExpense = new ExpenseItem(expense, dateExpanse, description);
        saveExpense(newExpense);

        Toast.makeText(getContext(), "Expense added!", Toast.LENGTH_SHORT).show();
    }

    private void saveExpense(ExpenseItem expenseItem) {
        fireStoreExpanseHelper.add(expenseItem);
    }


    public void getAllSuccess(ArrayList<ExpenseItem> expenseItems) {
        // Handle the success case where you get all expenses
    }

    public void getOneSuccess(ExpenseItem expenseItem) {
        // Handle the success case where you get a single expense
    }
}