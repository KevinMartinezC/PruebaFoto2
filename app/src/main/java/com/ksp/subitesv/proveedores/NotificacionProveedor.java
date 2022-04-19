package com.ksp.subitesv.proveedores;

import com.ksp.subitesv.modulos.FCMCuerpo;
import com.ksp.subitesv.modulos.FCMRespuesta;
import com.ksp.subitesv.retrofit.IFCMApi;
import com.ksp.subitesv.retrofit.RetrofitCliente;

import retrofit2.Call;

public class NotificacionProveedor {

    private String url = "https://fcm.googleapis.com";

    public NotificacionProveedor() {

    }
    public Call<FCMRespuesta> enviarNotificacion(FCMCuerpo body){
        return RetrofitCliente.getClienteObjeto(url).create(IFCMApi.class).send(body);
    }
}
