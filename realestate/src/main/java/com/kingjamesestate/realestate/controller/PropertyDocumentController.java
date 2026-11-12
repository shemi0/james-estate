package com.kingjamesestate.realestate.controller;

import com.kingjamesestate.realestate.model.Property;
import com.kingjamesestate.realestate.model.PropertyDocument;
import com.kingjamesestate.realestate.repository.PropertyDocumentRepository;
import com.kingjamesestate.realestate.repository.PropertyRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class PropertyDocumentController {

    private final PropertyDocumentRepository documentRepository;
    private final PropertyRepository propertyRepository;

    public PropertyDocumentController(PropertyDocumentRepository documentRepository,
                                      PropertyRepository propertyRepository) {
        this.documentRepository = documentRepository;
        this.propertyRepository = propertyRepository;
    }

    // Get all documents for a specific property
    @GetMapping("/property/{propertyId}")
    public List<PropertyDocument> getDocumentsByProperty(@PathVariable UUID propertyId) {
        return documentRepository.findByPropertyId(propertyId);
    }

    // Add a new document to a property
    @PostMapping
    public PropertyDocument addDocument(@RequestBody PropertyDocument document) {
        UUID propertyId = document.getProperty().getId();
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        document.setProperty(property);
        return documentRepository.save(document);
    }
}