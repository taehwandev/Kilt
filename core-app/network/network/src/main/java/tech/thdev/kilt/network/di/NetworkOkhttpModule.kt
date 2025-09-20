package tech.thdev.kilt.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import tech.thdev.kilt.network.BuildConfig
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkOkhttpModule {

    @Provides
    @Singleton
    @IntoSet
    fun providerHttpLoggingInterceptor(): Interceptor =
        HttpLoggingInterceptor {
            if (BuildConfig.DEBUG) {
                println(it)
            }
        }.apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
}
