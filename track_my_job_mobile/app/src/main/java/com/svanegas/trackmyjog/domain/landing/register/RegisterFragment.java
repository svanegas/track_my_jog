package com.svanegas.trackmyjog.domain.landing.register;

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

public class RegisterFragment extends Fragment implements RegisterView {

    private OnRegisterInteractionListener mListener;
    private RegisterPresenter mPresenter;
    private ViewHolder mViewHolder;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new RegisterPresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mListener.onActivityTitleRequested(R.string.register_title, true);
        View rootView = inflater.inflate(R.layout.register_fragment, container, false);
        mViewHolder = new ViewHolder(rootView);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterInteractionListener) {
            mListener = (OnRegisterInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnRegisterInteractionListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick(R.id.submit_button)
    public void onSubmitClicked() {
        mPresenter.validateRegistration();
    }

    @Override
    public String name() {
        return mViewHolder.nameField.getText().toString().trim();
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
    public String passwordConfirmation() {
        return mViewHolder.passwordConfirmationField.getText().toString();
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
    public void showEmptyNameError() {
        mViewHolder.nameField.setError(getString(R.string.register_error_field_required));
        mViewHolder.nameField.requestFocus();
    }

    @Override
    public void showEmptyEmailError() {
        mViewHolder.emailField.setError(getString(R.string.register_error_field_required));
        mViewHolder.emailField.requestFocus();
    }

    @Override
    public void showInvalidEmailError() {
        mViewHolder.emailField.setError(getString(R.string.register_error_invalid_email));
        mViewHolder.emailField.requestFocus();
    }

    @Override
    public void showEmptyPasswordError() {
        mViewHolder.passwordField.setError(getString(R.string.register_error_field_required));
        mViewHolder.passwordField.requestFocus();
    }

    @Override
    public void showShortPasswordError() {
        mViewHolder.passwordField.setError(getString(R.string.register_error_password_short));
        mViewHolder.passwordField.requestFocus();
    }

    @Override
    public void showPasswordsDontMatchError() {
        mViewHolder.passwordConfirmationField
                .setError(getString(R.string.register_error_passwords_mismatch));
        mViewHolder.passwordConfirmationField.requestFocus();
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

    @Override
    public void onRegisterSuccess() {
        mListener.goToMainScreen();
    }

    private void setLoadingAndFieldsEnabled(boolean enabled) {
        mViewHolder.progressBar.setVisibility(enabled ? View.GONE : View.VISIBLE);
        mViewHolder.submitButton.setEnabled(enabled);
        mViewHolder.nameField.setEnabled(enabled);
        mViewHolder.emailField.setEnabled(enabled);
        mViewHolder.passwordField.setEnabled(enabled);
        mViewHolder.passwordConfirmationField.setEnabled(enabled);
    }

    static class ViewHolder {

        @BindView(R.id.name_field)
        AppCompatEditText nameField;

        @BindView(R.id.email_field)
        AppCompatEditText emailField;

        @BindView(R.id.password_field)
        AppCompatEditText passwordField;

        @BindView(R.id.password_confirmation_field)
        AppCompatEditText passwordConfirmationField;

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

    public interface OnRegisterInteractionListener {

        void onActivityTitleRequested(int titleResId, boolean showBackArrow);

        void goToMainScreen();
    }
}
