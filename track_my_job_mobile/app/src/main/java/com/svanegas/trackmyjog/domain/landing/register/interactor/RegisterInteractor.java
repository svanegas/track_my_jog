package com.svanegas.trackmyjog.domain.landing.register.interactor;

import com.svanegas.trackmyjog.repository.landing.AuthenticationRepository;
import com.svanegas.trackmyjog.repository.landing.RemoteAuthenticationRepository;
import com.svanegas.trackmyjog.repository.landing.register.model.User;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class RegisterInteractor {

    private AuthenticationRepository mRemoteRepository;

    @Inject
    public RegisterInteractor(RemoteAuthenticationRepository remoteRepository) {
        mRemoteRepository = remoteRepository;
    }

    public Single<User> registerUser(String name, String email, String password) {
        return mRemoteRepository.registerUser(name, email, password);
    }
}
