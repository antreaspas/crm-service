package com.example.crmservice.service;

import com.example.crmservice.exception.CustomerNotFoundException;
import com.example.crmservice.model.customer.Customer;
import com.example.crmservice.model.customer.CustomerRequest;
import com.example.crmservice.model.customer.CustomerUpdateRequest;
import com.example.crmservice.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static com.example.crmservice.utils.TestUtils.generateMockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PhotoService photoService;
    @InjectMocks
    private CustomerService customerService;

    @Test
    public void testCreateCustomer() {
        CustomerRequest request = CustomerRequest.builder()
                .name("name")
                .surname("surname")
                .build();
        Customer mockedSavedCustomer = Customer.builder()
                .id(1L)
                .name(request.getName())
                .surname(request.getSurname())
                .photoId("photo ID")
                .build();
        when(customerRepository.save(any())).thenReturn(mockedSavedCustomer);
        when(photoService.getPhotoUrlForPhotoId(eq("photo ID")))
                .thenReturn("test URL");
        assertThat(customerService.createCustomer(request))
                .extracting("name", "surname", "photoUrl").containsExactly("name", "surname", "test URL");
    }

    @Test
    public void testDeleteCustomerById() {
        Customer mockedSavedCustomer = Customer.builder()
                .name("name")
                .surname("surname")
                .photoId("photo ID")
                .build();
        when(customerRepository.findById(eq(1L))).thenReturn(Optional.of(mockedSavedCustomer));
        customerService.deleteCustomerById(1L);
        verify(customerRepository).delete(mockedSavedCustomer);
        verify(photoService).deletePhoto("photo ID");
    }

    @Test
    public void testDeleteCustomerByIdThrowsWhenIdDoesNotExist() {
        when(customerRepository.findById(eq(1L))).thenReturn(Optional.empty());
        assertThatExceptionOfType(CustomerNotFoundException.class).isThrownBy(() -> customerService.deleteCustomerById(1L));
        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    public void testRetrieveCustomerById() {
        when(customerRepository.findById(eq(1L))).thenReturn(Optional.of(
                Customer.builder()
                        .name("name")
                        .surname("surname")
                        .build()));
        assertThat(customerService.retrieveCustomerResponseById(1L))
                .extracting("name", "surname")
                .containsExactly("name", "surname");
    }

    @Test
    public void testRetrieveCustomerByIdThrowsWhenIdDoesNotExist() {
        when(customerRepository.findById(eq(1L))).thenReturn(Optional.empty());
        assertThatExceptionOfType(CustomerNotFoundException.class).isThrownBy(
                () -> customerService.retrieveCustomerResponseById(1L));
    }

    @Test
    public void testPatchCustomer() {
        when(customerRepository.findById(eq(1L))).thenReturn(Optional.of(
                Customer.builder()
                        .id(1L)
                        .name("name")
                        .surname("surname")
                        .build()));
        when(customerRepository.save(any())).thenReturn(Customer.builder()
                .id(1L)
                .name("new name")
                .surname("surname")
                .build());
        customerService.patchCustomer(1L, CustomerUpdateRequest.builder()
                .name("new name")
                .build());
        verify(customerRepository).save(argThat(customer -> customer.getName().equals("new name")
                && customer.getSurname().equals("surname")));
    }

    @Test
    public void testUpdateCustomerPhoto() {
        when(customerRepository.findById(eq(1L))).thenReturn(Optional.of(
                Customer.builder()
                        .id(1L)
                        .name("name")
                        .surname("surname")
                        .build()));
        MockMultipartFile mockUpload = generateMockMultipartFile();
        when(photoService.uploadPhoto(null, mockUpload)).thenReturn("photo ID");
        when(photoService.getPhotoUrlForPhotoId("photo ID")).thenReturn("PHOTO URL");
        when(customerRepository.save(any())).thenReturn(Customer.builder()
                .id(1L)
                .name("name")
                .surname("surname")
                .photoId("photo ID")
                .build());
        assertThat(customerService.updateCustomerPhoto(1L, mockUpload))
                .extracting("name", "surname", "photoUrl")
                .containsExactly("name", "surname", "PHOTO URL");
    }

    @Test
    public void testUpdateCustomerPhotoThrowsWhenIdDoesNotExist() {
        when(customerRepository.findById(eq(1L))).thenReturn(Optional.empty());
        assertThatExceptionOfType(CustomerNotFoundException.class).isThrownBy(
                () -> customerService.updateCustomerPhoto(1L, generateMockMultipartFile()));
    }

}
