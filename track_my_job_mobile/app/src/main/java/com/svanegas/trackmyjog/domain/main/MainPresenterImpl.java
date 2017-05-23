package com.svanegas.trackmyjog.domain.main;

import com.svanegas.trackmyjog.R;
import com.svanegas.trackmyjog.TrackMyJogApplication;
import com.svanegas.trackmyjog.interactor.AuthenticationInteractor;
import com.svanegas.trackmyjog.util.PreferencesManager;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainPresenterImpl implements MainPresenter {

    private MainView mView;

    @Inject
    AuthenticationInteractor mAuthInteractor;

    @Inject
    PreferencesManager mPreferencesManager;

    MainPresenterImpl(MainView mainView) {
        mView = mainView;
        TrackMyJogApplication.getInstance().getApplicationComponent().inject(this);
    }

    @Override
    public boolean menuItemClicked(int itemId) {
        switch (itemId) {
            case R.id.nav_my_time_entries:
                mView.goToTimeEntriesList();
                return true;
            case R.id.nav_logout:
                mAuthInteractor.logoutUser()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doAfterTerminate(() -> {
                            mPreferencesManager.removeAuthHeaders();
                            mView.goToWelcome();
                        })
                        .subscribe();
                return true;
        }
        return false;
    }
}
