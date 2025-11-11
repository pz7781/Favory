package project.favory.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.*

@Service
class S3Service(
    private val s3Client: S3Client,
    @Value("\${aws.s3.bucket}") private val bucketName: String,
    @Value("\${aws.s3.region}") private val region: String
) {

    fun uploadFile(file: MultipartFile, directory: String): String {
        val fileName = "${directory}/${UUID.randomUUID()}-${file.originalFilename}"

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .contentType(file.contentType)
            .build()

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.bytes))

        return "https://${bucketName}.s3.${region}.amazonaws.com/${fileName}"
    }

    fun getPublicUrl(fileName: String): String {
        return "https://${bucketName}.s3.${region}.amazonaws.com/${fileName}"
    }

    fun deleteFile(fileUrl: String) {
        val fileName = fileUrl.substringAfter("${bucketName}.s3.${region}.amazonaws.com/")

        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .build()

        s3Client.deleteObject(deleteObjectRequest)
    }
}
