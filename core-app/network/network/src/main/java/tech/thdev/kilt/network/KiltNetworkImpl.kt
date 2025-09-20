package tech.thdev.kilt.network

import retrofit2.Retrofit
import tech.thdev.kilt.network.api.KiltNetwork
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class KiltNetworkImpl @Inject constructor(
    private val retrofit: Retrofit,
) : KiltNetwork {

    override fun <T> create(service: Class<T>): T =
        retrofit
            .create(service)
}
