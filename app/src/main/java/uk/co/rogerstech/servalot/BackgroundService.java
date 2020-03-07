package uk.co.rogerstech.servalot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.support.v4.app.NotificationCompat;

import java.io.File;

public class BackgroundService extends Service {
    private static final int RUNNING_NOTIFICATION = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "uk.co.rogerstech.servalot";
    private static final String CHANNEL_NAME = "Servalot Background Service";
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            showNotification();
        else
            showNotificationOld();
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

    private void showNotificationOld() {
        Notification notification = new NotificationCompat.Builder(this)
                .setOngoing(true)
                .setWhen(0)
                .build();

        startForeground(RUNNING_NOTIFICATION, notification);
    }

    private void showNotification(){

        // Create a Notification channel as required for Android O and beyond.
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE);
        channel.setLightColor(Color.YELLOW);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                //.setSmallIcon(R.drawable.icon_cloud)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentTitle("Servalot running in the background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .build();
        startForeground(RUNNING_NOTIFICATION, notification);
    }
}
