package com.ksp.subitesv.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface GoogleApi {
    @GET
    Call<String> getDirections(@Url String url);
}
