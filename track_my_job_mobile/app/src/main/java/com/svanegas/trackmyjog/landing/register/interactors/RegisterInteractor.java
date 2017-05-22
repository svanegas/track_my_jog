package com.svanegas.trackmyjog.landing.register.interactors;

import com.svanegas.trackmyjog.landing.repository.landing.register.RegisterRepository;
import com.svanegas.trackmyjog.landing.repository.landing.register.model.User;

import javax.inject.Inject;

import io.reactivex.Single;

public class RegisterInteractor {

    RegisterRepository mRemoteRepository;

    @Inject
    public RegisterInteractor(RegisterRepository remoteRepository) {
        mRemoteRepository = remoteRepository;
    }

    public Single<User> registerUser(String name, String email, String password) {
        return mRemoteRepository.registerUser(name, email, password);
    }
}
