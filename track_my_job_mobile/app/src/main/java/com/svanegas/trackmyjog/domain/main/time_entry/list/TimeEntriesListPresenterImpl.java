package com.svanegas.trackmyjog.domain.main.time_entry.list;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
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

import static com.svanegas.trackmyjog.network.ConnectionInterceptor.isInternetConnectionError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.isHttpError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.isUnauthorizedError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.parseHttpError;

public class TimeEntriesListPresenterImpl implements TimeEntriesListPresenter {

    private static final String TAG = TimeEntriesListPresenterImpl.class.getSimpleName();

    private static final String INPUT_DATE_FORMAT = "yyyy-MM-dd";
    private static final float UNITS_RELATIVE_SIZE = 0.7f;
    private static final String KM_UNIT = "km";

    private TimeEntriesListView mView;

    @Inject
    Context mContext;

    @Inject
    TimeEntryInteractor mInteractor;

    TimeEntriesListPresenterImpl(TimeEntriesListView timeEntriesListView) {
        mView = timeEntriesListView;
        TrackMyJogApplication.getInstance().getApplicationComponent().inject(this);
    }

    @Override
    public void fetchTimeEntries(boolean pulledToRefresh) {
        mView.showLoading(pulledToRefresh);
        mInteractor.fetchTimeEntries()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timeEntries -> {
                    mView.hideLoading(pulledToRefresh);
                    mView.populateTimeEntries(timeEntries);
                }, throwable -> {
                    mView.hideLoading(pulledToRefresh);
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

    @Override
    public String setupFormattedDate(String date) throws ParseException {
        DateFormat inputFormat = new SimpleDateFormat(INPUT_DATE_FORMAT, Locale.US);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Calendar entryDate = Calendar.getInstance();
        entryDate.setTime(inputFormat.parse(date));
        String outFormatString;
        if (currentYear == entryDate.get(Calendar.YEAR)) {
            outFormatString = mContext.getString(R.string.my_time_entries_without_year_format);
        } else outFormatString = mContext.getString(R.string.my_time_entries_with_year_format);
        return new SimpleDateFormat(outFormatString, Locale.US).format(entryDate.getTime());
    }

    @Override
    public Spannable setupDistanceText(long distance) {
        String units = KM_UNIT; // Maybe take preferred units from preferences.
        String textWithUnits = String.format(Locale.US, "%.2f %s",
                computeDistance(distance, units),
                units);
        Spannable span = new SpannableString(textWithUnits);
        span.setSpan(new RelativeSizeSpan(UNITS_RELATIVE_SIZE),
                textWithUnits.length() - units.length(),
                textWithUnits.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return span;
    }

    @Override
    public String setupDurationText(long duration) {
        long hours = duration / 60;
        long minutes = duration % 60;
        if (hours > 0) {
            if (minutes != 0) return String.format(Locale.US, "%d hr %d min", hours, minutes);
            else return String.format(Locale.US, "%d hr", hours);
        } else return String.format(Locale.US, "%d min", minutes);
    }

    /**
     * Converts the given distance to the desired units.
     *
     * @param distance distance in meters
     * @param units    desired units, possible units: {@link #KM_UNIT}
     * @return double representation of the given distance in given units
     * @throws IllegalArgumentException when given {@param #units} does not belong to possible units
     */
    private double computeDistance(long distance, String units) {
        switch (units) {
            case KM_UNIT:
                return distance / 1000f;
            default:
                throw new IllegalArgumentException(units + " is not a valid unit");
        }
    }
}
