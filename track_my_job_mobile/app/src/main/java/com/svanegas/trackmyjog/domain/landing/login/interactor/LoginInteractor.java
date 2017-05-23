package com.svanegas.trackmyjog.domain.landing.login.interactor;

import com.svanegas.trackmyjog.repository.landing.AuthenticationRepository;
import com.svanegas.trackmyjog.repository.landing.RemoteAuthenticationRepository;
import com.svanegas.trackmyjog.repository.model.User;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class LoginInteractor {

    private AuthenticationRepository mRemoteRepository;

    @Inject
    public LoginInteractor(RemoteAuthenticationRepository remoteRepository) {
        mRemoteRepository = remoteRepository;
    }

    public Single<User> loginUser(String email, String password) {
        return mRemoteRepository.loginUser(email, password);
    }
}
