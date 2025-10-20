package com.hendisantika.ecommerce.springbootecommerce.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 14:25
 */
public class CustomerProfileDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private LocalDate registeredDate;
    private List<AddressDto> addresses;

    public CustomerProfileDto() {
    }

    public CustomerProfileDto(Long id, String name, String email, String phone, 
                             LocalDate registeredDate, List<AddressDto> addresses) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.registeredDate = registeredDate;
        this.addresses = addresses;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(LocalDate registeredDate) {
        this.registeredDate = registeredDate;
    }

    public List<AddressDto> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressDto> addresses) {
        this.addresses = addresses;
    }
}
