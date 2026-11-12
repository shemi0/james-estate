package com.kingjamesestate.realestate.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private User agent;

    @Column(columnDefinition = "TEXT")
    private String description;

    // SALE, RENT, HOTEL
    @Column(nullable = false)
    private String listingType = "SALE";

    // House, Apartment, Land, Hotel Room, etc.
    private String propertyType;

    @Column(nullable = false)
    private BigDecimal price;

    // TOTAL (for sale), MONTHLY (for rent), NIGHTLY (for hotel)
    private String priceType = "TOTAL";

    private String address;
    private String city;
    private String state;

    private Double latitude;
    private Double longitude;

    private Integer bedrooms;
    private Integer bathrooms;

    // AVAILABLE, RENTED, SOLD, BOOKED
    private String status = "AVAILABLE";

    @Column(name = "title_verified")
    private boolean titleVerified = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder asc")
    private List<PropertyImage> images = new ArrayList<>();

    public User getAgent() {
        return agent;
    }

    public void setAgent(User agent) {
        this.agent = agent;
    }
    // Constructors
    public Property() {}

    // ================== Getters and Setters ==================

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getListingType() { return listingType; }
    public void setListingType(String listingType) { this.listingType = listingType; }

    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getPriceType() { return priceType; }
    public void setPriceType(String priceType) { this.priceType = priceType; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Integer getBedrooms() { return bedrooms; }
    public void setBedrooms(Integer bedrooms) { this.bedrooms = bedrooms; }

    public Integer getBathrooms() { return bathrooms; }
    public void setBathrooms(Integer bathrooms) { this.bathrooms = bathrooms; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isTitleVerified() { return titleVerified; }
    public void setTitleVerified(boolean titleVerified) { this.titleVerified = titleVerified; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<PropertyImage> getImages() { return images; }
    public void setImages(List<PropertyImage> images) { this.images = images; }
}