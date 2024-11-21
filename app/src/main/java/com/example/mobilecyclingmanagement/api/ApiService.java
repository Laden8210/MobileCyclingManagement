package com.example.mobilecyclingmanagement.api;

import com.example.mobilecyclingmanagement.model.ApiResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("index.php")
    Call<ApiResponse> uploadImage(
            @Part MultipartBody.Part image
    );
}
