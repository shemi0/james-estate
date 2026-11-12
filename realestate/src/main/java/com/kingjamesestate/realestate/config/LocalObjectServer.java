package com.kingjamesestate.realestate.config;

import org.gaul.s3proxy.AuthenticationType;
import org.gaul.s3proxy.S3Proxy;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@Configuration
@ConditionalOnProperty(name = "app.storage.endpoint", havingValue = "http://127.0.0.1:9000")
public class LocalObjectServer {

    @Bean(destroyMethod = "stop")
    public S3Proxy s3Proxy(
            @Value("${app.storage.endpoint}") String endpoint,
            @Value("${app.storage.access-key}") String accessKey,
            @Value("${app.storage.secret-key}") String secretKey) throws Exception {
        Path data = Path.of(System.getProperty("user.home"), "AppData", "Local", "james-estate-objects");
        Files.createDirectories(data);

        Properties overrides = new Properties();
        overrides.setProperty("jclouds.filesystem.basedir", data.toString().replace('\\', '/'));

        BlobStoreContext context = ContextBuilder.newBuilder("filesystem-nio2")
                .credentials("local", "local")
                .overrides(overrides)
                .build(BlobStoreContext.class);

        S3Proxy proxy = S3Proxy.builder()
                .blobStore(context.getBlobStore())
                .endpoint(URI.create(endpoint))
                .awsAuthentication(AuthenticationType.AWS_V2_OR_V4, accessKey, secretKey)
                .build();
        proxy.start();
        return proxy;
    }
}
