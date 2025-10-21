package com.hendisantika.ecommerce.springbootecommerce.controller;

import com.hendisantika.ecommerce.springbootecommerce.model.ShippingMethod;
import com.hendisantika.ecommerce.springbootecommerce.repository.ShippingMethodRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shipping/methods")
public class ShippingController {

    private final ShippingMethodRepository shippingMethodRepository;

    public ShippingController(ShippingMethodRepository shippingMethodRepository) {
        this.shippingMethodRepository = shippingMethodRepository;
    }

    @GetMapping
    public ResponseEntity<List<ShippingMethod>> getAllMethods() {
        return ResponseEntity.ok(shippingMethodRepository.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<ShippingMethod>> getActiveMethods() {
        return ResponseEntity.ok(shippingMethodRepository.findByActiveTrue());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShippingMethod> getMethod(@PathVariable Long id) {
        return shippingMethodRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ShippingMethod> createMethod(@Valid @RequestBody ShippingMethod method) {
        ShippingMethod savedMethod = shippingMethodRepository.save(method);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMethod);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShippingMethod> updateMethod(@PathVariable Long id, @Valid @RequestBody ShippingMethod method) {
        return shippingMethodRepository.findById(id)
                .map(existing -> {
                    method.setId(id);
                    return ResponseEntity.ok(shippingMethodRepository.save(method));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMethod(@PathVariable Long id) {
        return shippingMethodRepository.findById(id)
                .map(method -> {
                    shippingMethodRepository.delete(method);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/calculate")
    public ResponseEntity<Map<String, Object>> calculateShippingCost(
            @PathVariable Long id,
            @RequestParam(required = false) Double distanceKm) {
        
        return shippingMethodRepository.findById(id)
                .map(method -> {
                    Double cost = method.calculateShippingCost(distanceKm);
                    Map<String, Object> response = new HashMap<>();
                    response.put("methodId", id);
                    response.put("methodName", method.getName());
                    response.put("distanceKm", distanceKm != null ? distanceKm : 0);
                    response.put("shippingCost", cost);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
