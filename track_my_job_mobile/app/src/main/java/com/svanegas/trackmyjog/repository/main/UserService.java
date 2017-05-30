package com.svanegas.trackmyjog.repository.main;

import com.svanegas.trackmyjog.repository.model.User;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {

    @GET("users")
    Single<List<User>> fetchUsers();

    @GET("users/{userId}")
    Single<User> fetchUser(@Path("userId") long userId);

    @POST("users")
    @FormUrlEncoded
    Single<User> createUser(@Field("name") String name,
                            @Field("email") String email,
                            @Field("role") String role,
                            @Field("password") String password);

    @PATCH("users/{userId}")
    @FormUrlEncoded
    Single<User> updateUser(@Path("userId") long userId,
                            @Field("name") String name,
                            @Field("email") String email,
                            @Field("role") String role);

    @DELETE("users/{userId}")
    Single<Object> deleteUser(@Path("userId") long userId);
}
