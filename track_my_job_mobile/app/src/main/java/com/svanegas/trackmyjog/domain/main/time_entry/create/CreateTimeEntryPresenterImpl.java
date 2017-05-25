package com.svanegas.trackmyjog.domain.main.time_entry.create;

import android.text.TextUtils;
import android.util.Log;

import com.svanegas.trackmyjog.TrackMyJogApplication;
import com.svanegas.trackmyjog.interactor.TimeEntryInteractor;
import com.svanegas.trackmyjog.repository.model.APIError;

import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

import static com.svanegas.trackmyjog.network.ConnectionInterceptor.isInternetConnectionError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.isHttpError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.parseHttpError;

public class CreateTimeEntryPresenterImpl implements CreateTimeEntryPresenter {

    private static final String TAG = CreateTimeEntryPresenterImpl.class.getSimpleName();

    private CreateTimeEntryView mView;

    @Inject
    TimeEntryInteractor mInteractor;

    CreateTimeEntryPresenterImpl(CreateTimeEntryView createTimeEntryView) {
        mView = createTimeEntryView;
        TrackMyJogApplication.getInstance().getApplicationComponent().inject(this);
    }

    @Override
    public void validateTimeEntryCreation() {
        mView.showLoadingAndDisableFields();
        Calendar date = mView.date();
        String distance = mView.distance();
        String hours = mView.hours();
        String minutes = mView.minutes();

        int duration = validateDuration(hours, minutes);
        float distanceValue = validateDistance(distance);

        if (distanceValue > 0f && duration > 0) createTimeEntry(date, distance, duration);
        else mView.hideLoadingAndEnableFields();
    }

    private void createTimeEntry(Calendar date, String distance, int duration) {
        String dateString = String.format(Locale.US, "%d-%d-%d",
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH) + 1,
                date.get(Calendar.DAY_OF_MONTH));
        mInteractor.createTimeEntry(dateString, distance, duration)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> mView.onCreationSuccess(),
                        throwable -> {
                            mView.hideLoadingAndEnableFields();
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
                                Log.e(TAG, "Could not create Time Entry due unknown error: ",
                                        throwable);
                                mView.showUnknownError();
                            }
                        });
    }

    /**
     * Validates if the entered duration values are valid
     *
     * @param hours   string representing hours
     * @param minutes string representing minutes
     * @return 0 if duration is invalid, a positive integer otherwise. The positive integer
     * corresponds to the entered duration in minutes.
     */
    private int validateDuration(String hours, String minutes) {
        int total;
        if (TextUtils.isEmpty(hours) || TextUtils.isEmpty(minutes)) {
            mView.showEmptyDurationError();
            return 0;
        } else {
            try {
                int hoursInt = Integer.parseInt(hours);
                int minutesInt = Integer.parseInt(minutes);
                total = hoursInt * 60 + minutesInt;
                if (total <= 0) {
                    mView.showNegativeDurationError();
                    return 0;
                }
            } catch (NumberFormatException e) {
                mView.showInvalidDurationError();
                return 0;
            }
        }
        return total;
    }

    /**
     * Validates if the entered distance value is valid
     *
     * @param distance string representation of the distance
     * @return 0f if the given distance is invalid, a positive floating point number otherwise.
     * The positive floating point number corresponds to the entered distance.
     */
    private float validateDistance(String distance) {
        float distanceFloat;
        if (TextUtils.isEmpty(distance)) {
            mView.showEmptyDistanceError();
            return 0f;
        } else {
            try {
                distanceFloat = Float.valueOf(distance);
                if (distanceFloat <= 0f) {
                    mView.showNegativeDistanceError();
                    return 0f;
                }
            } catch (NumberFormatException e) {
                mView.showInvalidDistanceError();
                return 0f;
            }
        }
        return distanceFloat;
    }
}
