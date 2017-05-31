package com.svanegas.trackmyjog.domain.main.user.list;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;

import com.svanegas.trackmyjog.R;
import com.svanegas.trackmyjog.TrackMyJogApplication;
import com.svanegas.trackmyjog.interactor.UserInteractor;
import com.svanegas.trackmyjog.repository.model.APIError;
import com.svanegas.trackmyjog.repository.model.User;
import com.svanegas.trackmyjog.util.PreferencesManager;

import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

import static com.svanegas.trackmyjog.network.ConnectionInterceptor.isInternetConnectionError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.isHttpError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.isUnauthorizedError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.parseHttpError;

public class UsersListPresenterImpl implements UsersListPresenter {

    private static final String TAG = UsersListPresenterImpl.class.getSimpleName();

    // Indexes of sort
    public static final int NAME_SORT_INDEX = 0;
    public static final int ROLE_SORT_INDEX = 1;

    private UsersListView mView;

    @Inject
    Context mContext;

    @Inject
    UserInteractor mInteractor;

    @Inject
    PreferencesManager mPreferencesManager;

    @Inject
    CompositeDisposable mDisposables;

    @Inject
    Random mRandom;

    UsersListPresenterImpl(UsersListView usersListView) {
        mView = usersListView;
        TrackMyJogApplication.getInstance().getApplicationComponent().inject(this);
    }

    @Override
    public void userClicked(User user) {
        mView.onUserClicked(user);
    }

    @Override
    public void fetchUsers(boolean pulledToRefresh) {
        mView.showLoading(pulledToRefresh);
        Disposable disposable = mInteractor.fetchUsers()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(users -> {
                    mView.hideLoading(pulledToRefresh);
                    mView.populateUsers(users);
                }, throwable -> {
                    mView.hideLoading(pulledToRefresh);
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
                        Log.e(TAG, "Could not fetch users due unknown error: ", throwable);
                        mView.showUnknownError();
                    }
                });
        mDisposables.add(disposable);
    }

    @Override
    public void sortUsers(int sortOption, List<User> users) {
        if (sortOption == NAME_SORT_INDEX) {
            Collections.sort(users, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        } else if (sortOption == ROLE_SORT_INDEX) {
            Collections.sort(users, (o1, o2) -> o1.getRole().compareTo(o2.getRole()));
        }
    }

    @Override
    public String setupAvatarText(User user) {
        return user.getName().substring(0, 1);
    }

    @Override
    public void setupRandomAvatarColor(GradientDrawable gradientDrawable) {
        gradientDrawable.setColor(generateRandomColor());
    }

    @Override
    public String setupNameText(User user) {
        return user.getName();
    }

    @Override
    public int setupRoleTextResId(User user) {
        if (user.isAdmin()) return R.string.main_admin_role;
        else if (user.isManager()) return R.string.main_manager_role;
        else return R.string.main_regular_role;
    }

    @Override
    public int setupProfileIndicatorVisibility(User user) {
        return mPreferencesManager.getId() == user.getId() ? View.VISIBLE : View.GONE;
    }

    @Override
    public void unsubscribe() {
        mDisposables.clear();
    }

    private int generateRandomColor() {
        // This is the base color which will be mixed with the generated one
        final int baseColor = Color.LTGRAY;

        final int baseRed = Color.red(baseColor);
        final int baseGreen = Color.green(baseColor);
        final int baseBlue = Color.blue(baseColor);

        final int red = (baseRed + mRandom.nextInt(256)) / 2;
        final int green = (baseGreen + mRandom.nextInt(256)) / 2;
        final int blue = (baseBlue + mRandom.nextInt(256)) / 2;

        return Color.rgb(red, green, blue);
    }
}
