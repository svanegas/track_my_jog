package com.svanegas.trackmyjog.domain.main.user.form;

interface UserFormView {

    String name();

    String email();

    String password();

    String passwordConfirmation();

    String role();

    void showLoadingAndDisableFields();

    void hideLoadingAndEnableFields();

    void onCreationSuccess();

    void onUpdateSuccess();

    void onDeletionSuccess();

    void populateName(String name);

    void populateEmail(String email);

    void populateRoleSpinner(int adminIndex);

    void showEmptyNameError();

    void showEmptyEmailError();

    void showInvalidEmailError();

    void showEmptyPasswordError();

    void showShortPasswordError();

    void showPasswordsDontMatchError();

    void showTimeoutError();

    void showNoConnectionError();

    void showDisplayableError(String errorMessage);

    void showUnknownError();
}
