package com.svanegas.trackmyjog.interactor;

import com.svanegas.trackmyjog.domain.landing.register.interactors.RegisterInteractor;
import com.svanegas.trackmyjog.repository.landing.register.RemoteRegisterRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class InteractorsModule {

    // Register
    @Provides
    @Singleton
    public RegisterInteractor provideRegisterInteractor(RemoteRegisterRepository remoteRepository) {
        return new RegisterInteractor(remoteRepository);
    }
}
