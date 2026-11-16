package com.supplysync.service;

import com.supplysync.dto.AuthResponse;
import com.supplysync.dto.LoginRequest;
import com.supplysync.dto.RegisterRequest;
import com.supplysync.entity.User;
import com.supplysync.entity.Vendor;
import com.supplysync.enums.Role;
import com.supplysync.exception.BadRequestException;
import com.supplysync.repository.UserRepository;
import com.supplysync.repository.VendorRepository;
import com.supplysync.security.CustomUserDetails;
import com.supplysync.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       VendorRepository vendorRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.vendorRepository = vendorRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        Long vendorId = null;
        String vendorName = null;
        if (user.getVendor() != null) {
            vendorId = user.getVendor().getId();
            vendorName = user.getVendor().getCompanyName();
        }

        return new AuthResponse(
                jwt,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                vendorId,
                vendorName
        );
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered: " + request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Role.VENDOR);
        user.setPhone(request.getPhone());
        user.setActive(true);

        if (request.getVendorId() != null) {
            Vendor vendor = vendorRepository.findById(request.getVendorId())
                    .orElseThrow(() -> new BadRequestException("Vendor not found with id: " + request.getVendorId()));
            user.setVendor(vendor);
        }

        User savedUser = userRepository.save(user);

        String jwt = tokenProvider.generateTokenFromUsername(savedUser.getEmail());

        Long vendorId = null;
        String vendorName = null;
        if (savedUser.getVendor() != null) {
            vendorId = savedUser.getVendor().getId();
            vendorName = savedUser.getVendor().getCompanyName();
        }

        return new AuthResponse(
                jwt,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRole(),
                vendorId,
                vendorName
        );
    }
}
