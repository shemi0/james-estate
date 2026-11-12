package com.kingjamesestate.realestate.controller;

import com.kingjamesestate.realestate.dto.PublicPropertyResponse;
import com.kingjamesestate.realestate.service.PublicListingService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/public/properties")
public class PublicPropertyController {

    private final PublicListingService publicListingService;

    public PublicPropertyController(PublicListingService publicListingService) {
        this.publicListingService = publicListingService;
    }

    @GetMapping
    public ResponseEntity<List<PublicPropertyResponse>> list() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS).cachePublic())
                .body(publicListingService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicPropertyResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS).cachePublic())
                .body(publicListingService.get(id));
    }
}
