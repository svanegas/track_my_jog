package com.svanegas.trackmyjog;

import com.svanegas.trackmyjog.domain.landing.LandingPresenterImpl;
import com.svanegas.trackmyjog.domain.landing.login.LoginPresenterImpl;
import com.svanegas.trackmyjog.domain.landing.register.RegisterPresenterImpl;
import com.svanegas.trackmyjog.domain.main.MainPresenterImpl;
import com.svanegas.trackmyjog.domain.main.time_entry.create.CreateTimeEntryPresenterImpl;
import com.svanegas.trackmyjog.domain.main.time_entry.list.TimeEntriesListPresenterImpl;
import com.svanegas.trackmyjog.interactor.InteractorsModule;
import com.svanegas.trackmyjog.network.NetworkModule;
import com.svanegas.trackmyjog.repository.RepositoryModule;
import com.svanegas.trackmyjog.util.PreferencesModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, PreferencesModule.class, NetworkModule.class,
        RepositoryModule.class, InteractorsModule.class})
public interface ApplicationComponent {

    void inject(RegisterPresenterImpl registerPresenter);

    void inject(LoginPresenterImpl loginPresenter);

    void inject(LandingPresenterImpl landingPresenter);

    void inject(MainPresenterImpl mainPresenter);

    void inject(TimeEntriesListPresenterImpl timeEntriesListPresenter);

    void inject(CreateTimeEntryPresenterImpl createTimeEntryPresenter);
}
