package com.example.autodrive.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.autodrive.R;
import com.example.autodrive.views.itemExpanse.ExpenseItem;
import com.example.autodrive.views.itemExpanse.ExpanseAdapter;
import com.example.autodrive.views.itemExpanse.FireStoreExpanseHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ExpensesListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExpanseAdapter expanseAdapter;
    private FireStoreExpanseHelper expanseHelper;

    public ExpensesListFragment() {
        // Default constructor
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses_list, container, false);

        // Initialize RecyclerView and layout
        recyclerView = view.findViewById(R.id.rvExpense);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {
        // Query Firestore collection ordered by date descending
        Query query = expanseHelper.getCollectionRef()
                .orderBy("date", Query.Direction.DESCENDING);

        // Configure adapter with Firestore query results
        FirestoreRecyclerOptions<ExpenseItem> options = new FirestoreRecyclerOptions.Builder<ExpenseItem>()
                .setQuery(query, ExpenseItem.class)
                .build();

        // Initialize adapter and attach it to RecyclerView
        expanseAdapter = new ExpanseAdapter(options, requireContext(), false);
        recyclerView.setAdapter(expanseAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Start listening for real-time updates when fragment is visible
        expanseAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Stop listening to prevent memory leaks
        expanseAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh UI when fragment resumes
        expanseAdapter.notifyDataSetChanged();
    }
}
