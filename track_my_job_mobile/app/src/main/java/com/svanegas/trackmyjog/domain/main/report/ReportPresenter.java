package com.svanegas.trackmyjog.domain.main.report;

import java.util.Calendar;

interface ReportPresenter {

    void fetchReport(Calendar date, boolean pulledToRefresh);
}
