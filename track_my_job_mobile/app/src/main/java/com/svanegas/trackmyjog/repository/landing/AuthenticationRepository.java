package com.svanegas.trackmyjog.repository.landing;

import com.svanegas.trackmyjog.repository.landing.register.model.User;

import io.reactivex.Single;

public interface AuthenticationRepository {

    Single<User> registerUser(String name, String email, String password);

    Single<User> loginUser(String email, String password);
}
