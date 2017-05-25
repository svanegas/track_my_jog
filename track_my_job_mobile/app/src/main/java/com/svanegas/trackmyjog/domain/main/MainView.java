package com.svanegas.trackmyjog.domain.main;

interface MainView {

    void goToTimeEntriesList();

    void goToWelcome();

    void populateHeaderName(String name);

    void populateHeaderRole(int roleResId);
}
