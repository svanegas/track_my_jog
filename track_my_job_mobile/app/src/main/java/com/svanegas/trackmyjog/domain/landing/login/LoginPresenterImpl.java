package com.svanegas.trackmyjog.domain.landing.login;

import android.text.TextUtils;
import android.util.Log;

import com.svanegas.trackmyjog.TrackMyJogApplication;
import com.svanegas.trackmyjog.domain.landing.login.interactor.LoginInteractor;
import com.svanegas.trackmyjog.repository.model.APIError;

import java.net.SocketTimeoutException;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

import static com.svanegas.trackmyjog.network.ConnectionInterceptor.isInternetConnectionError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.isHttpError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.parseHttpError;

public class LoginPresenterImpl implements LoginPresenter {

    private static final String TAG = LoginPresenterImpl.class.getSimpleName();

    private LoginView mView;

    @Inject
    LoginInteractor mLoginInteractor;

    LoginPresenterImpl(LoginView loginView) {
        mView = loginView;
        TrackMyJogApplication.getInstance().getApplicationComponent().inject(this);
    }

    @Override
    public void validateLogin() {
        mView.showLoadingAndDisableFields();
        String email = mView.email();
        String password = mView.password();

        boolean validFields = validatePassword(password);
        validFields &= validateEmail(email);

        if (validFields) loginUser(email, password);
        else mView.hideLoadingAndEnableFields();
    }

    private void loginUser(String email, String password) {
        mLoginInteractor.loginUser(email, password)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> mView.hideLoadingAndEnableFields(),
                        throwable -> {
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
                                Log.e(TAG, "Could not login due unknown error: ", throwable);
                                mView.showUnknownError();
                            }
                        });
    }

    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            mView.showEmptyEmailError();
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            mView.showEmptyPasswordError();
            return false;
        }
        return true;
    }
}
