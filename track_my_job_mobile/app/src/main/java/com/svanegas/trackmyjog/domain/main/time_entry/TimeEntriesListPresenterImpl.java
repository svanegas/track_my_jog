package com.svanegas.trackmyjog.domain.main.time_entry;

import android.util.Log;

import com.svanegas.trackmyjog.TrackMyJogApplication;
import com.svanegas.trackmyjog.interactor.TimeEntryInteractor;
import com.svanegas.trackmyjog.repository.model.APIError;

import java.net.SocketTimeoutException;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

import static com.svanegas.trackmyjog.network.ConnectionInterceptor.isInternetConnectionError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.isHttpError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.isUnauthorizedError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.parseHttpError;

public class TimeEntriesListPresenterImpl implements TimeEntriesListPresenter {

    private static final String TAG = TimeEntriesListPresenterImpl.class.getSimpleName();
    TimeEntriesListView mView;

    @Inject
    TimeEntryInteractor mInteractor;

    TimeEntriesListPresenterImpl(TimeEntriesListView timeEntriesListView) {
        mView = timeEntriesListView;
        TrackMyJogApplication.getInstance().getApplicationComponent().inject(this);
    }

    @Override
    public void fetchTimeEntries() {
        mView.showLoading();
        mInteractor.fetchTimeEntries()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timeEntries -> {
                    mView.hideLoading();
                    mView.populateTimeEntries(timeEntries);
                }, throwable -> {
                    mView.hideLoading();
                    if (throwable instanceof SocketTimeoutException) {
                        mView.showTimeoutError();
                    } else if (isInternetConnectionError(throwable)) {
                        mView.showNoConnectionError();
                    } else if (isUnauthorizedError(throwable)) {
                        mView.goToWelcomeDueUnauthorized();
                    } else if (isHttpError(throwable)) {
                        APIError error = parseHttpError((HttpException) throwable);
                        if (error != null && error.getErrorMessage() != null) {
                            mView.showDisplayableError(error.errorMessage);
                        } else mView.showUnknownError();
                    } else {
                        Log.e(TAG, "Could not fetch time entries due unknown error: ", throwable);
                        mView.showUnknownError();
                    }
                });
    }
}
