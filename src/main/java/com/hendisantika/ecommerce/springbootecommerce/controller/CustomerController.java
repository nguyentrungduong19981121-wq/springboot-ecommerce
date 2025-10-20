package com.hendisantika.ecommerce.springbootecommerce.controller;

import com.hendisantika.ecommerce.springbootecommerce.dto.AddressDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.CreateAddressRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.CustomerProfileDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.LoginRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.LoginResponse;
import com.hendisantika.ecommerce.springbootecommerce.dto.RegisterRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.UpdateProfileRequest;
import com.hendisantika.ecommerce.springbootecommerce.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 15:20
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * POST /api/customers/register
     * Register a new customer account
     * 
     * @param request Registration request with name, email, password, phone
     * @return Created customer profile
     */
    @PostMapping("/register")
    public ResponseEntity<CustomerProfileDto> register(@Valid @RequestBody RegisterRequest request) {
        CustomerProfileDto profile = customerService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    /**
     * POST /api/customers/login
     * Login and get JWT token
     * 
     * @param request Login request with email and password
     * @return JWT token and customer info
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = customerService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/customers/profile
     * Get current customer profile (requires JWT authentication)
     * 
     * @return Customer profile with addresses
     */
    @GetMapping("/profile")
    public ResponseEntity<CustomerProfileDto> getProfile() {
        String email = getCurrentUserEmail();
        CustomerProfileDto profile = customerService.getProfile(email);
        return ResponseEntity.ok(profile);
    }

    /**
     * PUT /api/customers/profile
     * Update current customer profile (requires JWT authentication)
     * 
     * @param request Update request with name and/or phone
     * @return Updated customer profile
     */
    @PutMapping("/profile")
    public ResponseEntity<CustomerProfileDto> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        String email = getCurrentUserEmail();
        CustomerProfileDto profile = customerService.updateProfile(email, request);
        return ResponseEntity.ok(profile);
    }

    /**
     * GET /api/customers/addresses
     * Get all addresses for current customer (requires JWT authentication)
     * 
     * @return List of addresses
     */
    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDto>> getAddresses() {
        String email = getCurrentUserEmail();
        List<AddressDto> addresses = customerService.getAddresses(email);
        return ResponseEntity.ok(addresses);
    }

    /**
     * POST /api/customers/addresses
     * Add new address for current customer (requires JWT authentication)
     * 
     * @param request Address creation request
     * @return Created address
     */
    @PostMapping("/addresses")
    public ResponseEntity<AddressDto> addAddress(@Valid @RequestBody CreateAddressRequest request) {
        String email = getCurrentUserEmail();
        AddressDto address = customerService.addAddress(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(address);
    }

    /**
     * Get current authenticated user's email from Security Context
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
