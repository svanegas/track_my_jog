package com.svanegas.trackmyjog.interactor;

import com.svanegas.trackmyjog.repository.main.RemoteUserRepository;
import com.svanegas.trackmyjog.repository.main.UserRepository;
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
}
