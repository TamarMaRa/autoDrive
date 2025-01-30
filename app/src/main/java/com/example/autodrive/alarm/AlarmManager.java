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
    private Calendar alarmCalendar; // To store the selected date and time

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_time_manager);

        checkExactAlarmPermission();

        btn_add_reminder = findViewById(R.id.btn_add_reminder);
        dateText = findViewById(R.id.dateText);
        alarmCalendar = Calendar.getInstance();

        btn_add_reminder.setOnClickListener(v -> showDateTimePickerDialog());
    }


    private void showDateTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AlarmManager.this,
                (view, selectedYear, selectedMonth, selectedDay) -> showTimePickerDialog(selectedYear, selectedMonth, selectedDay),
                year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog(final int year, final int month, final int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(AlarmManager.this,
                (view, hourOfDay, minuteOfHour) -> {
                    alarmCalendar.set(year, month, dayOfMonth, hourOfDay, minuteOfHour, 0);
                    String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year + " " + hourOfDay + ":" + minuteOfHour;
                    dateText.setText(selectedDate);
                    setAlarm(alarmCalendar); // Schedule the alarm
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void setAlarm(Calendar alarmCalendar) {
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE for Android 12+
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Ensure exact alarm permission is granted
            alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Permission required to set exact alarms.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(this, "Alarm set for: " + alarmCalendar.getTime(), Toast.LENGTH_SHORT).show();
    }

    private void checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
    }
}
