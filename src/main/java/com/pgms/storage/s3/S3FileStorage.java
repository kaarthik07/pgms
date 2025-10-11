package com.pgms.storage.s3;

import com.pgms.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;

/**
 * S3 implementation using AWS SDK v2.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "pgms.storage.provider", havingValue = "s3")
public class S3FileStorage implements FileStorage {

    private final S3Client s3;
    private final S3Presigner presigner;

    @Value("${pgms.storage.s3.bucket}")
    private String bucket;

    @Value("${pgms.storage.s3.publicBaseUrl:}")
    private String publicBaseUrl; // optional CDN/domain for public assets

    @Override
    public void save(String key, InputStream in, long sizeBytes, String contentType) {
        log.info("S3 put: bucket={}, key={}, size={}", bucket, key, sizeBytes);
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();
        s3.putObject(req, RequestBody.fromInputStream(in, sizeBytes));
    }

    @Override
    public URL presignGet(String key, Duration ttl) {
        GetObjectRequest get = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        GetObjectPresignRequest pre = GetObjectPresignRequest.builder()
                .getObjectRequest(get)
                .signatureDuration(ttl)
                .build();
        return presigner.presignGetObject(pre).url();
    }

    @Override
    public URL presignPut(String key, Duration ttl, String contentType, long sizeBytes) {
        PutObjectRequest put = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .contentLength(sizeBytes)
                .build();
        PutObjectPresignRequest pre = PutObjectPresignRequest.builder()
                .putObjectRequest(put)
                .signatureDuration(ttl)
                .build();
        return presigner.presignPutObject(pre).url();
    }

    @Override
    public void delete(String key) {
        log.info("S3 delete: bucket={}, key={}", bucket, key);
        s3.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
    }

    @Override
    public String publicUrl(String key) {
        if (publicBaseUrl == null || publicBaseUrl.isBlank()) return null;
        return publicBaseUrl.endsWith("/") ? publicBaseUrl + key : publicBaseUrl + "/" + key;
    }
}
