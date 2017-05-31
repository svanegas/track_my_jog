package com.svanegas.trackmyjog.domain.main.user.form;

import android.text.TextUtils;
import android.util.Log;

import com.svanegas.trackmyjog.TrackMyJogApplication;
import com.svanegas.trackmyjog.interactor.UserInteractor;
import com.svanegas.trackmyjog.repository.model.APIError;
import com.svanegas.trackmyjog.repository.model.User;
import com.svanegas.trackmyjog.util.PreferencesManager;

import java.net.SocketTimeoutException;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

import static android.support.v4.util.PatternsCompat.EMAIL_ADDRESS;
import static com.svanegas.trackmyjog.domain.landing.register.RegisterPresenterImpl.MINIMUM_PASSWORD_SIZE;
import static com.svanegas.trackmyjog.domain.main.user.form.UserFormFragment.ADMIN_INDEX;
import static com.svanegas.trackmyjog.domain.main.user.form.UserFormFragment.MANAGER_INDEX;
import static com.svanegas.trackmyjog.domain.main.user.form.UserFormFragment.REGULAR_INDEX;
import static com.svanegas.trackmyjog.network.ConnectionInterceptor.isInternetConnectionError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.isHttpError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.isUnauthorizedError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.parseHttpError;

public class UserFormPresenterImpl implements UserFormPresenter {

    private static final String TAG = UserFormPresenterImpl.class.getSimpleName();

    private UserFormView mView;

    @Inject
    UserInteractor mInteractor;

    @Inject
    PreferencesManager mPreferencesManager;

    @Inject
    CompositeDisposable mDisposables;

    UserFormPresenterImpl(UserFormView timeEntryFormView) {
        mView = timeEntryFormView;
        TrackMyJogApplication.getInstance().getApplicationComponent().inject(this);
    }

    /**
     * Validates entered information related to the user and performs the create or update
     * request.
     *
     * @param userId -1 if is a create request, id of the user to be updated otherwise.
     */
    public void submitUser(long userId) {
        mView.showLoadingAndDisableFields();
        String name = mView.name();
        String email = mView.email();
        String role = mView.role();
        String password = mView.password();
        String passwordConfirmation = mView.passwordConfirmation();

        boolean validFields = true;
        if (userId == -1) validFields = validatePasswords(password, passwordConfirmation);
        validFields &= validateEmail(email);
        validFields &= validateName(name);

        if (validFields) processUser(userId, name, email, role, password);
        else mView.hideLoadingAndEnableFields();
    }

    @Override
    public void fetchUser(long userId) {
        mView.showLoadingAndDisableFields();
        Disposable disposable = mInteractor.fetchUser(userId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    mView.hideLoadingAndEnableFields();
                    mView.populateName(user.getName());
                    mView.populateEmail(user.getEmail());
                    if (mPreferencesManager.getId() == user.getId()) mView.disableUserEdition();
                    if (user.isAdmin()) mView.populateRoleSpinner(ADMIN_INDEX);
                    else if (user.isManager()) mView.populateRoleSpinner(MANAGER_INDEX);
                    else mView.populateRoleSpinner(REGULAR_INDEX);
                }, throwable -> {
                    mView.hideLoadingAndEnableFields();
                    if (throwable instanceof SocketTimeoutException) {
                        mView.showTimeoutError();
                    } else if (isInternetConnectionError(throwable)) {
                        mView.showNoConnectionError();
                    } else if (isUnauthorizedError(throwable)) {
                        mView.goToWelcomeDueUnauthorized();
                    } else if (isHttpError(throwable)) {
                        APIError error = parseHttpError((HttpException) throwable);
                        if (error != null && error.getErrorMessage() != null) {
                            mView.showDisplayableError(error.errorMessage);
                        } else mView.showUnknownError();
                    } else {
                        Log.e(TAG, "Could not fetch User due unknown error: ",
                                throwable);
                        mView.showUnknownError();
                    }
                });
        mDisposables.add(disposable);
    }

    @Override
    public void deleteUser(long userId) {
        mView.showLoadingAndDisableFields();
        Disposable disposable = mInteractor.deleteUser(userId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trash -> mView.onDeletionSuccess(), throwable -> {
                    mView.hideLoadingAndEnableFields();
                    if (throwable instanceof SocketTimeoutException) {
                        mView.showTimeoutError();
                    } else if (isInternetConnectionError(throwable)) {
                        mView.showNoConnectionError();
                    } else if (isUnauthorizedError(throwable)) {
                        mView.goToWelcomeDueUnauthorized();
                    } else if (isHttpError(throwable)) {
                        APIError error = parseHttpError((HttpException) throwable);
                        if (error != null && error.getErrorMessage() != null) {
                            mView.showDisplayableError(error.errorMessage);
                        } else mView.showUnknownError();
                    } else {
                        Log.e(TAG, "Could not delete User due unknown error: ",
                                throwable);
                        mView.showUnknownError();
                    }
                });
        mDisposables.add(disposable);
    }

    @Override
    public void unsubscribe() {
        mDisposables.clear();
    }

    private void processUser(long userId, String name, String email, String role, String password) {
        boolean isUpdate;
        Single<User> single;
        if (userId != -1) {
            isUpdate = true;
            single = mInteractor.updateUser(userId, name, email, role);
        } else {
            isUpdate = false;
            single = mInteractor.createUser(name, email, role, password);
        }
        Disposable disposable = single.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    if (isUpdate) mView.onUpdateSuccess();
                    else mView.onCreationSuccess();
                }, throwable -> {
                    mView.hideLoadingAndEnableFields();
                    if (throwable instanceof SocketTimeoutException) {
                        mView.showTimeoutError();
                    } else if (isInternetConnectionError(throwable)) {
                        mView.showNoConnectionError();
                    } else if (isUnauthorizedError(throwable)) {
                        mView.goToWelcomeDueUnauthorized();
                    } else if (isHttpError(throwable)) {
                        APIError error = parseHttpError((HttpException) throwable);
                        if (error != null && error.getErrorMessage() != null) {
                            mView.showDisplayableError(error.errorMessage);
                        } else mView.showUnknownError();
                    } else {
                        Log.e(TAG, "Could not " + (isUpdate ? "update" : "create") +
                                        " User due to unknown error: ",
                                throwable);
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
