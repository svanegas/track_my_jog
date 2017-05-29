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
            case R.id.nav_time_entries_list:
                mView.goToTimeEntriesList();
                return true;
            case R.id.nav_users_list:
                mView.goToUsersList();
                return true;
            case R.id.nav_report:
                mView.goToReport();
                return true;
            case R.id.nav_logout:
                mAuthInteractor.logoutUser()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doAfterTerminate(() -> {
                            mPreferencesManager.removeUserInfo();
                            mPreferencesManager.removeAuthHeaders();
                            mView.goToWelcome();
                        })
                        .onErrorReturnItem(new Object()) // Ignore error
                        .subscribe();
                return true;
        }
        return false;
    }

    @Override
    public void requestNavigationViewPopulation() {
        mView.populateHeaderName(mPreferencesManager.getName());
        int displayableRoleResId, menuResId;
        if (mPreferencesManager.isAdmin()) {
            displayableRoleResId = R.string.main_admin_role;
            menuResId = R.menu.main_activity_drawer;
        }
        else if (mPreferencesManager.isManager()) {
            displayableRoleResId = R.string.main_manager_role;
            menuResId = R.menu.main_activity_drawer;
        }
        else {
            displayableRoleResId = R.string.main_regular_role;
            menuResId = R.menu.main_activity_drawer_no_users;
        }
        mView.populateHeaderRole(displayableRoleResId);
        mView.populateNavigationViewItems(menuResId);
    }
}
