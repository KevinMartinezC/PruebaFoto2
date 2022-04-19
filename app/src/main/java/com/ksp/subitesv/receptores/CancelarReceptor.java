package com.ksp.subitesv.receptores;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ksp.subitesv.proveedores.ReservaClienteProveedor;

public class CancelarReceptor extends BroadcastReceiver {

    private ReservaClienteProveedor mReservaClienteProveedor;
    @Override
    public void onReceive(Context context, Intent intent) {

        String idCliente = intent.getExtras().getString("idCliente");
        mReservaClienteProveedor = new ReservaClienteProveedor();
        mReservaClienteProveedor.actualizarEstado(idCliente,"cancel");

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);
    }
}
