package com.example.autodrive.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.autodrive.R;

import java.util.Calendar;

public class TimeManagerFragment extends Fragment {

    private TextView dateText;  // TextView to show the selected date and time
    private Calendar alarmCalendar;

    public TimeManagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_time_manager, container, false);

        dateText = rootView.findViewById(R.id.dateText);
        Button btnAddReminder = rootView.findViewById(R.id.btn_add_reminder);

        // Set up the calendar to store the date and time selected
        alarmCalendar = Calendar.getInstance();

        // When the "Add a reminder" button is clicked, show the date and time picker dialogs
        btnAddReminder.setOnClickListener(v -> showDateTimePickerDialog());

        return rootView;
    }

    private void showDateTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    showTimePickerDialog(selectedYear, selectedMonth, selectedDay);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog(final int year, final int month, final int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Show the TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    alarmCalendar.set(year, month, dayOfMonth, hourOfDay, minuteOfHour, 0);
                    String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year + " " + hourOfDay + ":" + minuteOfHour;
                    dateText.setText(selectedDate);  // Display the selected date and time above the buttons
                    dateText.setVisibility(View.VISIBLE);
                    // Optionally, call a method to set the reminder (alarm)
                    setReminder(alarmCalendar);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void setReminder(Calendar alarmCalendar) {
        // This method can be used to set the alarm for the selected time
        // For now, we're just showing a Toast message as an example
        Toast.makeText(getContext(), "Reminder set for: " + alarmCalendar.getTime(), Toast.LENGTH_SHORT).show();

        // Here, you would add code to set an actual alarm or reminder using Android's AlarmManager
    }
}