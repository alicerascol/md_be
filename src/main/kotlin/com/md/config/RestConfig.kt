package com.md.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate


@Configuration
class RestConfig {

    private val LOGGER: Logger = LoggerFactory.getLogger(RestConfig::class.java)

    /**
     * Instantiate a RestTemplate providing a request factory from the tracing library.
     *
     * @return RestTemplate instance
     */
    @Bean
    fun restTemplate(): RestTemplate? {
        LOGGER.info("return restTemplate")
        return RestTemplate()
    }

}