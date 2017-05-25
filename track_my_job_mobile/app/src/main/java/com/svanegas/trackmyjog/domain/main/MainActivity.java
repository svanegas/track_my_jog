package com.svanegas.trackmyjog.domain.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.svanegas.trackmyjog.R;
import com.svanegas.trackmyjog.domain.landing.LandingActivity;
import com.svanegas.trackmyjog.domain.main.time_entry.form.TimeEntryFormFormFragment;
import com.svanegas.trackmyjog.domain.main.time_entry.list.TimeEntriesListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainView,
        NavigationView.OnNavigationItemSelectedListener,
        TimeEntriesListFragment.OnTimeEntriesListInteractionListener,
        TimeEntryFormFormFragment.OnTimeEntryFormListener {

    @BindView(R.id.drawer_layout)
    ViewGroup rootView;

    private MainPresenter mPresenter;
    private ViewHolder mViewHolder;
    private FragmentManager mFragmentManager;
    private int mDrawerSelectedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mPresenter = new MainPresenterImpl(this);

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

    @Override
    public void onActivityTitleRequested(int titleResId) {
        setTitle(titleResId);
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
    public void goToWelcome() {
        startActivity(new Intent(this, LandingActivity.class));
        finishAffinity();
    }

    private void setupToolbar() {
        setSupportActionBar(mViewHolder.toolbar);
    }

    private void setupDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                mViewHolder.drawerLayout,
                mViewHolder.toolbar,
                R.string.main_drawer_open,
                R.string.main_drawer_close);
        mViewHolder.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupNavigationView() {
        mViewHolder.navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupInitialSelection() {
        MenuItem myRecordsItem = mViewHolder.navigationView.getMenu()
                .findItem(R.id.nav_time_entries_list);
        myRecordsItem.setChecked(true);
        onNavigationItemSelected(myRecordsItem);
        mDrawerSelectedId = R.id.nav_time_entries_list;
    }

    static class ViewHolder {

        @BindView(R.id.toolbar)
        Toolbar toolbar;

        @BindView(R.id.drawer_layout)
        DrawerLayout drawerLayout;

        @BindView(R.id.nav_view)
        NavigationView navigationView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
