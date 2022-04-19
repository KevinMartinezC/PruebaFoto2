package com.ksp.subitesv.retrofit;

import com.ksp.subitesv.modulos.FCMCuerpo;
import com.ksp.subitesv.modulos.FCMRespuesta;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAACiYc030:APA91bF60ukYiExtxYeQC3I_BWN1UMyKweeHDCmhIoXy80suU6z4YJWgaFESbgZt0CjcwuiWUTlH2KkuoK13Rf111nxjHrQAf2xFEmjvKn0eou87eFIBUXpuzIf5n87aoWLa42baB1Mk"
    })
    @POST("fcm/send")
    Call<FCMRespuesta> send(@Body FCMCuerpo body);
}
