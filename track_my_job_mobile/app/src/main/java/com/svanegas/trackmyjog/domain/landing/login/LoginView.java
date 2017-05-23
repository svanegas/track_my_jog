package com.svanegas.trackmyjog.domain.landing.login;

interface LoginView {

    String email();

    String password();

    void showLoadingAndDisableFields();

    void hideLoadingAndEnableFields();

    void showEmptyEmailError();

    void showEmptyPasswordError();

    void showTimeoutError();

    void showNoConnectionError();

    void showDisplayableError(String errorMessage);

    void showUnknownError();
}
