package com.svanegas.trackmyjog.domain.settings;

import com.svanegas.trackmyjog.TrackMyJogApplication;
import com.svanegas.trackmyjog.util.PreferencesManager;

import javax.inject.Inject;

public class SettingsPresenterImpl implements SettingsPresenter {

    private SettingsView mView;

    @Inject
    PreferencesManager mPreferencesManager;

    public SettingsPresenterImpl(SettingsView settingsView) {
        mView = settingsView;
        TrackMyJogApplication.getInstance().getApplicationComponent().inject(this);
    }

    @Override
    public void fetchAccountInfo() {
        mView.populateName(mPreferencesManager.getName());
        mView.populateEmail(mPreferencesManager.getEmail());
    }

    @Override
    public boolean changeNameRequested() {
        mView.showNotImplementedYetMessage();
        return true;
    }

    @Override
    public boolean changeEmailRequested() {
        mView.showNotImplementedYetMessage();
        return true;
    }

    @Override
    public boolean changePasswordRequested() {
        mView.showNotImplementedYetMessage();
        return true;
    }
}
