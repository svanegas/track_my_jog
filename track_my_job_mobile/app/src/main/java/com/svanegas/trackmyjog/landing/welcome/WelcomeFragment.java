package com.svanegas.trackmyjog.landing.welcome;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.svanegas.trackmyjog.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Santiago Vanegas Gil on 5/22/17.
 */
public class WelcomeFragment extends Fragment implements WelcomeView {

    private OnWelcomeInteractionListener mListener;
    private WelcomePresenter mPresenter;

    public static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new WelcomePresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mListener.onActivityTitleRequested(R.string.welcome_title, false);
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.welcome_fragment, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWelcomeInteractionListener) {
            mListener = (OnWelcomeInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnWelcomeInteractionListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick(R.id.register_button)
    public void onRegisterButtonClicked() {
        mPresenter.registerButtonClicked();
    }

    @OnClick(R.id.login_button)
    public void onLoginButtonClicked() {
        mPresenter.loginButtonClicked();
    }

    @Override
    public void goToRegister() {
        mListener.onRegisterRequested();
    }

    @Override
    public void goToLogin() {
        mListener.onLoginRequested();
    }

    public interface OnWelcomeInteractionListener {

        void onRegisterRequested();

        void onLoginRequested();

        void onActivityTitleRequested(int titleResId, boolean showBackArrow);
    }
}
