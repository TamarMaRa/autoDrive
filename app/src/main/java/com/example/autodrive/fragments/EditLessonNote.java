package com.example.autodrive.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.autodrive.R;
import com.example.autodrive.alarm.AlarmReceiver;
import com.example.autodrive.views.itemLesson.FireStoreLessonHelper;
import com.example.autodrive.views.itemLesson.LessonItem;

import java.util.ArrayList;
import java.util.Calendar;

public class EditLessonNote extends Fragment implements FireStoreLessonHelper.FBReply {

    private EditText lessonNumberInput, dateInput, timeInput;
    private Button btnAddLesson, btn_add_reminder;
    private FireStoreLessonHelper fireStoreLessonHelper;
    private TextView dateText;
    private Calendar alarmCalendar;

    // Notification constants
    public static final String CHANNEL_ID = "LESSON_REMINDER_CHANNEL";
    public static final int NOTIFICATION_ID = 123;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;

    public EditLessonNote() {
        fireStoreLessonHelper = new FireStoreLessonHelper(this);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_lesson_note, container, false);

        // Initialize UI components
        lessonNumberInput = rootView.findViewById(R.id.lesson_number_input);
        dateInput = rootView.findViewById(R.id.date_input);
        timeInput = rootView.findViewById(R.id.time_input);
        btnAddLesson = rootView.findViewById(R.id.btn_add_event);
        btn_add_reminder = rootView.findViewById(R.id.btn_add_reminder);
        dateText = rootView.findViewById(R.id.dateText);

        btnAddLesson.setOnClickListener(v -> addLesson());
        alarmCalendar = Calendar.getInstance();

        checkExactAlarmPermission();
        checkNotificationPermission();
        btn_add_reminder.setOnClickListener(v -> showDateTimePickerDialog());

        return rootView;
    }

    private void addLesson() {
        String lessonNumberStr = lessonNumberInput.getText().toString().trim();
        String date = dateInput.getText().toString().trim();
        String time = timeInput.getText().toString().trim();

        if (lessonNumberStr.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int lessonNumber = Integer.parseInt(lessonNumberStr);
            LessonItem newLesson = new LessonItem(lessonNumber, date, time);
            saveLesson(newLesson);
            Toast.makeText(requireContext(), "Lesson added!", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid lesson number", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLesson(LessonItem lesson) {
        fireStoreLessonHelper.add(lesson);
    }

    // Date/Time Picker Methods
    private void showDateTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) ->
                        showTimePickerDialog(selectedYear, selectedMonth, selectedDay),
                year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog(final int year, final int month, final int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    alarmCalendar.set(year, month, dayOfMonth, hourOfDay, minuteOfHour, 0);
                    String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year + " " + hourOfDay + ":" + minuteOfHour;
                    dateText.setText(selectedDate);
                    setAlarm(alarmCalendar);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    // Alarm and Notification Methods
    private void setAlarm(Calendar alarmCalendar) {
        // Add validation for future time
        Calendar now = Calendar.getInstance();
        if (alarmCalendar.before(now)) {
            Toast.makeText(requireContext(), "Cannot set alarm for past time", Toast.LENGTH_SHORT).show();
            return;
        }

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        // Create intent with lesson details
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        intent.putExtra("LESSON_NUMBER", lessonNumberInput.getText().toString());
        intent.putExtra("LESSON_TIME", dateInput.getText() + " " + timeInput.getText());

        // Create pending intent
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                0,
                intent,
                flags
        );

        // Set the alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(requireContext(), "Permission required to set exact alarms.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmCalendar.getTimeInMillis(),
                pendingIntent
        );

        Toast.makeText(requireContext(),"Alarm set for: " + alarmCalendar.getTime(), Toast.LENGTH_LONG).show();
    }

    // Permission Handling
    private void checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        }
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Notifications disabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // FireStore callbacks
    @Override
    public void getAllSuccess(ArrayList<LessonItem> lessons) {}

    @Override
    public void getOneSuccess(LessonItem lesson) {}
}