package com.svanegas.trackmyjog.domain.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.svanegas.trackmyjog.R;

import static com.svanegas.trackmyjog.util.PreferencesManager.EMAIL_KEY;
import static com.svanegas.trackmyjog.util.PreferencesManager.NAME_KEY;

public class SettingsFragment extends PreferenceFragmentCompat implements SettingsView {

    private SettingsPresenter mPresenter;
    private OnSettingsListener mListener;
    private ViewHolder mViewHolder;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        mPresenter = new SettingsPresenterImpl(this);
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mListener.onActivityTitleRequested(R.string.settings_title);
        mViewHolder = new ViewHolder(this);
        initListeners();
        mPresenter.fetchAccountInfo();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsListener) {
            mListener = (OnSettingsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnSettingsListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initListeners() {
        mViewHolder.name.setOnPreferenceClickListener(
                preference -> mPresenter.changeNameRequested());
        mViewHolder.email.setOnPreferenceClickListener(
                preference -> mPresenter.changeEmailRequested());
        mViewHolder.changePassword.setOnPreferenceClickListener(
                preference -> mPresenter.changePasswordRequested());
    }

    @Override
    public void populateName(String name) {
        mViewHolder.name.setSummary(name);
    }

    @Override
    public void populateEmail(String email) {
        mViewHolder.email.setSummary(email);
    }

    static class ViewHolder {

        private static final String CHANGE_PASSWORD_KEY = "change_password";

        Preference name;
        Preference email;
        Preference changePassword;

        public ViewHolder(PreferenceFragmentCompat preferenceFragmentCompat) {

            name = preferenceFragmentCompat.findPreference(NAME_KEY);
            email = preferenceFragmentCompat.findPreference(EMAIL_KEY);
            changePassword = preferenceFragmentCompat.findPreference(CHANGE_PASSWORD_KEY);
        }
    }

    public interface OnSettingsListener {

        void onActivityTitleRequested(int titleResId);
    }
}
