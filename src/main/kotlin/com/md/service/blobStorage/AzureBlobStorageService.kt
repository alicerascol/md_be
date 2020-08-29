package com.md.service.blobStorage

import com.azure.core.http.rest.PagedIterable
import com.azure.storage.blob.BlobClient
import com.azure.storage.blob.BlobContainerClient
import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.BlobServiceClientBuilder
import com.azure.storage.blob.models.BlobContainerItem
import com.azure.storage.common.StorageSharedKeyCredential
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Component
class AzureBlobStorageService {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AzureBlobStorageService::class.java)
    }

    @Value("\${azureBlobStorage.ACCOUNT_NAME}")
    private val ACCOUNT_NAME: String? = null

    @Value("\${azureBlobStorage.ACCOUNT_KEY}")
    private val ACCOUNT_KEY: String? = null

    private val localPath = "/Users/arascol/Documents/alice/disertatie/be/data/"

    fun createBlobServiceClient(): BlobServiceClient {
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
        return BlobServiceClientBuilder().endpoint(endpoint).credential(credential).buildClient()
    }

    fun createContainerIfNotExists(containerName: String) {
        val storageClient = createBlobServiceClient()
        /*
         * Create a client that references a to-be-created container in your Azure Storage account. This returns a
         * ContainerClient object that wraps the container's endpoint, credential and a request pipeline (inherited from storageClient).
         * Note that container names require lowercase.
         */
        val blobContainerClient = storageClient.getBlobContainerClient(containerName)

        /*
         * Create a container in Storage blob account.
         */
        try {
            blobContainerClient.create()
        } catch (ex: Exception) {
            LOGGER.error(ex.message)
            if(!ex.message?.contains("already exists")!!)  throw ex
        }
    }

    fun uploadNewDetailsFileToContainer(detailsFacultyFile: MultipartFile, containerName: String): String {
        val storageClient = createBlobServiceClient()
        val containerClient: BlobContainerClient = storageClient.getBlobContainerClient(containerName)

        // Create a local file in the ./data/ directory for uploading and downloading
        val filename: String = detailsFacultyFile.originalFilename
        val path: Path = Paths.get(localPath);
        multipartFileToFile(detailsFacultyFile, path)

        // Get a reference to a blob
        val blobClient: BlobClient = containerClient.getBlobClient("config/$filename")

        // Upload the blob
        blobClient.uploadFromFile(localPath + filename, true)
        LOGGER.info("File uploaded in Blob storage as blob: ${blobClient.blobUrl}")

        return blobClient.blobUrl
    }

    fun uploadStudentFilesToContainer(studentFiles: List<MultipartFile>, containerName: String, studentName: String) {
        try {
            val storageClient = createBlobServiceClient()
            val containerClient: BlobContainerClient = storageClient.getBlobContainerClient(containerName)

            for (studentFile in studentFiles) {
                // Create a local file in the ./data/ directory for uploading and downloading
                val filename: String = studentFile.originalFilename
                val path: Path = Paths.get(localPath);
                multipartFileToFile(studentFile, path)

                // Get a reference to a blob
                val blobClient: BlobClient = containerClient.getBlobClient("config/$studentName/$filename")

                // Upload the blob
                blobClient.uploadFromFile(localPath + filename, true)
                LOGGER.info("File uploaded in Blob storage as blob: ${blobClient.blobUrl}")
            }
        } catch (ex: Exception) {
            throw ex
        }
    }

    // download blob
    fun getJsonForFaculty(containerName: String, configFileName: String) {
        val storageClient = createBlobServiceClient()
        val containerClient: BlobContainerClient = storageClient.getBlobContainerClient(containerName)
        val blobClient: BlobClient = containerClient.getBlobClient("config/$configFileName")

        blobClient.downloadToFile(localPath + "config/" + configFileName)
    }

    fun saveStudentDocuments(containerName: String, studentsDocuments: List<MultipartFile>, studentName: String) {
        uploadStudentFilesToContainer(studentsDocuments, containerName, studentName)
    }

    fun getStudentDocuments(containerName: String, studentDirector: String):  ArrayList<String> {
        val storageClient = createBlobServiceClient()
        val containerClient: BlobContainerClient = storageClient.getBlobContainerClient(containerName)
        val blobNames: List<String> = getContainerBlobs(containerName)
        val blobUrls = ArrayList<String>();
        for(blobName in blobNames) {
            if(blobName.contains(studentDirector.take(studentDirector.length-2))) {
                val blobClient: BlobClient = containerClient.getBlobClient(blobName)
                blobUrls.add(blobClient.blobUrl)
            }
        }
        return blobUrls
    }

    fun getAllContainers(): ArrayList<String> {
        val storageClient = createBlobServiceClient()
        val containers: PagedIterable<BlobContainerItem>? = storageClient.listBlobContainers()
        val containersNames = ArrayList<String>()
        // List the blob(s) in the container.
        if (containers != null) {
            for (containerItem in containers) {
                containersNames.add(containerItem.name)
            }
        }
        return containersNames
    }

    fun getContainerBlobs(containerName: String): List<String> {
        val storageClient = createBlobServiceClient()
        val containerClient: BlobContainerClient = storageClient.getBlobContainerClient(containerName)

        val blobNames = ArrayList<String>()
        // List the blob(s) in the container.
        for (blobItem in containerClient.listBlobs()) {
            blobNames.add(blobItem.name)
        }
        return blobNames
    }

    @Throws(IOException::class)
    fun multipartFileToFile(multipart: MultipartFile, dir: Path) {
        val filepath: Path = Paths.get(dir.toString(), multipart.originalFilename)
        multipart.transferTo(filepath)
    }

}