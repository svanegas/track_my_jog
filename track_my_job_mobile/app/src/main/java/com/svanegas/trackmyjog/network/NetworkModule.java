package com.svanegas.trackmyjog.network;

import android.app.Application;

import com.svanegas.trackmyjog.BuildConfig;
import com.svanegas.trackmyjog.repository.landing.AuthenticationService;
import com.svanegas.trackmyjog.util.PreferencesManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkModule {

    @Provides
    @Singleton
    public AuthenticationService provideRegisterService(AuthorizationInterceptor authInterceptor,
                                                        ConnectionInterceptor connectionInterceptor) {
        return new ServiceFactory.Builder()
                .withBaseUrl(BuildConfig.API_BASE_URL)
                .addInterceptor(authInterceptor)
                .addInterceptor(connectionInterceptor)
                .buildService(AuthenticationService.class);
    }

    @Provides
    @Singleton
    public AuthorizationInterceptor provideAuthorizationInterceptor(
            PreferencesManager preferencesManager) {
        return new AuthorizationInterceptor(preferencesManager);
    }

    @Provides
    @Singleton
    public ConnectionInterceptor provideConnectionInterceptor(Application application) {
        return new ConnectionInterceptor(application);
    }
}
