package com.svanegas.trackmyjog.domain.main.user.form;

interface UserFormPresenter {

    void submitUser(long userId);

    void fetchUser(long userId);

    void deleteUser(long userId);
}
