package com.ksp.subitesv.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ksp.subitesv.R;
import com.ksp.subitesv.actividades.conductor.NotificacionReservaActivity;
import com.ksp.subitesv.canal.NotificacionHelper;
import com.ksp.subitesv.receptores.AceptarReceptores;
import com.ksp.subitesv.receptores.CancelarReceptor;

import java.util.Map;

public class MensajeriaClienteFirebase extends FirebaseMessagingService {

    private static final int NOTIFICATION_CODE = 100;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        RemoteMessage.Notification notificacion = message.getNotification();
        Map<String, String> data = message.getData();
        String title = data.get("title");
        String body = data.get("body");

        if (title != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (title.contains("SOLICITUD DE SERVICIO")) {
                    String idCliente = data.get("idCliente");
                    String origen = data.get("origin");
                    String destino = data.get("destination");
                    String min = data.get("min");
                    String distancia = data.get("distance");
                    showNotificacionApiOreoAcciones(title, body, idCliente);
                    showNotificationActivity(idCliente, origen, destino, min, distancia);
                } else if (title.contains("VIAJE CANCELADO")) {
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(2);
                    showNotificacionApiOreo(title, body);
                } else {
                    showNotificacionApiOreo(title, body);
                }
            } else {
                if (title.contains("SOLICITUD DE SERVICIO")) {
                    String idCliente = data.get("idCliente");
                    String origen = data.get("origin");
                    String destino = data.get("destination");
                    String min = data.get("min");
                    String distancia = data.get("distance");
                    showNotificacionAccion(title, body, idCliente);
                    showNotificationActivity(idCliente, origen, destino, min, distancia);
                }
                else if (title.contains("VIAJE CANCELADO")) {
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(2);
                    showNotificacion(title, body);
                }

                else {
                    showNotificacion(title, body);
                }

            }
        }
    }

    private void showNotificationActivity(String idCliente, String origen, String destino, String min, String distancia) {
        PowerManager pm = (PowerManager) getBaseContext().getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (!isScreenOn) {
            PowerManager.WakeLock wakeLock = pm.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK |
                            PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.ON_AFTER_RELEASE,
                    "AppName:MyLock"
            );
            wakeLock.acquire(10000);
        }
        Intent intent = new Intent(getBaseContext(), NotificacionReservaActivity.class);
        intent.putExtra("idCliente", idCliente);
        intent.putExtra("origin", origen);
        intent.putExtra("destination", destino);
        intent.putExtra("min", min);
        intent.putExtra("distance", distancia);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showNotificacion(String title, String body) {
        PendingIntent intent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificacionHelper notificacionHelper = new NotificacionHelper(getBaseContext());
        NotificationCompat.Builder builder = notificacionHelper.obtenerNotificacionOldAPI(title, body, intent, sonido);
        notificacionHelper.obtenerManager().notify(1, builder.build());
    }

    private void showNotificacionAccion(String title, String body, String idCliente) {

        //Acceptar
        Intent acceptarIntent = new Intent(this, AceptarReceptores.class);
        acceptarIntent.putExtra("idCliente", idCliente);
        PendingIntent aceptarPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, acceptarIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action aceptarAccion = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Aceptar",
                aceptarPendingIntent
        ).build();

        //Cancelar
        Intent cancelarIntent = new Intent(this, CancelarReceptor.class);
        cancelarIntent.putExtra("idCliente", idCliente);
        PendingIntent cancelarPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, cancelarIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action cancelarAccion = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Cancelar",
                cancelarPendingIntent
        ).build();

        //PendingIntent intent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificacionHelper notificacionHelper = new NotificacionHelper(getBaseContext());
        NotificationCompat.Builder builder = notificacionHelper.obtenerNotificacionOldAPIAccciones(title, body, sonido, aceptarAccion, cancelarAccion);
        notificacionHelper.obtenerManager().notify(2, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotificacionApiOreo(String title, String body) {
        PendingIntent intent = PendingIntent.getBroadcast(getBaseContext(), 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT |
                PendingIntent.FLAG_IMMUTABLE);
        //PendingIntent intent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificacionHelper notificacionHelper = new NotificacionHelper(getBaseContext());
        Notification.Builder builder = notificacionHelper.obtenerNotificacion(title, body, intent, sonido);
        notificacionHelper.obtenerManager().notify(1, builder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotificacionApiOreoAcciones(String title, String body, String idCliente) {
        Intent acceptarIntent = new Intent(this, AceptarReceptores.class);
        acceptarIntent.putExtra("idCliente", idCliente);
        PendingIntent aceptarPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, acceptarIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification.Action aceptarAccion = new Notification.Action.Builder(
                R.mipmap.ic_launcher,
                "Aceptar",
                aceptarPendingIntent
        ).build();

        Intent cancelarIntent = new Intent(this, CancelarReceptor.class);
        cancelarIntent.putExtra("idCliente", idCliente);
        PendingIntent cancelarPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, cancelarIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification.Action cancelarAccion = new Notification.Action.Builder(
                R.mipmap.ic_launcher,
                "Cancelar",
                cancelarPendingIntent
        ).build();
        //PendingIntent intent= PendingIntent.getBroadcast(getBaseContext(), 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT |PendingIntent.FLAG_IMMUTABLE);
        //PendingIntent intent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificacionHelper notificacionHelper = new NotificacionHelper(getBaseContext());
        Notification.Builder builder = notificacionHelper.obtenerNotificacionAcciones(title, body, sonido, aceptarAccion, cancelarAccion);
        notificacionHelper.obtenerManager().notify(2, builder.build());

    }
}
