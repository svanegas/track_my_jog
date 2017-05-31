package com.svanegas.trackmyjog.domain.main.user.form;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.svanegas.trackmyjog.R;
import com.svanegas.trackmyjog.domain.main.dialog.DeleteConfirmDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static com.svanegas.trackmyjog.util.PreferencesManager.ADMIN_ROLE;
import static com.svanegas.trackmyjog.util.PreferencesManager.MANAGER_ROLE;
import static com.svanegas.trackmyjog.util.PreferencesManager.REGULAR_ROLE;

public class UserFormFragment extends Fragment implements UserFormView,
        DeleteConfirmDialogFragment.Callback {

    private static final String USER_ID_KEY = "user_id";

    // Indexes of toolbar spinner
    public static final int REGULAR_INDEX = 0;
    public static final int MANAGER_INDEX = 1;
    public static final int ADMIN_INDEX = 2;

    private OnUserFormListener mListener;
    private UserFormPresenter mPresenter;
    private ViewHolder mViewHolder;
    private long mUserId;
    private boolean mIsUpdate;

    public static UserFormFragment newInstance() {
        return new UserFormFragment();
    }

    public static UserFormFragment newInstance(long userId) {
        UserFormFragment fragment = new UserFormFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(USER_ID_KEY, userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new UserFormPresenterImpl(this);
        Bundle args = getArguments();
        if (args != null && args.containsKey(USER_ID_KEY)) {
            mUserId = args.getLong(USER_ID_KEY);
            mIsUpdate = true;
            setHasOptionsMenu(true);
        } else {
            mUserId = -1;
            mIsUpdate = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mIsUpdate) mListener.onActivityTitleRequested(R.string.user_form_update_title);
        else mListener.onActivityTitleRequested(R.string.user_form_create_title);
        View rootView = inflater.inflate(R.layout.user_form_fragment, container, false);
        mViewHolder = new ViewHolder(rootView);
        ButterKnife.bind(this, rootView);

        setupPasswordsFields();
        setupRolesSpinner();
        setupSubmitButton();
        if (mIsUpdate) mPresenter.fetchUser(mUserId);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserFormListener) {
            mListener = (OnUserFormListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnUserFormListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_user_update, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.submit_button)
    public void onSubmitClicked() {
        mPresenter.submitUser(mIsUpdate ? mUserId : -1);
    }

    private void setupPasswordsFields() {
        mViewHolder.passwordContainer.setVisibility(mIsUpdate ? View.GONE : View.VISIBLE);
        mViewHolder.passwordConfirmationContainer
                .setVisibility(mIsUpdate ? View.GONE : View.VISIBLE);
    }

    private void setupRolesSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.user_form_spinner_roles_options,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mViewHolder.rolesSpinner.setAdapter(adapter);
    }

    private void setupSubmitButton() {
        if (mIsUpdate) {
            mViewHolder.submitButton.setText(R.string.user_form_update_submit_text);
        } else {
            mViewHolder.submitButton.setText(R.string.user_form_create_submit_text);
        }
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
        return mViewHolder.passwordField.getText().toString().trim();
    }

    @Override
    public String passwordConfirmation() {
        return mViewHolder.passwordConfirmationField.getText().toString().trim();
    }

    @Override
    public String role() {
        switch (mViewHolder.rolesSpinner.getSelectedItemPosition()) {
            case MANAGER_INDEX:
                return MANAGER_ROLE;
            case ADMIN_INDEX:
                return ADMIN_ROLE;
            default:
                return REGULAR_ROLE;
        }
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
    public void onCreationSuccess() {
        Snackbar.make(mViewHolder.rootView,
                R.string.user_form_create_success, LENGTH_LONG).show();
        mListener.goToUsersList();
    }

    @Override
    public void onUpdateSuccess() {
        Snackbar.make(mViewHolder.rootView,
                R.string.user_form_update_success, LENGTH_LONG).show();
        mListener.goToUsersList();
    }

    @Override
    public void onDeletionSuccess() {
        Snackbar.make(mViewHolder.rootView,
                R.string.user_form_delete_success, LENGTH_LONG).show();
        mListener.goToUsersList();
    }

    @Override
    public void disableUserEdition() {
        setHasOptionsMenu(false);
        setLoadingAndFieldsEnabled(false);
        mViewHolder.progressBar.setVisibility(View.GONE);
        mViewHolder.submitButton.setVisibility(View.GONE);
    }

    @Override
    public void populateName(String name) {
        mViewHolder.nameField.setText(name);
    }

    @Override
    public void populateEmail(String email) {
        mViewHolder.emailField.setText(email);
    }

    @Override
    public void populateRoleSpinner(int adminIndex) {
        mViewHolder.rolesSpinner.setSelection(adminIndex);
    }

    @Override
    public void showEmptyNameError() {
        mViewHolder.nameField.setError(getString(R.string.error_field_required));
        mViewHolder.nameField.requestFocus();
    }

    @Override
    public void showEmptyEmailError() {
        mViewHolder.emailField.setError(getString(R.string.error_field_required));
        mViewHolder.emailField.requestFocus();
    }

    @Override
    public void showInvalidEmailError() {
        mViewHolder.emailField.setError(getString(R.string.register_error_invalid_email));
        mViewHolder.emailField.requestFocus();
    }

    @Override
    public void showEmptyPasswordError() {
        mViewHolder.passwordField.setError(getString(R.string.error_field_required));
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
    public void goToWelcomeDueUnauthorized() {
        mListener.onUnauthorizedUser();
    }

    private void setLoadingAndFieldsEnabled(boolean enabled) {
        mViewHolder.progressBar.setVisibility(enabled ? View.GONE : View.VISIBLE);
        mViewHolder.nameField.setEnabled(enabled);
        mViewHolder.emailField.setEnabled(enabled);
        mViewHolder.passwordField.setEnabled(enabled);
        mViewHolder.passwordConfirmationField.setEnabled(enabled);
        mViewHolder.rolesSpinner.setEnabled(enabled);
        mViewHolder.submitButton.setEnabled(enabled);
    }

    private void showDeleteConfirmationDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DeleteConfirmDialogFragment alert = DeleteConfirmDialogFragment.newInstance(this,
                R.string.user_form_delete_confirmation);
        alert.show(fm, DeleteConfirmDialogFragment.class.getSimpleName());
    }

    @Override
    public void onDeleteConfirmed() {
        mPresenter.deleteUser(mUserId);
    }

    static class ViewHolder {

        @BindView(R.id.root_view)
        ViewGroup rootView;

        @BindView(R.id.error_message)
        AppCompatTextView errorMessage;

        @BindView(R.id.name_field)
        AppCompatEditText nameField;

        @BindView(R.id.email_field)
        AppCompatEditText emailField;

        @BindView(R.id.password_container)
        TextInputLayout passwordContainer;

        @BindView(R.id.password_field)
        AppCompatEditText passwordField;

        @BindView(R.id.password_confirmation_container)
        TextInputLayout passwordConfirmationContainer;

        @BindView(R.id.password_confirmation_field)
        AppCompatEditText passwordConfirmationField;

        @BindView(R.id.roles_spinner)
        AppCompatSpinner rolesSpinner;

        @BindView(R.id.submit_button)
        AppCompatButton submitButton;

        @BindView(R.id.progress_bar)
        ProgressBar progressBar;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnUserFormListener {

        void onActivityTitleRequested(int titleResId);

        void goToUsersList();

        void onUnauthorizedUser();
    }
}
