package com.svanegas.trackmyjog.repository.landing;

import com.svanegas.trackmyjog.repository.model.User;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class RemoteAuthenticationRepository implements AuthenticationRepository {

    private AuthenticationService mService;

    @Inject
    public RemoteAuthenticationRepository(AuthenticationService authenticationService) {
        mService = authenticationService;
    }

    @Override
    public Single<User> registerUser(String name, String email, String password) {
        return mService.registerUser(name, email, password);
    }

    @Override
    public Single<User> loginUser(String email, String password) {
        return mService.loginUser(email, password);
    }

    @Override
    public Single<Object> logoutUser() {
        return mService.logoutUser();
    }
}
