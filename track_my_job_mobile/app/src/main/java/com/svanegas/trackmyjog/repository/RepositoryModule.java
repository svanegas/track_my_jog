package com.svanegas.trackmyjog.repository;

import com.svanegas.trackmyjog.repository.landing.register.RegisterService;
import com.svanegas.trackmyjog.repository.landing.register.RemoteRegisterRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    public RemoteRegisterRepository provideRegisterRepository(RegisterService registerService) {
        return new RemoteRegisterRepository(registerService);
    }
}
