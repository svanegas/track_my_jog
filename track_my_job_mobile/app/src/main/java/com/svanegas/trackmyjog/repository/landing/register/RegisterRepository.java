package com.svanegas.trackmyjog.repository.landing.register;

import com.svanegas.trackmyjog.repository.landing.register.model.User;

import io.reactivex.Single;

public interface RegisterRepository {

    Single<User> registerUser(String name, String email, String password);
}
