package com.pgms.storage;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;

/**
 * Storage SPI: S3 adapter implements this.
 */
public interface FileStorage {
    void save(String key, InputStream in, long sizeBytes, String contentType);

    URL presignGet(String key, Duration ttl);

    URL presignPut(String key, Duration ttl, String contentType, long sizeBytes);

    void delete(String key);

    String publicUrl(String key); // returns null if not configured / not public
}
