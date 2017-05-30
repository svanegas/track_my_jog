package com.svanegas.trackmyjog.interactor;

import com.svanegas.trackmyjog.repository.main.RemoteUserRepository;
import com.svanegas.trackmyjog.repository.main.UserRepository;
import com.svanegas.trackmyjog.repository.model.TimeEntry;
import com.svanegas.trackmyjog.repository.model.User;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class UserInteractor {

    private UserRepository mRemoteRepository;

    @Inject
    public UserInteractor(RemoteUserRepository remoteRepository) {
        mRemoteRepository = remoteRepository;
    }


    public Single<List<User>> fetchUsers() {
        return mRemoteRepository.fetchUsers();
    }

    public Single<User> fetchUser(long userId) {
        return mRemoteRepository.fetchUser(userId);
    }

    public Single<User> createUser(String name, String email, String role, String password) {
        return mRemoteRepository.createUser(name, email, role, password);
    }

    public Single<User> updateUser(long userId, String name, String email, String role) {
        return mRemoteRepository.updateUser(userId, name, email, role);
    }

    public Single<Object> deleteUser(long userId) {
        return mRemoteRepository.deleteUser(userId);
    }
}
