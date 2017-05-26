package com.svanegas.trackmyjog.domain.main.user.list;

import com.svanegas.trackmyjog.repository.model.User;

import java.util.List;

public interface UsersListPresenter {

    void userClicked(User user);

    void fetchUsers(boolean pulledToRefresh);

    void sortUsers(int sortOption, List<User> users);

    String setupNameText(User user);

    int setupRoleTextResId(User user);

    int setupProfileIndicatorVisibility(User user);
}
