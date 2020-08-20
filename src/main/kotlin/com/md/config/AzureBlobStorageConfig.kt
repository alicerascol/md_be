package com.md.config

import com.azure.core.util.Configuration;
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AzureBlobStorageConfig {

    @Value("\${azureBlobStorage.ACCOUNT_NAME}")
    private val ACCOUNT_NAME: String? = null

    @Value("\${azureBlobStorage.ACCOUNT_KEY}")
    private val ACCOUNT_KEY: String? = null

    fun getAccountName(): String? {
        return Configuration.getGlobalConfiguration()
            .get(ACCOUNT_NAME)
    }

    fun getAccountKey(): String? {
        return Configuration.getGlobalConfiguration()
            .get(ACCOUNT_KEY)
    }
}