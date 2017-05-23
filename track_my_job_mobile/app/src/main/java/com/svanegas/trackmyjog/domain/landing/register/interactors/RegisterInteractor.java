package com.svanegas.trackmyjog.domain.landing.register.interactors;

import com.svanegas.trackmyjog.repository.landing.register.RegisterRepository;
import com.svanegas.trackmyjog.repository.landing.register.RemoteRegisterRepository;
import com.svanegas.trackmyjog.repository.landing.register.model.User;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class RegisterInteractor {

    RegisterRepository mRemoteRepository;

    @Inject
    public RegisterInteractor(RemoteRegisterRepository remoteRepository) {
        mRemoteRepository = remoteRepository;
    }

    public Single<User> registerUser(String name, String email, String password) {
        return mRemoteRepository.registerUser(name, email, password);
    }
}
