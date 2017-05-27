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

    @Override
    public Single<User> fetchUser(long userId) {
        return mService.fetchUser(userId);
    }

    @Override
    public Single<User> createUser(String name, String email, String role, String password) {
        return mService.createUser(name, email, role, password);
    }

    @Override
    public Single<User> updateUser(long userId, String name, String email, String role) {
        return mService.updateUser(userId, name, email, role);
    }

    @Override
    public Single<Object> deleteUser(long userId) {
        return mService.deleteUser(userId);
    }
}
