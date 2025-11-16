package project.favory.external.spotify.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import java.time.Duration

@Configuration
class SpotifyClientConfig {

    @Bean
    fun spotifyRestClient(
        restClientBuilder: RestClient.Builder,
        @Value("\${spotify.api.base-url}") baseUrl: String,
        @Value("\${spotify.api.connect-timeout}") connectTimeout: Long,
        @Value("\${spotify.api.read-timeout}") readTimeout: Long
    ): RestClient {
        val settings = ClientHttpRequestFactorySettings.defaults()
            .withConnectTimeout(Duration.ofMillis(connectTimeout))
            .withReadTimeout(Duration.ofMillis(readTimeout))

        val requestFactory = ClientHttpRequestFactoryBuilder.detect().build(settings)

        return restClientBuilder
            .baseUrl(baseUrl)
            .requestFactory(requestFactory)
            .build()
    }
}

