package tech.thdev.kilt.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import tech.thdev.kilt.network.KiltNetworkImpl
import tech.thdev.kilt.network.api.KiltNetwork
import java.util.concurrent.TimeUnit
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            coerceInputValues = true
            ignoreUnknownKeys = true
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        interceptors: Provider<Set<@JvmSuppressWildcards Interceptor>>,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .apply {
                interceptors.get().forEach { addInterceptor(it) }
                connectTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS)
                readTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS)
                writeTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS)
            }
            .build()

    @Provides
    @Singleton
    fun providerRetrofit(
        json: Json,
        okHttpClient: OkHttpClient,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun providerJourneyMapper(
        retrofit: Retrofit,
    ): KiltNetwork =
        KiltNetworkImpl(retrofit)

    private const val REQUEST_TIME_OUT = 60L
}
