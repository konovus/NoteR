package com.konovus.noter.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.konovus.noter.R;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


public class WorkerNoteIt extends Worker {

    Context context;
    public WorkerNoteIt(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        displayNotification(getInputData().getString("title"), getInputData().getString("text"), NotificationCompat.PRIORITY_DEFAULT, 1, context);
        return Result.success();
    }

    public static void displayNotification(String title, String desc, int priority, int id, Context context){
        final String CHANNEL_ID = "WorkManager";
        createNotificationChannel(context, CHANNEL_ID);
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_icon);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(desc)
                .setSmallIcon(R.drawable.notification_white)
                .setLargeIcon(largeIcon)
                .setAutoCancel(true)
                .setPriority(priority);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(id, builder.build());
    }
    private static void createNotificationChannel(Context context, String CHANNEL_ID){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "JobIS", importance);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
