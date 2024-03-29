package com.svanegas.trackmyjog.domain.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.svanegas.trackmyjog.R;
import com.svanegas.trackmyjog.domain.landing.LandingActivity;
import com.svanegas.trackmyjog.domain.main.report.ReportFragment;
import com.svanegas.trackmyjog.domain.main.time_entry.form.TimeEntryFormFormFragment;
import com.svanegas.trackmyjog.domain.main.time_entry.list.TimeEntriesListFragment;
import com.svanegas.trackmyjog.domain.main.user.form.UserFormFragment;
import com.svanegas.trackmyjog.domain.main.user.list.UsersListFragment;
import com.svanegas.trackmyjog.domain.settings.SettingsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.svanegas.trackmyjog.domain.landing.LandingActivity.UNAUTHORIZED_TAG;

public class MainActivity extends AppCompatActivity implements MainView,
        NavigationView.OnNavigationItemSelectedListener,
        TimeEntriesListFragment.OnTimeEntriesListInteractionListener,
        UsersListFragment.OnUsersListInteractionListener,
        TimeEntryFormFormFragment.OnTimeEntryFormListener,
        UserFormFragment.OnUserFormListener,
        AdapterView.OnItemSelectedListener,
        ReportFragment.OnReportListener,
        SettingsFragment.OnSettingsListener {

    @BindView(R.id.drawer_layout)
    ViewGroup rootView;

    private MainPresenter mPresenter;
    private ViewHolder mViewHolder;
    private FragmentManager mFragmentManager;
    private int mDrawerSelectedId;
    private int mToolbarSpinnerSelectedPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mPresenter = new MainPresenterImpl(this);
        mToolbarSpinnerSelectedPos = -1;

        ButterKnife.bind(this);
        mViewHolder = new ViewHolder(rootView);

        mFragmentManager = getSupportFragmentManager();

        setupToolbar();
        setupDrawerLayout();
        setupNavigationView();
        setupInitialSelection();
    }

    @Override
    public void onBackPressed() {
        if (mViewHolder != null && mViewHolder.drawerLayout != null &&
                mViewHolder.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mViewHolder.drawerLayout.closeDrawer(GravityCompat.START);
        } else super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mPresenter.menuItemClicked(item.getItemId()) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id != mDrawerSelectedId) {
            mPresenter.menuItemClicked(item.getItemId());
            mDrawerSelectedId = id;
        }
        mViewHolder.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.add_button)
    public void onFabClicked() {
        Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment instanceof OnFabFragmentListener) {
            ((OnFabFragmentListener) fragment).onFabClicked();
        }
    }

    @Override
    public void onActivityTitleRequested(int titleResId) {
        setTitle(titleResId);
        if (mViewHolder.spinner != null) mViewHolder.spinner.setVisibility(View.GONE);
    }

    @Override
    public void onActivityTitleSpinnerRequested() {
        setTitle("");
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_entries_list_spinner_records_options,
                R.layout.main_toolbar_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mViewHolder.spinner.setAdapter(adapter);
        if (mToolbarSpinnerSelectedPos != -1)
            mViewHolder.spinner.setSelection(mToolbarSpinnerSelectedPos);
        mViewHolder.spinner.setOnItemSelectedListener(this);
        mViewHolder.spinner.setVisibility(View.VISIBLE);
    }

    @Override
    public void onShowFabRequested() {
        mViewHolder.fab.show();
    }

    @Override
    public void onHideFabRequested() {
        mViewHolder.fab.hide();
    }

    @Override
    public void onAddTimeEntryRequested() {
        TimeEntryFormFormFragment fragment = TimeEntryFormFormFragment.newInstance();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onUpdateTimeEntryRequested(long timeEntryId) {
        TimeEntryFormFormFragment fragment = TimeEntryFormFormFragment.newInstance(timeEntryId);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void goToTimeEntriesList() {
        mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        TimeEntriesListFragment fragment = TimeEntriesListFragment.newInstance();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onAddUserRequested() {
        UserFormFragment fragment = UserFormFragment.newInstance();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onUpdateUserRequested(long userId) {
        UserFormFragment fragment = UserFormFragment.newInstance(userId);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onUnauthorizedUser() {
        mPresenter.clearPreferences();
        Intent intent = new Intent(this, LandingActivity.class);
        intent.putExtra(UNAUTHORIZED_TAG, true);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    public void goToUsersList() {
        mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        UsersListFragment fragment = UsersListFragment.newInstance();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void goToReport() {
        mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        ReportFragment fragment = ReportFragment.newInstance();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void goToSettings() {
        mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        SettingsFragment fragment = SettingsFragment.newInstance();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void goToWelcome() {
        startActivity(new Intent(this, LandingActivity.class));
        finishAffinity();
    }

    @Override
    public void populateHeaderName(String name) {
        mViewHolder.headerNameLabel.setText(name);
    }

    @Override
    public void populateHeaderRole(int roleResId) {
        mViewHolder.headerRoleLabel.setText(roleResId);
    }

    @Override
    public void populateNavigationViewItems(int menuResId) {
        mViewHolder.navigationView.inflateMenu(menuResId);
    }

    private void setupToolbar() {
        setSupportActionBar(mViewHolder.toolbar);
    }

    private void setupDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                mViewHolder.drawerLayout,
                mViewHolder.toolbar,
                R.string.main_drawer_open,
                R.string.main_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                hideKeyboard();
                super.onDrawerOpened(drawerView);
            }
        };
        mViewHolder.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupNavigationView() {
        mViewHolder.navigationView.setNavigationItemSelectedListener(this);
        mPresenter.requestNavigationViewPopulation();
    }

    private void setupInitialSelection() {
        MenuItem myRecordsItem = mViewHolder.navigationView.getMenu()
                .findItem(R.id.nav_time_entries_list);
        myRecordsItem.setChecked(true);
        onNavigationItemSelected(myRecordsItem);
        mDrawerSelectedId = R.id.nav_time_entries_list;
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mToolbarSpinnerSelectedPos = position;
        TimeEntriesListFragment fragment = getFragment(TimeEntriesListFragment.class);
        if (fragment != null) fragment.toolbarSpinnerItemSelected(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    @Nullable
    private <T extends Fragment> T getFragment(Class<T> type) {
        Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);
        try {
            return type.cast(fragment);
        } catch (ClassCastException ex) {
            return null;
        }
    }

    static class ViewHolder {

        @BindView(R.id.toolbar)
        Toolbar toolbar;

        @BindView(R.id.toolbar_spinner)
        AppCompatSpinner spinner;

        @BindView(R.id.drawer_layout)
        DrawerLayout drawerLayout;

        @BindView(R.id.nav_view)
        NavigationView navigationView;

        @BindView(R.id.add_button)
        FloatingActionButton fab;

        AppCompatTextView headerNameLabel;

        AppCompatTextView headerRoleLabel;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);

            // Get references to navigation view header fields
            View headerLayout = navigationView.getHeaderView(0); // header is at index 0
            headerNameLabel = (AppCompatTextView) headerLayout.findViewById(R.id.name_label);
            headerRoleLabel = (AppCompatTextView) headerLayout.findViewById(R.id.role_label);
        }
    }
}
