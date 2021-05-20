package com.konovus.noter.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.konovus.noter.activity.NewNoteActivity;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class SavingNoteService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearService", "Service Destroyed");
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("NoteR", "Service Destroyed");
        Toast.makeText(this, "IN Service", Toast.LENGTH_SHORT).show();
        stopSelf();
    }

}
