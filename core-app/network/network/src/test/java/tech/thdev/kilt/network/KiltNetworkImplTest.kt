package tech.thdev.kilt.network

import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import retrofit2.Retrofit

internal class KiltNetworkImplTest {

  private val retrofitBuilder = Retrofit.Builder()
  private val network = KiltNetworkImpl(
    retrofit = retrofitBuilder.baseUrl("https://some.url/").build(),
  )

  @Test
  fun `test create`() = runTest {
    Assert.assertNotNull(network.create(MockService::class.java))
  }
}
