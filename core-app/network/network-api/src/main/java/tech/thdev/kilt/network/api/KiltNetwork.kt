package tech.thdev.kilt.network.api

interface KiltNetwork {

    fun <T> create(service: Class<T>): T
}
