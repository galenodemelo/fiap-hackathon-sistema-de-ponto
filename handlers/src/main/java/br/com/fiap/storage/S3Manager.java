package br.com.fiap.storage;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class S3Manager {

    public void put(String bucket, String filename, String content, String type) {
        try {
            AmazonS3 client = buildClient();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(type);

            client.putObject(bucket, filename, new ByteArrayInputStream(content.getBytes()), metadata);
        } catch (Throwable throwable) {
            // Silence is golden
        }
    }

    public String presignedUrl(String bucket, String filename) {
        final Instant expirationDate = Instant.now().plus(24, ChronoUnit.HOURS);

        AmazonS3 client = buildClient();
        return client.generatePresignedUrl(bucket, filename, new Date(expirationDate.toEpochMilli())).toString();
    }

    private AmazonS3 buildClient() {
        return AmazonS3Client.builder()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }
}
