package com.svanegas.trackmyjog.repository.main;

import com.svanegas.trackmyjog.repository.model.User;

import java.util.List;

import io.reactivex.Single;

public interface UserRepository {

    Single<List<User>> fetchUsers();
}
