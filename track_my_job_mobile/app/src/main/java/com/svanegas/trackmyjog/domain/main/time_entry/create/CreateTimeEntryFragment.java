package com.svanegas.trackmyjog.domain.main.time_entry.create;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.svanegas.trackmyjog.R;

import butterknife.ButterKnife;

public class CreateTimeEntryFragment extends Fragment implements CreateTimeEntryView {

    private OnCreateTimeEntryListener mListener;
    private CreateTimeEntryPresenter mPresenter;
    private ViewHolder mViewHolder;

    public static CreateTimeEntryFragment newInstance() {
        return new CreateTimeEntryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new CreateTimeEntryPresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mListener.onActivityTitleRequested(R.string.create_time_entry_title);
        View rootView = inflater.inflate(R.layout.create_time_entry_fragment, container, false);
        mViewHolder = new ViewHolder(rootView);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCreateTimeEntryListener) {
            mListener = (OnCreateTimeEntryListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnCreateTimeEntryListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    static class ViewHolder {

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnCreateTimeEntryListener {
        void onActivityTitleRequested(int titleResId);
    }
}
