package uk.co.rogerstech.servalot;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.support.v4.app.NotificationCompat;

public class BackgroundService extends Service {
    private static final int RUNNING_NOTIFICATION = 1;

    public BackgroundService() {
    }

    public class LocalBinder extends Binder {
        BackgroundService getService() {
            return BackgroundService.this;
        }
    }

    @Override
    public void onCreate() {
        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void showNotification() {
        Notification notification = new NotificationCompat.Builder(this)
                .setOngoing(true)
                .setWhen(0)
                .build();

        startForeground(RUNNING_NOTIFICATION, notification);
    }
}
