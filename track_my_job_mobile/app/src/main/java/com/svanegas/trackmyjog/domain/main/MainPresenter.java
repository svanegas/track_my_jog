package com.svanegas.trackmyjog.domain.main;

interface MainPresenter {

    boolean menuItemClicked(int itemId);

    void requestNavigationViewPopulation();

    void clearPreferences();
}
