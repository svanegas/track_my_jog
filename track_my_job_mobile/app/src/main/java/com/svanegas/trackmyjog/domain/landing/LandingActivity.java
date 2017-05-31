package com.svanegas.trackmyjog.domain.landing;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.svanegas.trackmyjog.R;
import com.svanegas.trackmyjog.domain.landing.login.LoginFragment;
import com.svanegas.trackmyjog.domain.landing.register.RegisterFragment;
import com.svanegas.trackmyjog.domain.landing.welcome.WelcomeFragment;
import com.svanegas.trackmyjog.domain.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LandingActivity extends AppCompatActivity implements
        LandingView, WelcomeFragment.OnWelcomeInteractionListener,
        LoginFragment.OnLoginInteractionListener,
        RegisterFragment.OnRegisterInteractionListener {

    public static final String UNAUTHORIZED_TAG = "unauthorized";

    private FragmentManager mFragmentManager;
    private LandingPresenter mPresenter;

    @BindView(R.id.root_view)
    ViewGroup mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_activity);

        mPresenter = new LandingPresenterImpl(this);
        ButterKnife.bind(this);

        mFragmentManager = getSupportFragmentManager();
        setupWelcomeFragment();
        showMessageIfUnauthorized();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.checkIfLogged();
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

    @Override
    public void goToMainScreen() {
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
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

    private void showMessageIfUnauthorized() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null && extras.containsKey(UNAUTHORIZED_TAG)) {
                Snackbar.make(mRootView,
                        R.string.welcome_unauthorized,
                        Snackbar.LENGTH_LONG).show();
                extras.remove(UNAUTHORIZED_TAG);
            }
        }
    }
}
