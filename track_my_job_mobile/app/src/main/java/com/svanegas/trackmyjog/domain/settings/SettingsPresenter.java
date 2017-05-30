package com.svanegas.trackmyjog.domain.settings;

interface SettingsPresenter {

    void fetchAccountInfo();

    boolean changeNameRequested();

    boolean changeEmailRequested();

    boolean changePasswordRequested();
}
