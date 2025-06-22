package com.dange.roomly;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("upload_pg.php")
    Call<Void> uploadPG(
            @Part("pg_name") RequestBody pgName,
            @Part("pg_type") RequestBody pgType,
            @Part("city") RequestBody city,
            @Part("description") RequestBody description,
            @Part("owner_name") RequestBody ownerName,
            @Part("owner_phone") RequestBody ownerPhone,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("rent") RequestBody rent, // Added missing rent parameter
            @Part MultipartBody.Part image1,
            @Part MultipartBody.Part image2,
            @Part MultipartBody.Part image3
    );
}