package com.supplysync.service;

import com.supplysync.dto.PurchaseOrderDto;
import com.supplysync.dto.VendorDto;
import com.supplysync.entity.Vendor;
import com.supplysync.enums.VendorStatus;
import com.supplysync.exception.BadRequestException;
import com.supplysync.exception.ResourceNotFoundException;
import com.supplysync.repository.PurchaseOrderRepository;
import com.supplysync.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VendorService {

    private final VendorRepository vendorRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    public VendorService(VendorRepository vendorRepository,
                         PurchaseOrderRepository purchaseOrderRepository) {
        this.vendorRepository = vendorRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @Transactional(readOnly = true)
    public List<VendorDto> getAllVendors(String query, VendorStatus status, String category) {
        List<Vendor> vendors;
        if ((query != null && !query.isBlank()) || status != null || (category != null && !category.isBlank())) {
            vendors = vendorRepository.searchVendors(query, status, category);
        } else {
            vendors = vendorRepository.findAll();
        }
        return vendors.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VendorDto getVendorById(Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));
        return toDto(vendor);
    }

    @Transactional
    public VendorDto createVendor(VendorDto dto) {
        if (vendorRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("A vendor with email " + dto.getEmail() + " already exists");
        }

        Vendor vendor = new Vendor();
        updateVendorFields(vendor, dto);
        Vendor saved = vendorRepository.save(vendor);
        return toDto(saved);
    }

    @Transactional
    public VendorDto updateVendor(Long id, VendorDto dto) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));

        if (!vendor.getEmail().equalsIgnoreCase(dto.getEmail()) && vendorRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("A vendor with email " + dto.getEmail() + " already exists");
        }

        updateVendorFields(vendor, dto);
        Vendor saved = vendorRepository.save(vendor);
        return toDto(saved);
    }

    @Transactional
    public void deleteVendor(Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));
        vendorRepository.delete(vendor);
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrderDto> getVendorOrders(Long vendorId) {
        if (!vendorRepository.existsById(vendorId)) {
            throw new ResourceNotFoundException("Vendor not found with id: " + vendorId);
        }
        return purchaseOrderRepository.findByVendorId(vendorId).stream()
                .map(po -> {
                    PurchaseOrderDto dto = new PurchaseOrderDto();
                    dto.setId(po.getId());
                    dto.setOrderNumber(po.getOrderNumber());
                    dto.setVendorId(po.getVendor().getId());
                    dto.setVendorCompanyName(po.getVendor().getCompanyName());
                    dto.setOrderDate(po.getOrderDate());
                    dto.setExpectedDeliveryDate(po.getExpectedDeliveryDate());
                    dto.setStatus(po.getStatus());
                    dto.setSubtotal(po.getSubtotal());
                    dto.setTaxRate(po.getTaxRate());
                    dto.setTaxAmount(po.getTaxAmount());
                    dto.setTotalAmount(po.getTotalAmount());
                    dto.setNotes(po.getNotes());
                    dto.setInventoryReceived(po.getInventoryReceived());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private void updateVendorFields(Vendor vendor, VendorDto dto) {
        vendor.setCompanyName(dto.getCompanyName());
        vendor.setContactPerson(dto.getContactPerson());
        vendor.setEmail(dto.getEmail());
        vendor.setPhone(dto.getPhone());
        vendor.setAddress(dto.getAddress());
        vendor.setCity(dto.getCity());
        vendor.setBusinessCategory(dto.getBusinessCategory());
        if (dto.getVendorStatus() != null) {
            vendor.setVendorStatus(dto.getVendorStatus());
        }
    }

    public VendorDto toDto(Vendor v) {
        VendorDto dto = new VendorDto();
        dto.setId(v.getId());
        dto.setCompanyName(v.getCompanyName());
        dto.setContactPerson(v.getContactPerson());
        dto.setEmail(v.getEmail());
        dto.setPhone(v.getPhone());
        dto.setAddress(v.getAddress());
        dto.setCity(v.getCity());
        dto.setBusinessCategory(v.getBusinessCategory());
        dto.setVendorStatus(v.getVendorStatus());
        dto.setRegistrationDate(v.getRegistrationDate());
        dto.setCreatedAt(v.getCreatedAt());
        return dto;
    }
}
