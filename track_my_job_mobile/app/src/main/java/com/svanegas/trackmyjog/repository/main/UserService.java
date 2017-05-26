package com.svanegas.trackmyjog.repository.main;

import com.svanegas.trackmyjog.repository.model.User;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface UserService {

    @GET("users")
    Single<List<User>> fetchUsers();
}
