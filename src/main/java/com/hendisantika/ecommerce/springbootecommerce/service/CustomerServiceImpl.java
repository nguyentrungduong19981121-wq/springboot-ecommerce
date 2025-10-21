package com.hendisantika.ecommerce.springbootecommerce.service;

import com.hendisantika.ecommerce.springbootecommerce.dto.AddressDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.CreateAddressRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.CustomerProfileDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.LoginRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.LoginResponse;
import com.hendisantika.ecommerce.springbootecommerce.dto.RegisterRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.UpdateProfileRequest;
import com.hendisantika.ecommerce.springbootecommerce.exception.ResourceNotFoundException;
import com.hendisantika.ecommerce.springbootecommerce.model.Address;
import com.hendisantika.ecommerce.springbootecommerce.model.Customer;
import com.hendisantika.ecommerce.springbootecommerce.repository.AddressRepository;
import com.hendisantika.ecommerce.springbootecommerce.repository.CustomerRepository;
import com.hendisantika.ecommerce.springbootecommerce.util.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 15:10
 */
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                              AddressRepository addressRepository,
                              PasswordEncoder passwordEncoder,
                              JwtUtil jwtUtil) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public CustomerProfileDto register(RegisterRequest request) {
        // Check if email already exists
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        // Create new customer
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        customer.setPhone(request.getPhone());
        customer.setRegisteredDate(LocalDate.now());

        Customer savedCustomer = customerRepository.save(customer);
        return convertToProfileDto(savedCustomer);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // Find customer by email
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), customer.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(customer.getEmail(), customer.getId());

        return new LoginResponse(token, customer.getId(), customer.getName(), customer.getEmail());
    }

    @Override
    public CustomerProfileDto getProfile(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + email));
        return convertToProfileDto(customer);
    }

    @Override
    public CustomerProfileDto updateProfile(String email, UpdateProfileRequest request) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + email));

        // Update fields if provided
        if (request.getName() != null) {
            customer.setName(request.getName());
        }

        if (request.getPhone() != null) {
            customer.setPhone(request.getPhone());
        }

        Customer updatedCustomer = customerRepository.save(customer);
        return convertToProfileDto(updatedCustomer);
    }

    @Override
    public List<AddressDto> getAddresses(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + email));

        return customer.getAddresses().stream()
                .map(this::convertToAddressDto)
                .collect(Collectors.toList());
    }

    @Override
    public AddressDto addAddress(String email, CreateAddressRequest request) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + email));

        Address address = new Address();
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setCustomer(customer);

        Address savedAddress = addressRepository.save(address);
        return convertToAddressDto(savedAddress);
    }

    private CustomerProfileDto convertToProfileDto(Customer customer) {
        List<AddressDto> addresses = customer.getAddresses().stream()
                .map(this::convertToAddressDto)
                .collect(Collectors.toList());

        return new CustomerProfileDto(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getRegisteredDate(),
                addresses
        );
    }

    private AddressDto convertToAddressDto(Address address) {
        return new AddressDto(
                address.getId(),
                address.getStreet(),
                address.getCity(),
                address.getPostalCode(),
                address.getCountry()
        );
    }
}
