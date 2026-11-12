package com.kingjamesestate.realestate.controller;

import com.kingjamesestate.realestate.service.ObjectStorage;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/media")
public class MediaController {

    private final ObjectStorage objectStorage;

    public MediaController(ObjectStorage objectStorage) {
        this.objectStorage = objectStorage;
    }

    @GetMapping("/**")
    public ResponseEntity<InputStreamResource> open(jakarta.servlet.http.HttpServletRequest request) {
        String prefix = "/media/";
        String uri = request.getRequestURI();
        String key = uri.startsWith(prefix) ? uri.substring(prefix.length()) : uri;
        ResponseInputStream<GetObjectResponse> body = objectStorage.open(key);
        String type = body.response().contentType();
        MediaType mediaType = type == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(type);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic())
                .body(new InputStreamResource(body));
    }
}
