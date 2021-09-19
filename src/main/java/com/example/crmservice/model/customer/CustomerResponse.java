package com.example.crmservice.model.customer;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class CustomerResponse {
    Long id;
    String name;
    String surname;
    String photoUrl;
    String createdBy;
    Instant createdAt;
    String modifiedBy;
    Instant modifiedAt;
}
