package com.example.autodrive.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.autodrive.R;
import com.example.autodrive.views.itemLesson.LessonItem;
import com.example.autodrive.views.itemLesson.MyViewAdapterLessons;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LessonListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LessonListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // RecyclerView components
    private RecyclerView recyclerView;
    private MyViewAdapterLessons lessonAdapter;
    private List<LessonItem> lessonList;

    public LessonListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LessonListFragment.
     */
    public static LessonListFragment newInstance(String param1, String param2) {
        LessonListFragment fragment = new LessonListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lesson_list, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewLesson); // Ensure this ID matches your XML
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize lesson list and adapter
        lessonList = new ArrayList<>();
        populateLessonList(); // Populate the lesson list with sample data
        lessonAdapter = new MyViewAdapterLessons(getContext(), lessonList);

        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(lessonAdapter);

        return view;
    }

    /** 
     * Populate the lesson list with sample data
     */
    private void populateLessonList() {
        lessonList.add(new LessonItem(1, "01/11/24", "10:00 AM"));
        lessonList.add(new LessonItem(2, "02/11/24", "12:00 PM"));
        lessonList.add(new LessonItem(3, "03/11/24", "01:00 PM"));
        lessonList.add(new LessonItem(4, "04/11/24", "11:00 AM"));
        lessonList.add(new LessonItem(5, "05/11/24", "08:00 AM"));
    }

}
