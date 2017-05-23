package com.svanegas.trackmyjog.interactor;

import com.svanegas.trackmyjog.domain.landing.login.interactor.LoginInteractor;
import com.svanegas.trackmyjog.domain.landing.register.interactor.RegisterInteractor;
import com.svanegas.trackmyjog.repository.landing.RemoteAuthenticationRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class InteractorsModule {

    @Provides
    @Singleton
    public RegisterInteractor provideRegisterInteractor(RemoteAuthenticationRepository remoteRepository) {
        return new RegisterInteractor(remoteRepository);
    }

    @Provides
    @Singleton
    public LoginInteractor provideLoginInteractor(RemoteAuthenticationRepository remoteRepository) {
        return new LoginInteractor(remoteRepository);
    }
}
