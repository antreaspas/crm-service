package com.example.crmservice.model.customer;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Size;

@Value
@Builder
public class CustomerUpdateRequest {
    @Size(max = 20)
    String name;

    @Size(max = 20)
    String surname;
}
