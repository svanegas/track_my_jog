package com.svanegas.trackmyjog.domain.landing;

import com.svanegas.trackmyjog.TrackMyJogApplication;
import com.svanegas.trackmyjog.util.PreferencesManager;

import javax.inject.Inject;

public class LandingPresenterImpl implements LandingPresenter {

    private LandingView mView;

    @Inject
    PreferencesManager mPreferencesManager;

    LandingPresenterImpl(LandingView landingView) {
        mView = landingView;
        TrackMyJogApplication.getInstance().getApplicationComponent().inject(this);
    }

    @Override
    public void checkIfLogged() {
        if (mPreferencesManager.isLoggedIn()) {
            mView.goToMainScreen();
        }
    }
}
