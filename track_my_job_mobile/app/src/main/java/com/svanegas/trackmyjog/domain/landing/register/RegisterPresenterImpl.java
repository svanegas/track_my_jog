package com.svanegas.trackmyjog.domain.landing.register;

import android.text.TextUtils;
import android.util.Log;

import com.svanegas.trackmyjog.TrackMyJogApplication;
import com.svanegas.trackmyjog.interactor.AuthenticationInteractor;
import com.svanegas.trackmyjog.repository.model.APIError;
import com.svanegas.trackmyjog.util.PreferencesManager;

import java.net.SocketTimeoutException;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

import static android.support.v4.util.PatternsCompat.EMAIL_ADDRESS;
import static com.svanegas.trackmyjog.network.ConnectionInterceptor.isInternetConnectionError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.isHttpError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.parseHttpError;

public class RegisterPresenterImpl implements RegisterPresenter {

    private static final String TAG = RegisterPresenterImpl.class.getSimpleName();
    public static final int MINIMUM_PASSWORD_SIZE = 8;

    private RegisterView mView;

    @Inject
    AuthenticationInteractor mAutheInteractor;

    @Inject
    PreferencesManager mPreferencesManager;

    @Inject
    CompositeDisposable mDisposables;

    RegisterPresenterImpl(RegisterView registerView) {
        mView = registerView;
        TrackMyJogApplication.getInstance().getApplicationComponent().inject(this);
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

    @Override
    public void unsubscribe() {
        mDisposables.clear();
    }

    private void registerUser(String name, String email, String password) {
        Disposable disposable = mAutheInteractor.registerUser(name, email, password)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    mPreferencesManager.saveUserInfo(user);
                    mView.onRegisterSuccess();
                }, throwable -> {
                    mView.hideLoadingAndEnableFields();
                    if (throwable instanceof SocketTimeoutException) {
                        mView.showTimeoutError();
                    } else if (isInternetConnectionError(throwable)) {
                        mView.showNoConnectionError();
                    } else if (isHttpError(throwable)) {
                        APIError error = parseHttpError((HttpException) throwable);
                        if (error != null && error.getErrorMessage() != null) {
                            mView.showDisplayableError(error.errorMessage);
                        } else mView.showUnknownError();
                    } else {
                        Log.e(TAG, "Could not register due unknown error: ", throwable);
                        mView.showUnknownError();
                    }
                });
        mDisposables.add(disposable);
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
