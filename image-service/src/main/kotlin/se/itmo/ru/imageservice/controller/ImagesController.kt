package se.itmo.ru.imageservice.controller

import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import se.itmo.ru.imageservice.service.MinioService


@RestController
@RequestMapping("/images")
class ImagesController(
    val minioService: MinioService
) {

    @GetMapping("/{fileName}", produces = ["application/json"])
    fun download(
        @PathVariable("fileName") fileName: String
    ): ResponseEntity<InputStreamResource> {
        val inputStream = minioService.download(fileName)
        val contentType = minioService.getFileContentType(fileName)
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$fileName\"")
            .body(InputStreamResource(inputStream))
    }

    @PostMapping(produces = ["application/json"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(
        @RequestPart("file") file: MultipartFile
    ): ResponseEntity<String> {
        return minioService.upload(
            file.originalFilename!!,
            file.inputStream,
            file.contentType!!
        ).let { ResponseEntity.ok(it) }
    }
}