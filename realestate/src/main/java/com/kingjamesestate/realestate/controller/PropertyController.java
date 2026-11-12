package com.kingjamesestate.realestate.controller;

import com.kingjamesestate.realestate.model.Property;
import com.kingjamesestate.realestate.model.PropertyImage;
import com.kingjamesestate.realestate.model.User;
import com.kingjamesestate.realestate.repository.PropertyDocumentRepository;
import com.kingjamesestate.realestate.repository.PropertyRepository;
import com.kingjamesestate.realestate.repository.UserRepository;
import com.kingjamesestate.realestate.service.ObjectStorage;
import com.kingjamesestate.realestate.service.PublicListingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PropertyDocumentRepository documentRepository;
    private final ObjectStorage objectStorage;
    private final PublicListingService publicListingService;

    public PropertyController(PropertyRepository propertyRepository,
                              UserRepository userRepository,
                              PropertyDocumentRepository documentRepository,
                              ObjectStorage objectStorage,
                              PublicListingService publicListingService) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.objectStorage = objectStorage;
        this.publicListingService = publicListingService;
    }

    @GetMapping
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    @GetMapping("/agent/{agentId}")
    public List<Property> getPropertiesByAgent(@PathVariable UUID agentId) {
        return propertyRepository.findByAgentId(agentId);
    }

    @GetMapping("/{id}")
    public Property getPropertyById(@PathVariable UUID id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
    }

    @PostMapping
    public Property createProperty(@RequestBody Property property) {
        property.setId(null);
        property.setAgent(resolveAgent(property.getAgent()));
        property.setCreatedAt(LocalDateTime.now());
        property.setUpdatedAt(LocalDateTime.now());
        Property saved = propertyRepository.save(property);
        publicListingService.evict();
        return saved;
    }

    @PutMapping("/{id}")
    public Property updateProperty(@PathVariable UUID id, @RequestBody Property updates) {
        Property existing = propertyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));

        existing.setTitle(updates.getTitle());
        existing.setDescription(updates.getDescription());
        existing.setListingType(updates.getListingType());
        existing.setPropertyType(updates.getPropertyType());
        existing.setPrice(updates.getPrice());
        existing.setPriceType(updates.getPriceType());
        existing.setAddress(updates.getAddress());
        existing.setCity(updates.getCity());
        existing.setState(updates.getState());
        existing.setLatitude(updates.getLatitude());
        existing.setLongitude(updates.getLongitude());
        existing.setBedrooms(updates.getBedrooms());
        existing.setBathrooms(updates.getBathrooms());
        existing.setStatus(updates.getStatus() == null ? existing.getStatus() : updates.getStatus());
        existing.setTitleVerified(updates.isTitleVerified());
        if (updates.getAgent() != null && updates.getAgent().getId() != null) {
            existing.setAgent(resolveAgent(updates.getAgent()));
        }
        existing.setUpdatedAt(LocalDateTime.now());
        Property saved = propertyRepository.save(existing);
        publicListingService.evict();
        return saved;
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<PropertyImage> addImages(@PathVariable UUID id, @RequestParam("files") List<MultipartFile> files) throws IOException {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
        if (files == null || files.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose at least one photo");
        }
        if (property.getImages().size() + files.size() > 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A listing can have up to 8 photos");
        }

        int order = property.getImages().size();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            String type = file.getContentType() == null ? "" : file.getContentType();
            String extension = switch (type) {
                case "image/png" -> ".png";
                case "image/webp" -> ".webp";
                case "image/gif" -> ".gif";
                case "image/jpeg" -> ".jpg";
                default -> null;
            };
            if (extension == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Photos must be JPG, PNG, WEBP, or GIF");
            }
            String key = "properties/" + UUID.randomUUID() + extension;
            String url = objectStorage.store(key, file.getInputStream(), file.getSize(), type);

            PropertyImage image = new PropertyImage();
            image.setProperty(property);
            image.setFileName(key);
            image.setUrl(url);
            image.setSortOrder(order++);
            property.getImages().add(image);
        }
        List<PropertyImage> saved = propertyRepository.save(property).getImages();
        publicListingService.evict();
        return saved;
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Map<String, Object> deleteProperty(@PathVariable UUID id) throws IOException {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
        for (PropertyImage image : property.getImages()) {
            objectStorage.delete(image.getFileName());
        }
        documentRepository.deleteByPropertyId(id);
        propertyRepository.delete(property);
        publicListingService.evict();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Property deleted");
        return response;
    }

    private User resolveAgent(User agent) {
        if (agent == null || agent.getId() == null) {
            return null;
        }
        return userRepository.findById(agent.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Agent not found"));
    }
}
