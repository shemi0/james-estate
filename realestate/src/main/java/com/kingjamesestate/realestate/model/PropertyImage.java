package com.kingjamesestate.realestate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "property_images")
public class PropertyImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String url;

    private int sortOrder;

    public PropertyImage() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    @JsonIgnore
    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }

    @JsonIgnore
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
