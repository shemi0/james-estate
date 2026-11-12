package com.kingjamesestate.realestate.repository;

import com.kingjamesestate.realestate.model.PropertyDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface PropertyDocumentRepository extends JpaRepository<PropertyDocument, UUID> {
    List<PropertyDocument> findByPropertyId(UUID propertyId);

    @Transactional
    long deleteByPropertyId(UUID propertyId);
}