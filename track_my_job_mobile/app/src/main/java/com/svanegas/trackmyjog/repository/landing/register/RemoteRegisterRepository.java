package com.svanegas.trackmyjog.repository.landing.register;

import com.svanegas.trackmyjog.repository.landing.register.model.User;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class RemoteRegisterRepository implements RegisterRepository {

    private RegisterService mService;

    @Inject
    public RemoteRegisterRepository(RegisterService registerService) {
        mService = registerService;
    }

    @Override
    public Single<User> registerUser(String name, String email, String password) {
        return mService.registerUser(name, email, password);
    }
}
