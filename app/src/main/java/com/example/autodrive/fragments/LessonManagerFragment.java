package com.example.autodrive.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.autodrive.R;
import com.example.autodrive.alarm.AlarmReceiver;
import com.example.autodrive.views.itemLesson.FireStoreLessonHelper;
import com.example.autodrive.views.itemLesson.LessonItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

public class LessonManagerFragment extends Fragment implements FireStoreLessonHelper.FBReply {

    private EditText lessonNumberInput, dateInput, timeInput, counterET;
    private Button btnAddLesson, btn_add_reminder;
    private FireStoreLessonHelper fireStoreLessonHelper;
    private TextView dateText;
    private Calendar alarmCalendar;

    // Constants for notifications
    public static final String CHANNEL_ID = "LESSON_REMINDER_CHANNEL";
    public static final int NOTIFICATION_ID = 123;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;

    public LessonManagerFragment() {
        // Initialize Firestore helper with this fragment as callback
        fireStoreLessonHelper = new FireStoreLessonHelper(this);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lesson_manager_note, container, false);

        // Initialize views
        lessonNumberInput = rootView.findViewById(R.id.lesson_number_input);
        dateInput = rootView.findViewById(R.id.date_input);
        timeInput = rootView.findViewById(R.id.time_input);
        btnAddLesson = rootView.findViewById(R.id.btn_add_event);
        btn_add_reminder = rootView.findViewById(R.id.btn_add_reminder);
        dateText = rootView.findViewById(R.id.dateText);
        counterET = rootView.findViewById(R.id.counterET2);

        // Restore saved lesson counter (user-specific)
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String counterKey = "editTextValue_lessonNoteCounter_" + userId;
        SharedPreferences prefs = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String savedText = prefs.getString(counterKey, "");
        counterET.setText(savedText);

        // Set button actions
        btnAddLesson.setOnClickListener(v -> addLesson());
        alarmCalendar = Calendar.getInstance();
        checkExactAlarmPermission();
        checkNotificationPermission();
        btn_add_reminder.setOnClickListener(v -> showDateTimePickerDialog());

        // Set up date picker
        dateInput.setInputType(android.text.InputType.TYPE_NULL); // disable keyboard
        dateInput.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, y, m, d) -> {
                String selectedDate = d + "/" + (m + 1) + "/" + y;
                dateInput.setText(selectedDate);
            }, year, month, day);

            datePickerDialog.show();
        });

        // Set up time picker
        timeInput.setInputType(android.text.InputType.TYPE_NULL); // disable keyboard
        timeInput.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                    (view, h, m) -> {
                        String formattedTime = String.format("%02d:%02d", h, m);
                        timeInput.setText(formattedTime);
                    }, hour, minute, true);

            timePickerDialog.show();
        });

        // Load and display current number of lessons
        updateNumLessons();

        return rootView;
    }

    // Gets lesson count and auto-fills lesson number
    private void updateNumLessons() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("users").document(currentUser.getUid()).collection("my_lessons")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int itemCount = queryDocumentSnapshots.size();
                    Log.d("ItemCount", "Total items: " + itemCount);
                    counterET.setText(String.valueOf(itemCount));
                    lessonNumberInput.setText(String.valueOf(itemCount + 1));
                    lessonNumberInput.setEnabled(false);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error getting documents: ", e);
                    counterET.setText("0");
                    lessonNumberInput.setText("1");
                    lessonNumberInput.setEnabled(false);
                });
    }

    // Adds a new lesson after input validation
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

        updateNumLessons();
    }

    // Saves lesson to Firestore
    private void saveLesson(LessonItem lesson) {
        fireStoreLessonHelper.add(lesson);
    }

    // Launches date and time picker to set reminder
    private void showDateTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, y, m, d) -> showTimePickerDialog(y, m, d),
                year, month, day);
        datePickerDialog.show();
    }

    // Shows time picker after date is selected
    private void showTimePickerDialog(final int year, final int month, final int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, h, m) -> {
                    alarmCalendar.set(year, month, dayOfMonth, h, m, 0);
                    String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year + " " + h + ":" + m;
                    dateText.setText(selectedDate);
                    setAlarm(alarmCalendar);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    // Schedules an alarm at the selected date/time
    private void setAlarm(Calendar alarmCalendar) {
        Calendar now = Calendar.getInstance();
        if (alarmCalendar.before(now)) {
            Toast.makeText(requireContext(), "Cannot set alarm for past time", Toast.LENGTH_SHORT).show();
            return;
        }

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        intent.putExtra("LESSON_NUMBER", lessonNumberInput.getText().toString());
        intent.putExtra("LESSON_TIME", dateInput.getText() + " " + timeInput.getText());

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, flags);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(requireContext(), "Permission required to set exact alarms.", Toast.LENGTH_SHORT).show();
            return;
        }

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                alarmCalendar.getTimeInMillis(), pendingIntent);

        Toast.makeText(requireContext(), "Alarm set for: " + alarmCalendar.getTime(), Toast.LENGTH_LONG).show();
    }

    // Requests permission for exact alarms if needed (Android 12+)
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

    // Requests notification permission if needed (Android 13+)
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
        }
    }

    // Handles permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION &&
                grantResults.length > 0 &&
                grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Notifications disabled", Toast.LENGTH_SHORT).show();
        }
    }

    // Firestore callbacks (not used in this screen)
    @Override
    public void getAllSuccess(ArrayList<LessonItem> lessons) {}
    @Override
    public void getOneSuccess(LessonItem lesson) {}
}
