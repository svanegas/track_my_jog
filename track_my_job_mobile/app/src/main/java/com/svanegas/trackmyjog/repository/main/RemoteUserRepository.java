package com.svanegas.trackmyjog.repository.main;

import com.svanegas.trackmyjog.repository.model.User;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class RemoteUserRepository implements UserRepository {

    private UserService mService;

    @Inject
    public RemoteUserRepository(UserService userService) {
        mService = userService;
    }

    @Override
    public Single<List<User>> fetchUsers() {
        return mService.fetchUsers();
    }
}
