package com.example.crmservice.controller;

import com.example.crmservice.model.customer.CustomerRequest;
import com.example.crmservice.model.customer.CustomerResponse;
import com.example.crmservice.model.customer.CustomerUpdateRequest;
import com.example.crmservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public List<CustomerResponse> retrieveAllCustomers() {
        return customerService.retrieveAllCustomers();
    }

    @PostMapping
    public CustomerResponse createCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        return customerService.createCustomer(customerRequest);
    }

    @GetMapping("/{customerId}")
    public CustomerResponse retrieveCustomerById(@PathVariable Long customerId) {
        return customerService.retrieveCustomerResponseById(customerId);
    }

    @PatchMapping("/{customerId}")
    public CustomerResponse patchCustomer(@PathVariable Long customerId,
                                          @Valid @RequestBody CustomerUpdateRequest customerUpdateRequest) {
        return customerService.patchCustomer(customerId, customerUpdateRequest);
    }

    @DeleteMapping("/{customerId}")
    public void deleteCustomerById(@PathVariable Long customerId) {
        customerService.deleteCustomerById(customerId);
    }

    @PostMapping("/{customerId}/photo")
    public CustomerResponse updateCustomerPhoto(@PathVariable Long customerId,
                                                @RequestParam("photo") MultipartFile photo) {
        return customerService.updateCustomerPhoto(customerId, photo);
    }
}
