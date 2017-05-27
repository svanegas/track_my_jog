package com.svanegas.trackmyjog.repository.main;

import com.svanegas.trackmyjog.repository.model.User;

import java.util.List;

import io.reactivex.Single;

public interface UserRepository {

    Single<List<User>> fetchUsers();

    Single<User> fetchUser(long userId);

    Single<User> createUser(String name, String email, String role, String password);

    Single<User> updateUser(long userId, String name, String email, String role);

    Single<Object> deleteUser(long userId);
}
