package com.svanegas.trackmyjog.landing.welcome;

class WelcomePresenterImpl implements WelcomePresenter {

    private WelcomeView mView;

    WelcomePresenterImpl(WelcomeView welcomeView) {
        mView = welcomeView;
    }

    @Override
    public void registerButtonClicked() {
        mView.goToRegister();
    }

    @Override
    public void loginButtonClicked() {
        mView.goToLogin();
    }
}
