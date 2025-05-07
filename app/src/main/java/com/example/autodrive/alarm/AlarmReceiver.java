package com.example.autodrive.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.autodrive.MainActivity;
import com.example.autodrive.R;
import com.example.autodrive.fragments.EditLessonNote;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // When the alarm triggers, show a notification with the lesson reminder
        showNotification(context,
                "Lesson Reminder!",
                "Upcoming driving lesson now",
                "Drive safely :) "); // Display the message when the notification is triggered
    }

    // Creates a notification channel required for Android versions Oreo and above
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    EditLessonNote.CHANNEL_ID,
                    "Lesson Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for driving lesson reminders");

            // Create the notification channel for the app
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    // Method to show the notification to the user
    private void showNotification(Context context, String title, String content, String bigText) {
        // Ensure that the notification channel is created
        createNotificationChannel(context);

        // Intent to open MainActivity when the notification is tapped
        Intent intent = new Intent(context, com.example.autodrive.MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // PendingIntent that will open MainActivity when the notification is tapped
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification with the given title, content, and styling
        Notification notification = new NotificationCompat.Builder(context, EditLessonNote.CHANNEL_ID)
                .setSmallIcon(R.drawable.icons_green_car) // Set the notification icon
                .setContentTitle(title) // Set the notification title
                .setContentText(content) // Set the notification content
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText)) // Show large text for better visibility
                .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority for the notification
                .setCategory(NotificationCompat.CATEGORY_REMINDER) // Set notification category as reminder
                .setContentIntent(pendingIntent) // When tapped, it will open MainActivity
                .setAutoCancel(true) // Dismiss notification after tapping
                .build();

        // Get the NotificationManager and issue the notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(EditLessonNote.NOTIFICATION_ID, notification);
    }
}
