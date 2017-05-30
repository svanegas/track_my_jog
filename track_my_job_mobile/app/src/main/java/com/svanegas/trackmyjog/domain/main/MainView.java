package com.svanegas.trackmyjog.domain.main;

interface MainView {

    void goToTimeEntriesList();

    void goToUsersList();

    void goToReport();

    void goToSettings();

    void goToWelcome();

    void populateHeaderName(String name);

    void populateHeaderRole(int roleResId);

    void populateNavigationViewItems(int menuResId);
}
