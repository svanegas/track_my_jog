package com.svanegas.trackmyjog.repository.landing;

import com.svanegas.trackmyjog.repository.model.User;

import io.reactivex.Single;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthenticationService {

    @POST("auths")
    @FormUrlEncoded
    Single<User> registerUser(@Field("name") String name,
                              @Field("email") String email,
                              @Field("password") String password);

    @POST("auths/sign_in")
    @FormUrlEncoded
    Single<User> loginUser(@Field("email") String email,
                           @Field("password") String password);

    @DELETE("auths/sign_out")
    Single<Object> logoutUser();
}
