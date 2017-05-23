package com.svanegas.trackmyjog.repository;

import com.svanegas.trackmyjog.repository.landing.AuthenticationService;
import com.svanegas.trackmyjog.repository.landing.RemoteAuthenticationRepository;

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
}
