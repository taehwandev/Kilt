package tech.thdev.kilt.poke.repository.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.thdev.kilt.network.api.KiltNetwork
import tech.thdev.kilt.poke.repository.api.PokeApiService

@Module
@InstallIn(SingletonComponent::class)
internal class PokeApiModule {

  @Provides
  fun providePokeApiService(network: KiltNetwork): PokeApiService {
    // Retrofit 객체를 사용해 PokeApiService 인터페이스의 구현체를 생성
    return network.create(PokeApiService::class.java)
  }
}
