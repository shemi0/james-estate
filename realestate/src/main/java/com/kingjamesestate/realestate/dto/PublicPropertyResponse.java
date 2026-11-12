package com.kingjamesestate.realestate.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PublicPropertyResponse(
        UUID id,
        String title,
        String description,
        String listingType,
        String propertyType,
        BigDecimal price,
        String priceType,
        String address,
        String city,
        String state,
        Double latitude,
        Double longitude,
        Integer bedrooms,
        Integer bathrooms,
        String status,
        boolean titleVerified,
        List<PhotoResponse> images,
        AgentResponse agent
) {
    public record PhotoResponse(String url) {}

    public record AgentResponse(String fullName, String phone, String email) {}
}
