package com.kingjamesestate.realestate.repository;

import com.kingjamesestate.realestate.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PropertyRepository extends JpaRepository<Property, UUID> {
    List<Property> findByAgentId(UUID agentId);
}