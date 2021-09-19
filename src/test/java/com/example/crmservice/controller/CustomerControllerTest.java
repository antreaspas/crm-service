package com.example.crmservice.controller;

import com.example.crmservice.exception.CustomerNotFoundException;
import com.example.crmservice.model.customer.CustomerRequest;
import com.example.crmservice.model.customer.CustomerResponse;
import com.example.crmservice.model.customer.CustomerUpdateRequest;
import com.example.crmservice.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.example.crmservice.utils.TestUtils.generateMockMultipartFile;
import static com.example.crmservice.utils.TestUtils.toJson;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    // Authentication tests - one endpoint should be enough as we apply role based security on the controller
    // and not its individual methods

    @Test
    public void testGetCustomersUnauthenticated() throws Exception {
        mockMvc.perform(get("/v1/customers"))
                .andExpect(status().isUnauthorized());
    }

    // Normal tests
    @Test
    @WithMockUser
    public void testGetCustomersAuthenticated() throws Exception {
        when(customerService.retrieveAllCustomers())
                .thenReturn(List.of(CustomerResponse.builder().build(), CustomerResponse.builder().build()));
        mockMvc.perform(get("/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser
    public void testCreateCustomer() throws Exception {
        when(customerService.createCustomer(any()))
                .thenReturn(CustomerResponse.builder().build());
        CustomerRequest request = CustomerRequest.builder()
                .name("name")
                .surname("surname")
                .build();
        mockMvc.perform(post("/v1/customers").content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(customerService).createCustomer(request);
    }

    @Test
    @WithMockUser
    public void testCreateCustomerValidation() throws Exception {
        when(customerService.createCustomer(any()))
                .thenReturn(CustomerResponse.builder().build());
        // Empty request
        CustomerRequest request = CustomerRequest.builder().build();
        mockMvc.perform(post("/v1/customers").content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        // Request with short string lengths for name and surname
        request = CustomerRequest.builder().name("s").surname("a").build();
        mockMvc.perform(post("/v1/customers").content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        // Request with long string lengths for name and surname
        request = CustomerRequest.builder()
                .name("ssssssssssssssssssssssssssssss")
                .surname("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").build();
        mockMvc.perform(post("/v1/customers").content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(customerService, never()).createCustomer(any());
        // Valid request
        request = CustomerRequest.builder()
                .name("name")
                .surname("surname").build();
        mockMvc.perform(post("/v1/customers").content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(customerService).createCustomer(any());
    }

    @Test
    @WithMockUser
    public void testRetrieveCustomer() throws Exception {
        when(customerService.retrieveCustomerResponseById(1L)).thenReturn(CustomerResponse.builder().build());
        mockMvc.perform(get("/v1/customers/{customerId}", 1))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testRetrieveCustomerDoesNotExist() throws Exception {
        when(customerService.retrieveCustomerResponseById(1L)).thenThrow(CustomerNotFoundException.class);
        mockMvc.perform(get("/v1/customers/{customerId}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testPatchCustomer() throws Exception {
        CustomerUpdateRequest request = CustomerUpdateRequest.builder()
                .name("name").build();
        when(customerService.patchCustomer(1L, request)).thenReturn(CustomerResponse.builder().build());
        mockMvc.perform(patch("/v1/customers/{customerId}", 1).content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testPatchCustomerDoesNotExist() throws Exception {
        CustomerUpdateRequest request = CustomerUpdateRequest.builder()
                .name("name").build();
        when(customerService.patchCustomer(1L, request)).thenThrow(CustomerNotFoundException.class);
        mockMvc.perform(patch("/v1/customers/{customerId}", 1).content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testDeleteCustomer() throws Exception {
        mockMvc.perform(delete("/v1/customers/{customerId}", 1))
                .andExpect(status().isOk());
        verify(customerService).deleteCustomerById(1L);
    }

    @Test
    @WithMockUser
    public void testDeleteCustomerDoesNotExist() throws Exception {
        doThrow(new CustomerNotFoundException()).when(customerService).deleteCustomerById(eq(1L));
        mockMvc.perform(delete("/v1/customers/{customerId}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testUpdateCustomerPhoto() throws Exception {
        when(customerService.updateCustomerPhoto(eq(1L), any()))
                .thenReturn(CustomerResponse.builder().photoUrl("PHOTO URL").build());
        mockMvc.perform(multipart("/v1/customers/{customerId}/photo", 1).file(generateMockMultipartFile()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photoUrl", equalTo("PHOTO URL")));
    }

    @Test
    @WithMockUser
    public void testUpdateCustomerPhotoCustomerDoesNotExist() throws Exception {
        doThrow(new CustomerNotFoundException()).when(customerService).updateCustomerPhoto(eq(1L), any());
        mockMvc.perform(multipart("/v1/customers/{customerId}/photo", 1).file(generateMockMultipartFile()))
                .andExpect(status().isNotFound());
    }
}
