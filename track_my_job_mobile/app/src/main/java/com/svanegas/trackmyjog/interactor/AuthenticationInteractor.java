package com.svanegas.trackmyjog.interactor;

import com.svanegas.trackmyjog.repository.landing.AuthenticationRepository;
import com.svanegas.trackmyjog.repository.landing.RemoteAuthenticationRepository;
import com.svanegas.trackmyjog.repository.model.User;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class AuthenticationInteractor {

    private AuthenticationRepository mRemoteRepository;

    @Inject
    public AuthenticationInteractor(RemoteAuthenticationRepository remoteRepository) {
        mRemoteRepository = remoteRepository;
    }

    public Single<User> loginUser(String email, String password) {
        return mRemoteRepository.loginUser(email, password);
    }

    public Single<User> registerUser(String name, String email, String password) {
        return mRemoteRepository.registerUser(name, email, password);
    }

    public Single<Object> logoutUser() {
        return mRemoteRepository.logoutUser();
    }
}
