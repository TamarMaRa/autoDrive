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
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lesson_list, container, false);

        recyclerView = view.findViewById(R.id.rvNotes); // Make sure this matches the XML ID
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        Query query = FireStoreLessonHelper.getCollectionRef()
                .orderBy("numLesson", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<LessonItem> options = new FirestoreRecyclerOptions.Builder<LessonItem>()
                .setQuery(query, LessonItem.class)
                .build();

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext())); // Use requireContext() instead of "this"
        lessonAdapter = new LessonAdapter(options, getContext(),false);
        recyclerView.setAdapter(lessonAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        lessonAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        lessonAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        lessonAdapter.notifyDataSetChanged();

    }

}
