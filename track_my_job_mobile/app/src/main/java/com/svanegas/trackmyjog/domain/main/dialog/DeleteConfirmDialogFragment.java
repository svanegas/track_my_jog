package com.svanegas.trackmyjog.domain.main.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.svanegas.trackmyjog.R;

import java.io.Serializable;

public class DeleteConfirmDialogFragment extends DialogFragment {

    public static final String CALLBACK_KEY = "callback";
    public static final String TITLE_KEY = "title";

    public static DeleteConfirmDialogFragment newInstance(Callback callback, int titleId) {
        DeleteConfirmDialogFragment fragment = new DeleteConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(CALLBACK_KEY, callback);
        args.putInt(TITLE_KEY, titleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        final Callback callback = (Callback) args.getSerializable(CALLBACK_KEY);
        int titleId = args.getInt(TITLE_KEY);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(titleId)
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
