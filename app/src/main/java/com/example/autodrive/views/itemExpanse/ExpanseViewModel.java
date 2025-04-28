package com.example.autodrive.views.itemExpanse;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ExpanseViewModel extends ViewModel {
    private final MutableLiveData<List<ExpenseItem>> expanses = new MutableLiveData<>(new ArrayList<>());
}
