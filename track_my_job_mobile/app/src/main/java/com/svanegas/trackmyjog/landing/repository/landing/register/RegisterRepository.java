package com.svanegas.trackmyjog.landing.repository.landing.register;

import com.svanegas.trackmyjog.landing.repository.landing.register.model.User;

import io.reactivex.Single;

public interface RegisterRepository {

    Single<User> registerUser(String name, String email, String password);
}
