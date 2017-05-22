package com.svanegas.trackmyjog.landing.register;

import android.text.TextUtils;

import com.svanegas.trackmyjog.landing.register.interactors.RegisterInteractor;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.support.v4.util.PatternsCompat.EMAIL_ADDRESS;

class RegisterPresenterImpl implements RegisterPresenter {

    private static final int MINIMUM_PASSWORD_SIZE = 8;

    private RegisterView mView;

    @Inject
    RegisterInteractor mRegisterInteractor;

    RegisterPresenterImpl(RegisterView registerView) {
        mView = registerView;
    }

    @Override
    public void validateRegistration() {
        mView.showLoadingAndDisableFields();
        String name = mView.name();
        String email = mView.email();
        String password = mView.password();
        String passwordConfirmation = mView.passwordConfirmation();

        boolean validFields = validatePasswords(password, passwordConfirmation);
        validFields &= validateEmail(email);
        validFields &= validateName(name);

        if (validFields) registerUser(name, email, password);
        else mView.hideLoadingAndEnableFields();
    }

    private void registerUser(String name, String email, String password) {
        mRegisterInteractor.registerUser(name, email, password)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    // TODO: Do something with the User
                }, throwable -> {
                    // TODO: Do something with the error.
                });
    }

    private boolean validateName(String name) {
        if (TextUtils.isEmpty(name)) {
            mView.showEmptyNameError();
            return false;
        }
        return true;
    }

    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            mView.showEmptyEmailError();
            return false;
        } else if (!EMAIL_ADDRESS.matcher(email).matches()) {
            mView.showInvalidEmailError();
            return false;
        }
        return true;
    }

    private boolean validatePasswords(String password, String passwordConfirmation) {
        if (TextUtils.isEmpty(password)) {
            mView.showEmptyPasswordError();
            return false;
        } else if (password.length() < MINIMUM_PASSWORD_SIZE) {
            mView.showShortPasswordError();
            return false;
        } else if (!password.equals(passwordConfirmation)) {
            mView.showPasswordsDontMatchError();
            return false;
        }
        return true;
    }
}
