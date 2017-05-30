package com.svanegas.trackmyjog.domain.main.user.list;

import com.svanegas.trackmyjog.repository.model.User;

import java.util.List;

interface UsersListView {

    void onUserClicked(User user);

    void showLoading(boolean pulledToRefresh);

    void hideLoading(boolean pulledToRefresh);

    void populateUsers(List<User> users);

    void showTimeoutError();

    void showNoConnectionError();

    void goToWelcomeDueUnauthorized();

    void showDisplayableError(String errorMessage);

    void showUnknownError();
}
