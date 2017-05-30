package com.svanegas.trackmyjog.repository;

import com.svanegas.trackmyjog.repository.landing.AuthenticationService;
import com.svanegas.trackmyjog.repository.landing.RemoteAuthenticationRepository;
import com.svanegas.trackmyjog.repository.main.RemoteTimeEntryRepository;
import com.svanegas.trackmyjog.repository.main.RemoteUserRepository;
import com.svanegas.trackmyjog.repository.main.TimeEntryService;
import com.svanegas.trackmyjog.repository.main.UserService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    public RemoteAuthenticationRepository provideAuthenticationRepository(
            AuthenticationService authenticationService) {
        return new RemoteAuthenticationRepository(authenticationService);
    }

    @Provides
    @Singleton
    public RemoteTimeEntryRepository provideTimeEntryRepository(
            TimeEntryService timeEntryService) {
        return new RemoteTimeEntryRepository(timeEntryService);
    }

    @Provides
    @Singleton
    public RemoteUserRepository provideUserRepository(
            UserService userService) {
        return new RemoteUserRepository(userService);
    }
}
