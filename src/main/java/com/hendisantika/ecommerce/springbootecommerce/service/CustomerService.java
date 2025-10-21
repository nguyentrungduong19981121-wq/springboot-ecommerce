package com.hendisantika.ecommerce.springbootecommerce.service;

import com.hendisantika.ecommerce.springbootecommerce.dto.AddressDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.CreateAddressRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.CustomerProfileDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.LoginRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.LoginResponse;
import com.hendisantika.ecommerce.springbootecommerce.dto.RegisterRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.UpdateProfileRequest;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 15:05
 */
public interface CustomerService {
    
    /**
     * Register a new customer
     */
    CustomerProfileDto register(RegisterRequest request);
    
    /**
     * Login and get JWT token
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * Get customer profile by email (from JWT)
     */
    CustomerProfileDto getProfile(String email);
    
    /**
     * Update customer profile
     */
    CustomerProfileDto updateProfile(String email, UpdateProfileRequest request);
    
    /**
     * Get all addresses for a customer
     */
    List<AddressDto> getAddresses(String email);
    
    /**
     * Add new address for a customer
     */
    AddressDto addAddress(String email, CreateAddressRequest request);
}
