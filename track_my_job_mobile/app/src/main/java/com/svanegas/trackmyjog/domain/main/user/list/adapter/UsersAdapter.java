package com.svanegas.trackmyjog.domain.main.user.list.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.svanegas.trackmyjog.R;
import com.svanegas.trackmyjog.domain.main.user.list.UsersListPresenter;
import com.svanegas.trackmyjog.repository.model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private UsersListPresenter mPresenter;
    private List<User> mUsers;

    public UsersAdapter(UsersListPresenter presenter,
                        @NonNull List<User> users) {
        mPresenter = presenter;
        mUsers = users;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemRow = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_list_item, parent, false);
        return new ViewHolder(itemRow);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User user = mUsers.get(position);

        holder.rootView.setOnClickListener(view -> mPresenter.userClicked(user));
    }

    @Override
    public int getItemCount() {
        return mUsers != null ? mUsers.size() : 0;
    }

    public List<User> getItems() {
        return mUsers;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.root_view)
        ViewGroup rootView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
