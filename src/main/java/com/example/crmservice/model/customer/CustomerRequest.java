package com.example.crmservice.model.customer;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
@Builder
public class CustomerRequest {

    @NotBlank
    @Size(min = 2, max = 20)
    String name;

    @NotBlank
    @Size(min = 2, max = 20)
    String surname;
}
