package com.example.crmservice.model.customer;

import com.example.crmservice.util.SecurityUtils;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 20)
    private String surname;

    private String photoId;

    @Column(nullable = false, updatable = false, length = 20)
    private String createdBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false, length = 20)
    private String modifiedBy;

    @Column(nullable = false)
    private Instant modifiedAt;

    @PrePersist
    public void prePersist() {
        String currentUserName = SecurityUtils.getCurrentUsername();
        Instant currentTimestamp = Instant.now();
        createdBy = currentUserName;
        modifiedBy = currentUserName;
        createdAt = currentTimestamp;
        modifiedAt = currentTimestamp;
    }

    @PreUpdate
    public void preUpdate() {
        modifiedBy = SecurityUtils.getCurrentUsername();
        modifiedAt = Instant.now();
    }
}
