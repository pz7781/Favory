package project.favory.external.tmdb.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder
import java.time.Duration

@Configuration
class TmdbClientConfig {

    @Bean
    fun tmdbRestClient(
        restClientBuilder: RestClient.Builder,
        @Value("\${tmdb.api.base-url}") baseUrl: String,
        @Value("\${tmdb.api.key}") apiKey: String,
        @Value("\${tmdb.api.connect-timeout}") connectTimeout: Long,
        @Value("\${tmdb.api.read-timeout}") readTimeout: Long
    ): RestClient {
        val settings = ClientHttpRequestFactorySettings.defaults()
            .withConnectTimeout(Duration.ofMillis(connectTimeout))
            .withReadTimeout(Duration.ofMillis(readTimeout))

        val requestFactory = ClientHttpRequestFactoryBuilder.detect().build(settings)

        val apiKeyInterceptor = ClientHttpRequestInterceptor { request, body, execution ->
            val modifiedRequest = object : HttpRequest by request {
                override fun getURI() = UriComponentsBuilder.fromUri(request.uri)
                    .queryParam("api_key", apiKey)
                    .build()
                    .toUri()
            }
            execution.execute(modifiedRequest, body)
        }

        return restClientBuilder
            .baseUrl(baseUrl)
            .requestFactory(requestFactory)
            .requestInterceptor(apiKeyInterceptor)
            .build()
    }
}


