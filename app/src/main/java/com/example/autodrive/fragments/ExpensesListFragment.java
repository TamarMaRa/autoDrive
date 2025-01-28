package com.example.autodrive.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.autodrive.R;
import com.example.autodrive.views.itemExpanse.ExpenseItem;
import com.example.autodrive.views.itemExpanse.MyViewAdapterExpanse;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExpensesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpensesListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    // RecyclerView components
    private RecyclerView recyclerView;
    private MyViewAdapterExpanse expenseAdapter;
    private List<ExpenseItem> expenseList;


    public ExpensesListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpansesListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpensesListFragment newInstance(String param1, String param2) {
        ExpensesListFragment fragment = new ExpensesListFragment();
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
        View view = inflater.inflate(R.layout.fragment_expenses_list, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewExpense); // Ensure this ID matches your XML
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize lesson list and adapter
        expenseList = new ArrayList<>();
        populateLessonList(); // Populate the lesson list with sample data
        expenseAdapter = new MyViewAdapterExpanse(getContext(),expenseList);

        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(expenseAdapter);
        return view;
    }

    private void populateLessonList() {
       expenseList.add(new ExpenseItem(180, "01/11/24", "lesson 1"));
       expenseList.add(new ExpenseItem(180, "02/11/24", "lesson 2"));
       expenseList.add(new ExpenseItem(180, "03/11/24", "lesson 3"));
       expenseList.add(new ExpenseItem(180, "04/11/24", "lesson 4"));
       expenseList.add(new ExpenseItem(180, "05/11/24", "lesson 5"));
       expenseList.add(new ExpenseItem(180, "06/11/24", "lesson 6"));
    }
}