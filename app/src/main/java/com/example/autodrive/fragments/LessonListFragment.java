package com.example.autodrive.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autodrive.R;
import com.example.autodrive.views.itemLesson.FireStoreLessonHelper;
import com.example.autodrive.views.itemLesson.LessonAdapter;
import com.example.autodrive.views.itemLesson.LessonItem;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class LessonListFragment extends Fragment {

    private RecyclerView recyclerView;
    private LessonAdapter lessonAdapter;

    public LessonListFragment() {
        // Default constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lesson_list, container, false);

        // Initialize RecyclerView and set layout manager
        recyclerView = view.findViewById(R.id.rvNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupRecyclerView(); // Set up the adapter and query
        return view;
    }

    private void setupRecyclerView() {
        // Query lessons from Firestore, ordered by lesson number (descending)
        Query query = FireStoreLessonHelper.getCollectionRef()
                .orderBy("numLesson", Query.Direction.DESCENDING);

        // Build FirestoreRecyclerOptions with query and LessonItem class
        FirestoreRecyclerOptions<LessonItem> options = new FirestoreRecyclerOptions.Builder<LessonItem>()
                .setQuery(query, LessonItem.class)
                .build();

        // Initialize adapter with Firestore options
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        lessonAdapter = new LessonAdapter(options, getContext(), false);
        recyclerView.setAdapter(lessonAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Start listening to Firestore data when fragment becomes visible
        lessonAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Stop listening to Firestore to avoid memory leaks
        lessonAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the RecyclerView on resume
        lessonAdapter.notifyDataSetChanged();
    }
}
