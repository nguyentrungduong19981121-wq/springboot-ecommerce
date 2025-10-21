package com.hendisantika.ecommerce.springbootecommerce.controller;

import com.hendisantika.ecommerce.springbootecommerce.model.Coupon;
import com.hendisantika.ecommerce.springbootecommerce.repository.CouponRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/marketing/coupons")
public class MarketingController {

    private final CouponRepository couponRepository;

    public MarketingController(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return ResponseEntity.ok(couponRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCoupon(@PathVariable Long id) {
        return couponRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@Valid @RequestBody Coupon coupon) {
        Coupon savedCoupon = couponRepository.save(coupon);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCoupon);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @Valid @RequestBody Coupon coupon) {
        return couponRepository.findById(id)
                .map(existing -> {
                    coupon.setId(id);
                    return ResponseEntity.ok(couponRepository.save(coupon));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        return couponRepository.findById(id)
                .map(coupon -> {
                    couponRepository.delete(coupon);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/validate/{code}")
    public ResponseEntity<Boolean> validateCoupon(@PathVariable String code) {
        return couponRepository.findByCode(code)
                .map(coupon -> {
                    boolean valid = coupon.isValid();
                    return ResponseEntity.ok(valid);
                })
                .orElse(ResponseEntity.ok(false));
    }
}
