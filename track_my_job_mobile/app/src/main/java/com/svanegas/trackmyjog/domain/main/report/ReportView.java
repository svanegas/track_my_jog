package com.svanegas.trackmyjog.domain.main.report;

import android.text.Spannable;

import com.svanegas.trackmyjog.repository.model.Report;

interface ReportView {

    void populateDateRange(String dateFrom, String dateTo);

    void populateEmpty();

    void populateReport(Report report);

    void populateDistance(Spannable spannable);

    void populateDuration(Spannable spannable);

    void populateSpeed(Spannable spannable);

    void showLoadingAndDisableFields(boolean pulledToRefresh);

    void hideLoadingAndEnableFields(boolean pulledToRefresh);

    void showTimeoutError();

    void showNoConnectionError();

    void showDisplayableError(String errorMessage);

    void showUnableToParseDateError();

    void showUnknownError();
}
