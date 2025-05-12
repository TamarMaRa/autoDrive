package com.example.autodrive.alarm;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autodrive.R;

import java.util.Calendar;

public class AlarmManager extends AppCompatActivity {

    private Button btn_add_reminder;
    private TextView dateText;
    private Calendar alarmCalendar; // Holds the user-selected date and time for the alarm

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_lesson_manager_note); // Loads layout with the date picker and button

        checkExactAlarmPermission(); // Ensures the app has permission to set exact alarms on Android 12+

        btn_add_reminder = findViewById(R.id.btn_add_reminder);
        dateText = findViewById(R.id.dateText);
        alarmCalendar = Calendar.getInstance(); // Initialize with current date and time

        // When user clicks the reminder button, show a date-time picker dialog
        btn_add_reminder.setOnClickListener(v -> showDateTimePickerDialog());
    }

    // Opens a date picker dialog and handles selected date
    private void showDateTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AlarmManager.this,
                (view, selectedYear, selectedMonth, selectedDay) ->
                        showTimePickerDialog(selectedYear, selectedMonth, selectedDay),
                year, month, day
        );
        datePickerDialog.show();
    }

    // Opens a time picker dialog and handles selected time
    private void showTimePickerDialog(final int year, final int month, final int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                AlarmManager.this,
                (view, hourOfDay, minuteOfHour) -> {
                    // Set the final alarm time in the calendar
                    alarmCalendar.set(year, month, dayOfMonth, hourOfDay, minuteOfHour, 0);
                    String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year + " " + hourOfDay + ":" + minuteOfHour;
                    dateText.setText(selectedDate); // Display chosen date-time
                    setAlarm(alarmCalendar); // Schedule alarm
                },
                hour, minute, true
        );
        timePickerDialog.show();
    }

    // Schedules an exact alarm using AlarmManager
    private void setAlarm(Calendar alarmCalendar) {
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Prepare the intent to trigger when the alarm goes off
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE // Required for Android 12+
        );

        // Check permission for scheduling exact alarms (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Permission required to set exact alarms.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Schedule alarm to wake device if idle
        alarmManager.setExactAndAllowWhileIdle(
                android.app.AlarmManager.RTC_WAKEUP,
                alarmCalendar.getTimeInMillis(),
                pendingIntent
        );

        Toast.makeText(this, "Alarm set for: " + alarmCalendar.getTime(), Toast.LENGTH_SHORT).show();
    }

    // Checks and requests permission to schedule exact alarms (Android 12+)
    private void checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent); // Redirects user to system settings to grant permission
            }
        }
    }
}
