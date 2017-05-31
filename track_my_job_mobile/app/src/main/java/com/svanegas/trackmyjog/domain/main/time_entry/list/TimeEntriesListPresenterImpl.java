package com.svanegas.trackmyjog.domain.main.time_entry.list;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import com.svanegas.trackmyjog.R;
import com.svanegas.trackmyjog.TrackMyJogApplication;
import com.svanegas.trackmyjog.interactor.TimeEntryInteractor;
import com.svanegas.trackmyjog.repository.model.APIError;
import com.svanegas.trackmyjog.repository.model.TimeEntry;
import com.svanegas.trackmyjog.repository.model.User;
import com.svanegas.trackmyjog.util.PreferencesManager;

import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

import static com.svanegas.trackmyjog.network.ConnectionInterceptor.isInternetConnectionError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.isHttpError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.isUnauthorizedError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.parseHttpError;
import static com.svanegas.trackmyjog.util.PreferencesManager.KM_UNIT;
import static com.svanegas.trackmyjog.util.PreferencesManager.MILE_UNIT;

public class TimeEntriesListPresenterImpl implements TimeEntriesListPresenter {

    private static final String TAG = TimeEntriesListPresenterImpl.class.getSimpleName();

    // Indexes of sort
    public static final int DATE_SORT_INDEX = 0;
    public static final int DISTANCE_SORT_INDEX = 1;
    public static final int DURATION_SORT_INDEX = 2;

    public static final String INPUT_DATE_FORMAT = "yyyy-MM-dd";
    private static final float UNITS_RELATIVE_SIZE = 0.7f;

    public static final double KM_CONVERSION_FACTOR = 1000.0;
    public static final double MI_CONVERSION_FACTOR = 1609.344;

    private TimeEntriesListView mView;

    @Inject
    Context mContext;

    @Inject
    TimeEntryInteractor mInteractor;

    @Inject
    PreferencesManager mPreferencesManager;

    @Inject
    CompositeDisposable mDisposables;

    TimeEntriesListPresenterImpl(TimeEntriesListView timeEntriesListView) {
        mView = timeEntriesListView;
        TrackMyJogApplication.getInstance().getApplicationComponent().inject(this);
    }

    @Override
    public void determineActivityTitle() {
        if (mPreferencesManager.isAdmin()) mView.setupSpinnerAsTitle();
        else {
            // We need to fetch time entries, because there won't be spinner selection that triggers
            // a fetch.
            mView.setupRegularTitle();
            fetchTimeEntries(false);
        }
    }

    @Override
    public void timeEntryClicked(TimeEntry timeEntry) {
        mView.onTimeEntryClicked(timeEntry);
    }

    @Override
    public void fetchTimeEntries(boolean pulledToRefresh) {
        String dateFrom = parseDateToString(mView.dateFrom());
        String dateTo = parseDateToString(mView.dateTo());
        Single<List<TimeEntry>> single = mInteractor.fetchTimeEntries(dateFrom, dateTo);
        processTimeEntries(single, pulledToRefresh);
    }

    @Override
    public void fetchTimeEntriesByCurrentUser(boolean pulledToRefresh) {
        String dateFrom = parseDateToString(mView.dateFrom());
        String dateTo = parseDateToString(mView.dateTo());
        Single<List<TimeEntry>> single = mInteractor.fetchTimeEntries(mPreferencesManager.getId(),
                dateFrom, dateTo);
        processTimeEntries(single, pulledToRefresh);
    }

    @Override
    public void sortTimeEntries(int sortOption, List<TimeEntry> timeEntries) {
        if (sortOption == DATE_SORT_INDEX) {
            Collections.sort(timeEntries, (o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        } else if (sortOption == DISTANCE_SORT_INDEX) {
            Collections.sort(timeEntries, (o1, o2) ->
                    Long.compare(o2.getDistance(), o1.getDistance()));
        } else if (sortOption == DURATION_SORT_INDEX) {
            Collections.sort(timeEntries, (o1, o2) ->
                    Long.compare(o2.getDuration(), o1.getDuration()));
        }
    }

    @Override
    public String setupFormattedDate(String date) throws ParseException {
        DateFormat inputFormat = new SimpleDateFormat(INPUT_DATE_FORMAT, Locale.US);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Calendar entryDate = Calendar.getInstance();
        entryDate.setTime(inputFormat.parse(date));
        String outFormatString;
        if (currentYear == entryDate.get(Calendar.YEAR)) {
            outFormatString = mContext.getString(R.string.time_entries_list_without_year_format);
        } else outFormatString = mContext.getString(R.string.time_entries_list_with_year_format);
        return new SimpleDateFormat(outFormatString, Locale.US).format(entryDate.getTime());
    }

    @Override
    public Spannable setupDistanceText(long distance) {
        String units = mPreferencesManager.getDistanceUnits();
        return createDistanceTextWithUnits(distance, units);
    }

    @Override
    public Spannable setupDurationText(long duration) {
        return createDurationText(mContext, duration);
    }

    @Override
    public Spannable setupSpeedText(TimeEntry timeEntry) {
        String units = mPreferencesManager.getDistanceUnits();
        return createSpeedText(mContext, timeEntry.getDistance(), timeEntry.getDuration(), units);
    }

    @Override
    public boolean isUserVisible() {
        return mView.shouldDisplayUserInList();
    }

    @Override
    public String setupUserText(User user) {
        if (user != null) return user.getName();
        else return "";
    }

    @Override
    public void unsubscribe() {
        mDisposables.clear();
    }

    @Override
    public void processSelectedFilterFrom(Calendar date) {
        mView.populateFilterDateFrom(formatSelectedDateFilter(date));
    }

    @Override
    public void processSelectedFilterTo(Calendar date) {
        mView.populateFilterDateTo(formatSelectedDateFilter(date));
    }

    private void processTimeEntries(Single<List<TimeEntry>> timeEntriesSingle,
                                    boolean pulledToRefresh) {
        mView.showLoading(pulledToRefresh);
        Disposable disposable = timeEntriesSingle
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timeEntries -> {
                    mView.hideLoading(pulledToRefresh);
                    if (timeEntries.isEmpty()) mView.populateEmpty();
                    else mView.populateTimeEntries(timeEntries);
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
        mDisposables.add(disposable);
    }

    /**
     * Converts the given distance to the desired units.
     *
     * @param distance distance in meters
     * @param units    desired units, possible units: {@link PreferencesManager#KM_UNIT},
     *                 {@link PreferencesManager#MILE_UNIT}.
     * @return double representation of the given distance in given units
     * @throws IllegalArgumentException when given {@param #units} does not belong to possible units
     */
    public static double computeDistance(long distance, String units) {
        switch (units) {
            case KM_UNIT:
                return distance / KM_CONVERSION_FACTOR;
            case MILE_UNIT:
                return distance / MI_CONVERSION_FACTOR;
            default:
                throw new IllegalArgumentException(units + " is not a valid unit");
        }
    }

    /**
     * Given a distance in meters and desired distance units, creates a {@link Spannable} text
     * with those parameters.
     *
     * @param distance meters representation of desired distance.
     * @param units    units to be placed in the {@link Spannable}.
     * @return spannable with the distance converted and including its units.
     */
    public static Spannable createDistanceTextWithUnits(long distance, String units) {
        String textWithUnits = String.format(Locale.US, "%.2f %s",
                computeDistance(distance, units),
                units);
        Spannable span = new SpannableString(textWithUnits);
        applyResizeSpan(span, textWithUnits, units);
        return span;
    }

    public static Spannable createDurationText(Context context, long duration) {
        long hours = duration / 60;
        long minutes = duration % 60;
        String resultText;
        String hourLabel = context.getString(R.string.time_entries_list_duration_hr);
        String minLabel = context.getString(R.string.time_entries_list_duration_min);
        if (hours > 0) {
            if (minutes != 0) {
                resultText = String.format(Locale.US, "%d %s %d %s",
                        hours, hourLabel, minutes, minLabel);
            } else {
                resultText = String.format(Locale.US, "%d %s", hours, hourLabel);
            }
        } else resultText = String.format(Locale.US, "%d %s", minutes, minLabel);
        Spannable span = new SpannableString(resultText);
        applyResizeSpan(span, resultText, hourLabel);
        applyResizeSpan(span, resultText, minLabel);
        return span;
    }

    public static Spannable createSpeedText(Context context, long distance, long duration,
                                            String units) {
        double distanceDouble = computeDistance(distance, units);
        double hours = duration / 60.0;
        double average = distanceDouble / hours;
        String speedUnit;
        switch (units) {
            case KM_UNIT:
                speedUnit = context.getString(R.string.time_entries_list_speed_km);
                break;
            case MILE_UNIT:
                speedUnit = context.getString(R.string.time_entries_list_speed_mi);
                break;
            default:
                speedUnit = "";
                break;
        }
        String speedText = String.format(Locale.US, "%.2f %s", average, speedUnit);
        Spannable span = new SpannableString(speedText);
        applyResizeSpan(span, speedText, speedUnit);
        return span;
    }

    /**
     * Applies a {@link RelativeSizeSpan} to the given {@link Spannable} if the label is found.
     *
     * @param span  current span to apply resize.
     * @param text  full text that may contain the given label.
     * @param label label to find and apply the span to.
     */
    private static void applyResizeSpan(Spannable span, String text, String label) {
        int indexOfLabel = text.indexOf(label);
        if (indexOfLabel != -1) {
            span.setSpan(new RelativeSizeSpan(UNITS_RELATIVE_SIZE),
                    indexOfLabel,
                    indexOfLabel + label.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }

    /**
     * Returns a String representation of the given Calendar in format yyyy-mm-dd.
     *
     * @return string representation of the given date, null if given date is null.
     */
    private String parseDateToString(Calendar date) {
        if (date != null) {
            return String.format(Locale.US, "%d-%d-%d",
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH) + 1,
                    date.get(Calendar.DAY_OF_MONTH));
        } else return null;
    }

    /**
     * Returns a pretty string representation of a given date.
     *
     * @param date date to prettify.
     * @return string representation of the given date.
     */
    private String formatSelectedDateFilter(@NonNull Calendar date) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String outFormatString;
        if (currentYear == date.get(Calendar.YEAR)) {
            outFormatString = mContext.getString(R.string.time_entries_list_without_year_format);
        } else outFormatString = mContext.getString(R.string.time_entries_list_with_year_format);
        return new SimpleDateFormat(outFormatString, Locale.US).format(date.getTime());
    }
}
