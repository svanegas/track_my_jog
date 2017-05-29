package com.svanegas.trackmyjog.domain.main.report;

import android.content.Context;
import android.util.Log;

import com.svanegas.trackmyjog.R;
import com.svanegas.trackmyjog.TrackMyJogApplication;
import com.svanegas.trackmyjog.interactor.TimeEntryInteractor;
import com.svanegas.trackmyjog.repository.model.APIError;

import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

import static com.svanegas.trackmyjog.domain.main.time_entry.list.TimeEntriesListPresenterImpl.INPUT_DATE_FORMAT;
import static com.svanegas.trackmyjog.network.ConnectionInterceptor.isInternetConnectionError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.isHttpError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.parseHttpError;

public class ReportPresenterImpl implements ReportPresenter {

    private static final String TAG = ReportPresenterImpl.class.getSimpleName();

    private ReportView mView;

    @Inject
    TimeEntryInteractor mInteractor;

    @Inject
    Context mContext;

    public ReportPresenterImpl(ReportView reportView) {
        mView = reportView;
        TrackMyJogApplication.getInstance().getApplicationComponent().inject(this);
    }

    @Override
    public void fetchReport(Calendar date, boolean pulledToRefresh) {
        mView.showLoadingAndDisableFields(pulledToRefresh);
        String dateString = String.format(Locale.US, "%d-%d-%d",
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH) + 1,
                date.get(Calendar.DAY_OF_MONTH));
        mInteractor.fetchReport(dateString)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(report -> {
                    mView.hideLoadingAndEnableFields(pulledToRefresh);
                    try {
                        String dateFrom = setupFormattedDate(report.getFrom());
                        String dateTo = setupFormattedDate(report.getTo());
                        mView.populateDateRange(dateFrom, dateTo);
                    } catch (ParseException e) {
                        Log.e(TAG, "There was an error parsing the date of report", e);
                        mView.showUnableToParseDateError();
                    }
                    if (report.getCount() == 0) mView.populateEmpty();
                    else mView.populateReport(report);
                }, throwable -> {
                    mView.hideLoadingAndEnableFields(pulledToRefresh);
                    if (throwable instanceof SocketTimeoutException) {
                        mView.showTimeoutError();
                    } else if (isInternetConnectionError(throwable)) {
                        mView.showNoConnectionError();
                    } else if (isHttpError(throwable)) {
                        APIError error = parseHttpError((HttpException) throwable);
                        if (error != null && error.getErrorMessage() != null) {
                            mView.showDisplayableError(error.errorMessage);
                        } else mView.showUnknownError();
                    } else {
                        Log.e(TAG, "Could not fetch report due unknown error: ", throwable);
                        mView.showUnknownError();
                    }
                });
    }

    private String setupFormattedDate(String date) throws ParseException {
        DateFormat inputFormat = new SimpleDateFormat(INPUT_DATE_FORMAT, Locale.US);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Calendar entryDate = Calendar.getInstance();
        entryDate.setTime(inputFormat.parse(date));
        String outFormatString;
        if (currentYear == entryDate.get(Calendar.YEAR)) {
            outFormatString = mContext.getString(R.string.report_without_year_format);
        } else outFormatString = mContext.getString(R.string.report_with_year_format);
        return new SimpleDateFormat(outFormatString, Locale.US).format(entryDate.getTime());
    }
}
