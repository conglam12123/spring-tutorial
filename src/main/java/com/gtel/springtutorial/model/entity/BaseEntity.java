package com.gtel.springtutorial.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@MappedSuperclass //thể hiện là 1 base class
@Data
public class BaseEntity {

    @CreationTimestamp
    @Column(name="created_at", updatable = false)
    private Instant createdAt;
    @Column(name="updated_at", updatable = false)

    private Instant updatedAt;
}
