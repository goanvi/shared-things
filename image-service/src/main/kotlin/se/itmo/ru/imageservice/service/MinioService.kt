package se.itmo.ru.imageservice.service

import io.minio.*
import io.minio.errors.ErrorResponseException
import jakarta.ws.rs.NotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.InputStream
import java.util.*


@Service
class MinioService(
    private val minioClient: MinioClient,
    @Value("\${app.minio.bucket}")
    val bucketName: String
) {

    fun upload(filename: String, inputStream: InputStream, contentType: String): String {
        val objectName = UUID.randomUUID().toString() + "." + filename
        ensureBucketExists()
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .`object`(objectName)
                .stream(inputStream, -1, 10485760)
                .contentType(contentType)
                .build()
        )
        return objectName
    }

    fun download(objectName: String?): InputStream {
        try {
            ensureBucketExists()
            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(objectName)
                    .build()
            )
        } catch (e: ErrorResponseException) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                throw NotFoundException("Image not found with name=%s".formatted(objectName))
            }
            throw RuntimeException(e)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun getFileContentType(objectName: String?): String {
        try {
            val stat = minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(objectName)
                    .build()
            )
            return stat.contentType()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @Throws(Exception::class)
    private fun ensureBucketExists() {
        val isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())
        if (!isExist) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
        }
    }
}