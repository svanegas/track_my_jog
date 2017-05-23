package com.svanegas.trackmyjog.domain.landing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.svanegas.trackmyjog.R;
import com.svanegas.trackmyjog.domain.landing.login.LoginFragment;
import com.svanegas.trackmyjog.domain.landing.register.RegisterFragment;
import com.svanegas.trackmyjog.domain.landing.welcome.WelcomeFragment;

import butterknife.ButterKnife;

public class LandingActivity extends AppCompatActivity implements
        WelcomeFragment.OnWelcomeInteractionListener, LoginFragment.OnLoginInteractionListener,
        RegisterFragment.OnRegisterInteractionListener {

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_activity);

        ButterKnife.bind(this);

        mFragmentManager = getSupportFragmentManager();
        setupWelcomeFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRegisterRequested() {
        performFragmentTransaction(RegisterFragment.newInstance(), false);
    }

    @Override
    public void onLoginRequested() {
        performFragmentTransaction(LoginFragment.newInstance(), false);
    }

    @Override
    public void onActivityTitleRequested(int titleResId, boolean showBackArrow) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            setTitle(titleResId);
            actionBar.setDisplayHomeAsUpEnabled(showBackArrow);
        }
    }

    private void setupWelcomeFragment() {
        performFragmentTransaction(WelcomeFragment.newInstance(), true);
    }

    private void performFragmentTransaction(Fragment fragment, boolean initialFragment) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (initialFragment) fragmentTransaction.add(R.id.fragment_container, fragment);
        else {
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }
}
