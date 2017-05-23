package com.svanegas.trackmyjog.domain.landing.login;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.svanegas.trackmyjog.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends Fragment implements LoginView {

    private OnLoginInteractionListener mListener;
    private LoginPresenter mPresenter;
    private ViewHolder mViewHolder;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new LoginPresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mListener.onActivityTitleRequested(R.string.login_title, true);
        View rootView = inflater.inflate(R.layout.login_fragment, container, false);
        mViewHolder = new ViewHolder(rootView);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginInteractionListener) {
            mListener = (OnLoginInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnLoginInteractionListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick(R.id.submit_button)
    public void onSubmitClicked() {
        mPresenter.validateLogin();
    }

    @Override
    public String email() {
        return mViewHolder.emailField.getText().toString().trim();
    }

    @Override
    public String password() {
        return mViewHolder.passwordField.getText().toString();
    }

    @Override
    public void showLoadingAndDisableFields() {
        mViewHolder.errorMessage.setVisibility(View.GONE);
        setLoadingAndFieldsEnabled(false);
    }

    @Override
    public void hideLoadingAndEnableFields() {
        setLoadingAndFieldsEnabled(true);
    }

    @Override
    public void showEmptyEmailError() {
        mViewHolder.emailField.setError(getString(R.string.register_error_field_required));
        mViewHolder.emailField.requestFocus();
    }

    @Override
    public void showEmptyPasswordError() {
        mViewHolder.passwordField.setError(getString(R.string.register_error_field_required));
        mViewHolder.passwordField.requestFocus();
    }

    @Override
    public void showTimeoutError() {
        mViewHolder.errorMessage.setVisibility(View.VISIBLE);
        mViewHolder.errorMessage.setText(R.string.error_timeout);
    }

    @Override
    public void showNoConnectionError() {
        mViewHolder.errorMessage.setVisibility(View.VISIBLE);
        mViewHolder.errorMessage.setText(R.string.error_no_internet);
    }

    @Override
    public void showDisplayableError(String errorMessage) {
        mViewHolder.errorMessage.setVisibility(View.VISIBLE);
        mViewHolder.errorMessage.setText(errorMessage);
    }

    @Override
    public void showUnknownError() {
        mViewHolder.errorMessage.setVisibility(View.VISIBLE);
        mViewHolder.errorMessage.setText(R.string.error_unknown);
    }

    private void setLoadingAndFieldsEnabled(boolean enabled) {
        mViewHolder.progressBar.setVisibility(enabled ? View.GONE : View.VISIBLE);
        mViewHolder.submitButton.setEnabled(enabled);
        mViewHolder.emailField.setEnabled(enabled);
        mViewHolder.passwordField.setEnabled(enabled);
    }

    static class ViewHolder {

        @BindView(R.id.email_field)
        AppCompatEditText emailField;

        @BindView(R.id.password_field)
        AppCompatEditText passwordField;

        @BindView(R.id.submit_button)
        AppCompatButton submitButton;

        @BindView(R.id.error_message)
        AppCompatTextView errorMessage;

        @BindView(R.id.progress_bar)
        ProgressBar progressBar;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnLoginInteractionListener {

        void onActivityTitleRequested(int titleResId, boolean showBackArrow);
    }
}
