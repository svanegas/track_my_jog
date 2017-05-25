package com.svanegas.trackmyjog.domain.main.time_entry.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.svanegas.trackmyjog.R;

import java.io.Serializable;

public class DeleteConfirmDialogFragment extends DialogFragment {

    public static final String CALLBACK_KEY = "callback";

    public static DeleteConfirmDialogFragment newInstance(Callback callback) {
        DeleteConfirmDialogFragment fragment = new DeleteConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(CALLBACK_KEY, callback);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        final Callback callback = (Callback) args.getSerializable(CALLBACK_KEY);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.time_entry_form_delete_confirmation)
                .setPositiveButton(R.string.time_entry_form_action_delete, (dialog, id) -> {
                    if (callback != null) callback.onDeleteConfirmed();
                })
                .setNegativeButton(R.string.time_entry_form_delete_cancel, (dialog, id) -> {
                    // Do nothing
                });
        return builder.create();
    }

    public interface Callback extends Serializable {

        void onDeleteConfirmed();
    }
}
