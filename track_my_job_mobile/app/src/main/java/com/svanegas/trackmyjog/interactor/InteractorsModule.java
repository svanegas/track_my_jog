package com.svanegas.trackmyjog.interactor;

import com.svanegas.trackmyjog.repository.landing.RemoteAuthenticationRepository;
import com.svanegas.trackmyjog.repository.main.RemoteTimeEntryRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class InteractorsModule {

    @Provides
    @Singleton
    public AuthenticationInteractor provideAuthenticationInteractor(
            RemoteAuthenticationRepository remoteRepository) {
        return new AuthenticationInteractor(remoteRepository);
    }

    @Provides
    @Singleton
    public TimeEntryInteractor provideTimeEntryInteractor(
            RemoteTimeEntryRepository remoteRepository) {
        return new TimeEntryInteractor(remoteRepository);
    }
}
