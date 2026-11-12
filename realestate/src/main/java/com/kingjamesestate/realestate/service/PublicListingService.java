package com.kingjamesestate.realestate.service;

import com.kingjamesestate.realestate.dto.PublicPropertyResponse;
import com.kingjamesestate.realestate.model.Property;
import com.kingjamesestate.realestate.model.User;
import com.kingjamesestate.realestate.repository.PropertyRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class PublicListingService {

    private final PropertyRepository propertyRepository;

    public PublicListingService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "publicListings")
    public List<PublicPropertyResponse> list() {
        return propertyRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "publicProperty", key = "#id")
    public PublicPropertyResponse get(UUID id) {
        return propertyRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
    }

    @CacheEvict(cacheNames = {"publicListings", "publicProperty"}, allEntries = true)
    public void evict() {
    }

    private PublicPropertyResponse toResponse(Property property) {
        User agent = property.getAgent();
        List<PublicPropertyResponse.PhotoResponse> photos = property.getImages().stream()
                .map(image -> new PublicPropertyResponse.PhotoResponse(image.getUrl()))
                .toList();
        PublicPropertyResponse.AgentResponse agentResponse = agent == null ? null
                : new PublicPropertyResponse.AgentResponse(agent.getFullName(), agent.getPhone(), agent.getEmail());
        return new PublicPropertyResponse(
                property.getId(),
                property.getTitle(),
                property.getDescription(),
                property.getListingType(),
                property.getPropertyType(),
                property.getPrice(),
                property.getPriceType(),
                property.getAddress(),
                property.getCity(),
                property.getState(),
                property.getLatitude(),
                property.getLongitude(),
                property.getBedrooms(),
                property.getBathrooms(),
                property.getStatus(),
                property.isTitleVerified(),
                photos,
                agentResponse
        );
    }
}
