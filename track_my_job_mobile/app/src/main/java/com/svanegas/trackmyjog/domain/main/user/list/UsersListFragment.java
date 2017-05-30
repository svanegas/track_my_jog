package com.svanegas.trackmyjog.domain.main.user.list;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.svanegas.trackmyjog.R;
import com.svanegas.trackmyjog.domain.main.dialog.SortByDialogFragment;
import com.svanegas.trackmyjog.domain.main.user.list.adapter.UsersAdapter;
import com.svanegas.trackmyjog.repository.model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.svanegas.trackmyjog.domain.main.user.list.UsersListPresenterImpl.NAME_SORT_INDEX;

public class UsersListFragment extends Fragment implements UsersListView,
        SwipeRefreshLayout.OnRefreshListener, SortByDialogFragment.Callback {

    private OnUsersListInteractionListener mListener;
    private UsersListPresenter mPresenter;
    private ViewHolder mViewHolder;
    private UsersAdapter mAdapter;
    private int mCurrentSortOption;

    public static UsersListFragment newInstance() {
        return new UsersListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new UsersListPresenterImpl(this);
        mCurrentSortOption = NAME_SORT_INDEX;
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mListener.onActivityTitleRequested(R.string.users_list_title);
        View rootView = inflater.inflate(R.layout.users_list_fragment, container, false);
        mViewHolder = new ViewHolder(rootView);
        ButterKnife.bind(this, rootView);
        mViewHolder.swipeRefreshLayout.setOnRefreshListener(this);
        mPresenter.fetchUsers(false);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUsersListInteractionListener) {
            mListener = (OnUsersListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnUsersListInteractionListener.class.getSimpleName());
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
        inflater.inflate(R.menu.menu_users_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by:
                showSortByDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        mPresenter.fetchUsers(true);
    }

    @Override
    public void onSortOptionSelected(int position) {
        if (mCurrentSortOption != position) {
            mCurrentSortOption = position;
            if (mAdapter != null) populateUsers(mAdapter.getItems());
        }
    }

    @OnClick(R.id.add_button)
    public void onAddClicked() {
        mListener.onAddUserRequested();
    }

    @Override
    public void onUserClicked(User user) {
        mListener.onUpdateUserRequested(user.getId());
    }

    @Override
    public void showLoading(boolean pulledToRefresh) {
        if (!pulledToRefresh) mViewHolder.progress.setVisibility(View.VISIBLE);
        mViewHolder.errorMessage.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading(boolean pulledToRefresh) {
        if (pulledToRefresh) mViewHolder.swipeRefreshLayout.setRefreshing(false);
        else mViewHolder.progress.setVisibility(View.GONE);
    }

    @Override
    public void populateUsers(List<User> users) {
        mPresenter.sortUsers(mCurrentSortOption, users);
        mAdapter = new UsersAdapter(mPresenter, users);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mViewHolder.usersList.setLayoutManager(layoutManager);
        mViewHolder.usersList.setAdapter(mAdapter);
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
    public void goToWelcomeDueUnauthorized() {
        mListener.onUnauthorizedUser();
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

    private void showSortByDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        SortByDialogFragment dialog = SortByDialogFragment.newInstance(this,
                R.array.users_list_sort_options);
        dialog.show(fm, SortByDialogFragment.class.getSimpleName());
    }

    static class ViewHolder {

        @BindView(R.id.swipe_refresh_layout)
        SwipeRefreshLayout swipeRefreshLayout;

        @BindView(R.id.error_message)
        AppCompatTextView errorMessage;

        @BindView(R.id.users_list)
        RecyclerView usersList;

        @BindView(R.id.progress)
        ProgressBar progress;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);

            swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                    R.color.colorAccentDark);
        }
    }

    public interface OnUsersListInteractionListener {

        void onActivityTitleRequested(int titleResId);

        void onAddUserRequested();

        void onUpdateUserRequested(long userId);

        void onUnauthorizedUser();
    }
}
