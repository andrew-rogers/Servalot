package uk.co.rogerstech.servalot;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.support.v4.app.NotificationCompat;

import java.io.File;

public class BackgroundService extends Service {
    private static final int RUNNING_NOTIFICATION = 1;
    private final IBinder binder = new LocalBinder();
    private ServiceManager serviceManager;

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

        // Start service manager
        serviceManager = new ServiceManager(getFilesDir(), new File(getFilesDir(),"services.tsv"));
        serviceManager.registerNodeFactoryBuilder("sh", new ProcessNodeFactory.Builder(getFilesDir(), new File(getFilesDir(),"services")));
        serviceManager.registerNodeFactoryBuilder("rfcomm", new RfcommNodeFactory.Builder());
        serviceManager.load();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void showNotification() {
        Notification notification = new NotificationCompat.Builder(this)
                .setOngoing(true)
                .setWhen(0)
                .build();

        startForeground(RUNNING_NOTIFICATION, notification);
    }
}
