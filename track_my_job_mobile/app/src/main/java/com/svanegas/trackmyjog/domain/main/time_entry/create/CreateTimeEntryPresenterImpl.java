package com.svanegas.trackmyjog.domain.main.time_entry.create;

public class CreateTimeEntryPresenterImpl implements CreateTimeEntryPresenter {

    private CreateTimeEntryView mView;

    CreateTimeEntryPresenterImpl(CreateTimeEntryView createTimeEntryView) {
        mView = createTimeEntryView;
    }
}
