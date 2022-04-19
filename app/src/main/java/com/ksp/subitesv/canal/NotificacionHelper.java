package com.ksp.subitesv.canal;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.ksp.subitesv.R;

public class NotificacionHelper extends ContextWrapper {

    private static final String CANAL_ID = "com.ksp.subitesv";
    private static final String CANAL_NAME = "UberClone";

    private NotificationManager manager;


    public NotificacionHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CrearCanales();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void CrearCanales() {
        NotificationChannel notificationChannel = new NotificationChannel(
                CANAL_ID,
                CANAL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.GRAY);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        obtenerManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager obtenerManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        }
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder obtenerNotificacion(String title, String body, PendingIntent intent, Uri sonioUri) {
        return new Notification.Builder(getApplicationContext(), CANAL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(sonioUri)
                .setContentIntent(intent)
                .setSmallIcon(R.drawable.ic_car)
                .setStyle(new Notification.BigTextStyle().bigText(body).setBigContentTitle(title));
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder obtenerNotificacionAcciones(String title,
                                                            String body,
                                                            Uri sonioUri,
                                                            Notification.Action aceptarAccion,
                                                            Notification.Action cancelarAccion) {
        return new Notification.Builder(getApplicationContext(), CANAL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(sonioUri)
                .setSmallIcon(R.drawable.ic_car)
                .addAction(aceptarAccion)
                .addAction(cancelarAccion)
                .setStyle(new Notification.BigTextStyle().bigText(body).setBigContentTitle(title));
    }

    public NotificationCompat.Builder obtenerNotificacionOldAPI(String title, String body, PendingIntent intent, Uri sonioUri) {
        return new NotificationCompat.Builder(getApplicationContext(), CANAL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(sonioUri)
                .setContentIntent(intent)
                .setSmallIcon(R.drawable.ic_car)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));
    }
    public NotificationCompat.Builder obtenerNotificacionOldAPIAccciones(String title,
                                                                         String body,
                                                                         Uri sonioUri,
                                                                         NotificationCompat.Action aceptarAccion,
                                                                         NotificationCompat.Action cancelarAccion) {
        return new NotificationCompat.Builder(getApplicationContext(), CANAL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(sonioUri)
                .setSmallIcon(R.drawable.ic_car)
                .addAction(aceptarAccion)
                .addAction(cancelarAccion)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));
    }
}
