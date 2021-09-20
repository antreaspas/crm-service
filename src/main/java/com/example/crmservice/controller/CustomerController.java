package com.example.crmservice.controller;

import com.example.crmservice.model.customer.CustomerRequest;
import com.example.crmservice.model.customer.CustomerResponse;
import com.example.crmservice.model.customer.CustomerUpdateRequest;
import com.example.crmservice.service.CustomerService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @ApiOperation(value = "Get all existing customers", notes = "Requires an authenticated user")
    @GetMapping
    public List<CustomerResponse> retrieveAllCustomers() {
        return customerService.retrieveAllCustomers();
    }

    @ApiOperation(value = "Create a new customer", notes = "Requires an authenticated user")
    @PostMapping
    public CustomerResponse createCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        return customerService.createCustomer(customerRequest);
    }

    @ApiOperation(value = "Get an existing customer by their ID", notes = "Requires an authenticated user")
    @GetMapping("/{customerId}")
    public CustomerResponse retrieveCustomerById(@PathVariable Long customerId) {
        return customerService.retrieveCustomerResponseById(customerId);
    }

    @ApiOperation(value = "Update an existing customer by their ID", notes = "Requires an authenticated user")
    @PatchMapping("/{customerId}")
    public CustomerResponse patchCustomer(@PathVariable Long customerId,
                                          @Valid @RequestBody CustomerUpdateRequest customerUpdateRequest) {
        return customerService.patchCustomer(customerId, customerUpdateRequest);
    }

    @ApiOperation(value = "Delete an existing customer by their ID", notes = "Requires an authenticated user")
    @DeleteMapping("/{customerId}")
    public void deleteCustomerById(@PathVariable Long customerId) {
        customerService.deleteCustomerById(customerId);
    }

    @ApiOperation(value = "Update an existing customer's photo by their ID", notes = "Requires an authenticated user")
    @PostMapping(value = "/{customerId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CustomerResponse updateCustomerPhoto(@PathVariable Long customerId,
                                                @RequestPart("photo") MultipartFile photo) {
        return customerService.updateCustomerPhoto(customerId, photo);
    }
}
