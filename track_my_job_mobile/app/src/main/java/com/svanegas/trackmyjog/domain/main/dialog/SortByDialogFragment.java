package com.svanegas.trackmyjog.domain.main.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.svanegas.trackmyjog.R;

import java.io.Serializable;

public class SortByDialogFragment extends DialogFragment {

    public static final String CALLBACK_KEY = "callback";
    public static final String ITEMS_KEY = "items";

    public static SortByDialogFragment newInstance(Callback callback, int itemsId) {
        SortByDialogFragment fragment = new SortByDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(CALLBACK_KEY, callback);
        args.putInt(ITEMS_KEY, itemsId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        final Callback callback = (Callback) args.getSerializable(CALLBACK_KEY);
        int itemsId = args.getInt(ITEMS_KEY);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.main_list_sort_by)
                .setItems(itemsId, (dialog, which) -> {
                    if (callback != null) callback.onSortOptionSelected(which);
                });
        return builder.create();
    }

    public interface Callback extends Serializable {

        void onSortOptionSelected(int position);
    }
}
