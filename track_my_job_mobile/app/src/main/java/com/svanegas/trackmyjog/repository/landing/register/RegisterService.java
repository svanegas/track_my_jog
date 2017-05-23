package com.svanegas.trackmyjog.repository.landing.register;

import com.svanegas.trackmyjog.repository.landing.register.model.User;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterService {

    @POST("auths")
    @FormUrlEncoded
    Single<User> registerUser(@Field("name") String name,
                              @Field("email") String email,
                              @Field("password") String password);
}
