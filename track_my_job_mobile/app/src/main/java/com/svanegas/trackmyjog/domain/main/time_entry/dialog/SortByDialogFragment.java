package com.svanegas.trackmyjog.domain.main.time_entry.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.svanegas.trackmyjog.R;

import java.io.Serializable;

public class SortByDialogFragment extends DialogFragment {

    public static final String CALLBACK_KEY = "callback";
    public static final int DATE_SORT_INDEX = 0;
    public static final int DISTANCE_SORT_INDEX = 1;
    public static final int DURATION_SORT_INDEX = 2;

    public static SortByDialogFragment newInstance(Callback callback) {
        SortByDialogFragment fragment = new SortByDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(CALLBACK_KEY, callback);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        final Callback callback = (Callback) args.getSerializable(CALLBACK_KEY);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.main_list_sort_by)
                .setItems(R.array.time_entries_list_sort_options, (dialog, which) -> {
                    if (callback != null) callback.onSortOptionSelected(which);
                });
        return builder.create();
    }

    public interface Callback extends Serializable {

        void onSortOptionSelected(int position);
    }
}
