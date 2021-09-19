package com.example.crmservice.service;

import com.example.crmservice.exception.CustomerNotFoundException;
import com.example.crmservice.model.customer.Customer;
import com.example.crmservice.model.customer.CustomerRequest;
import com.example.crmservice.model.customer.CustomerResponse;
import com.example.crmservice.model.customer.CustomerUpdateRequest;
import com.example.crmservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PhotoService photoService;

    public CustomerResponse createCustomer(CustomerRequest customerRequest) {
        Customer customer = Customer.builder()
                .name(customerRequest.getName())
                .surname(customerRequest.getSurname())
                .build();
        return toResponse(customerRepository.save(customer));
    }

    public List<CustomerResponse> retrieveAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CustomerResponse retrieveCustomerResponseById(Long id) {
        return toResponse(retrieveCustomerById(id));
    }

    private Customer retrieveCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(CustomerNotFoundException::new);
    }

    public void deleteCustomerById(Long id) {
        Customer customer = retrieveCustomerById(id);
        photoService.deletePhoto(customer.getPhotoId());
        customerRepository.delete(customer);
    }

    public CustomerResponse patchCustomer(Long id, CustomerUpdateRequest updates) {
        Customer customer = retrieveCustomerById(id);
        if (updates.getName() != null) customer.setName(updates.getName());
        if (updates.getSurname() != null) customer.setSurname(updates.getSurname());
        return toResponse(customerRepository.save(customer));
    }

    public CustomerResponse updateCustomerPhoto(Long id, MultipartFile photo) {
        Customer customer = retrieveCustomerById(id);
        String photoId = photoService.uploadPhoto(customer.getPhotoId(), photo);
        customer.setPhotoId(photoId);
        return toResponse(customerRepository.save(customer));
    }

    public CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surname(customer.getSurname())
                .photoUrl(photoService.getPhotoUrlForPhotoId(customer.getPhotoId()))
                .createdBy(customer.getCreatedBy())
                .createdAt(customer.getCreatedAt())
                .modifiedBy(customer.getModifiedBy())
                .modifiedAt(customer.getModifiedAt())
                .build();
    }
}
