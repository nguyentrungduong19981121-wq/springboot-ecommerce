package com.hendisantika.ecommerce.springbootecommerce.controller;

import com.hendisantika.ecommerce.springbootecommerce.model.Role;
import com.hendisantika.ecommerce.springbootecommerce.model.User;
import com.hendisantika.ecommerce.springbootecommerce.repository.RoleRepository;
import com.hendisantika.ecommerce.springbootecommerce.repository.UserRepository;
import com.hendisantika.ecommerce.springbootecommerce.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserManagementController(UserRepository userRepository,
                                   RoleRepository roleRepository,
                                   PasswordEncoder passwordEncoder,
                                   JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String email = request.get("email");
        String fullName = request.get("fullName");

        if (userRepository.existsByUsername(username)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Username already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setFullName(fullName);

        // Assign default STAFF role
        roleRepository.findByName("STAFF").ifPresent(role -> {
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
        });

        User savedUser = userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", savedUser.getId());
        response.put("username", savedUser.getUsername());
        response.put("email", savedUser.getEmail());
        response.put("roles", savedUser.getRoles());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        return userRepository.findByUsername(username)
                .map(user -> {
                    if (!user.getActive()) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("error", "Account is inactive");
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
                    }

                    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("error", "Invalid credentials");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
                    }

                    // Update last login
                    user.setLastLogin(LocalDateTime.now());
                    userRepository.save(user);

                    // Generate JWT token
                    String token = jwtUtil.generateToken(user.getUsername(), user.getId());

                    Map<String, Object> response = new HashMap<>();
                    response.put("token", token);
                    response.put("userId", user.getId());
                    response.put("username", user.getUsername());
                    response.put("roles", user.getRoles());

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> error = new HashMap<>();
                    error.put("error", "User not found");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                });
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .map(user -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("userId", user.getId());
                    response.put("username", user.getUsername());
                    response.put("email", user.getEmail());
                    response.put("fullName", user.getFullName());
                    response.put("roles", user.getRoles());
                    response.put("active", user.getActive());
                    response.put("createdAt", user.getCreatedAt());
                    response.put("lastLogin", user.getLastLogin());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<User> updateUserRoles(@PathVariable Long id, @RequestBody Set<Long> roleIds) {
        return userRepository.findById(id)
                .map(user -> {
                    Set<Role> roles = new HashSet<>();
                    for (Long roleId : roleIds) {
                        roleRepository.findById(roleId).ifPresent(roles::add);
                    }
                    user.setRoles(roles);
                    return ResponseEntity.ok(userRepository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
