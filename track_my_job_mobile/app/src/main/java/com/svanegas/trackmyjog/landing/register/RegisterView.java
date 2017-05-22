package com.svanegas.trackmyjog.landing.register;

interface RegisterView {

    String name();

    String email();

    String password();

    String passwordConfirmation();

    void showLoadingAndDisableFields();

    void hideLoadingAndEnableFields();

    void showEmptyNameError();

    void showEmptyEmailError();

    void showInvalidEmailError();

    void showEmptyPasswordError();

    void showShortPasswordError();

    void showPasswordsDontMatchError();
}
