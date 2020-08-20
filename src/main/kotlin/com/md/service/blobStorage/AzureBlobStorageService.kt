package com.md.service.blobStorage

import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.BlobServiceClientBuilder
import com.azure.storage.common.StorageSharedKeyCredential
import com.md.config.AzureBlobStorageConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*


@Component
class AzureBlobStorageService (
    val azureBlogStorageConfig: AzureBlobStorageConfig
){

    @Value("\${azureBlobStorage.ACCOUNT_NAME}")
    private val ACCOUNT_NAME: String? = null

    @Value("\${azureBlobStorage.ACCOUNT_KEY}")
    private val ACCOUNT_KEY: String? = null

    fun createContainer(containerName: String) {
        /*
         * From the Azure portal, get your Storage account's name and account key.
         */
        val accountName: String? = ACCOUNT_NAME
        val accountKey: String? = ACCOUNT_KEY

        val credential = StorageSharedKeyCredential(accountName, accountKey)
        val endpoint = String.format(Locale.ROOT, "https://%s.blob.core.windows.net", accountName)

        /*
         * Create a BlobServiceClient object that wraps the service endpoint, credential and a request pipeline.
         */
        val storageClient: BlobServiceClient =
            BlobServiceClientBuilder().endpoint(endpoint).credential(credential).buildClient()


        /*
         * Create a client that references a to-be-created container in your Azure Storage account. This returns a
         * ContainerClient object that wraps the container's endpoint, credential and a request pipeline (inherited from storageClient).
         * Note that container names require lowercase.
         */
        val blobContainerClient =
            storageClient.getBlobContainerClient(containerName)

        /*
         * Create a container in Storage blob account.
         */

        blobContainerClient.create()
    }

}