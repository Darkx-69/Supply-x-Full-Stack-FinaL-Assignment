package com.supplysync.service;

import com.supplysync.dto.ProcurementItemDto;
import com.supplysync.dto.ProcurementRequestDto;
import com.supplysync.dto.PurchaseOrderDto;
import com.supplysync.dto.PurchaseOrderItemDto;
import com.supplysync.entity.*;
import com.supplysync.enums.OrderStatus;
import com.supplysync.enums.RequestStatus;
import com.supplysync.exception.BadRequestException;
import com.supplysync.exception.ResourceNotFoundException;
import com.supplysync.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProcurementService {

    private final ProcurementRequestRepository requestRepository;
    private final ProcurementRequestItemRepository itemRepository;
    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PurchaseOrderService purchaseOrderService;

    public ProcurementService(ProcurementRequestRepository requestRepository,
                              ProcurementRequestItemRepository itemRepository,
                              VendorRepository vendorRepository,
                              UserRepository userRepository,
                              ProductRepository productRepository,
                              PurchaseOrderService purchaseOrderService) {
        this.requestRepository = requestRepository;
        this.itemRepository = itemRepository;
        this.vendorRepository = vendorRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.purchaseOrderService = purchaseOrderService;
    }

    @Transactional(readOnly = true)
    public List<ProcurementRequestDto> getAllRequests(String search, RequestStatus status, Long vendorId) {
        List<ProcurementRequest> list;
        if ((search != null && !search.isBlank()) || status != null || vendorId != null) {
            list = requestRepository.searchRequests(search, status, vendorId);
        } else {
            list = requestRepository.findAll();
        }
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProcurementRequestDto getRequestById(Long id) {
        ProcurementRequest pr = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Procurement request not found with id: " + id));
        return toDto(pr);
    }

    @Transactional
    public ProcurementRequestDto createRequest(ProcurementRequestDto dto, String userEmail) {
        Vendor vendor = vendorRepository.findById(dto.getVendorId())
                .orElseThrow(() -> new BadRequestException("Vendor not found with id: " + dto.getVendorId()));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        String reqNum = "PR-" + System.currentTimeMillis() % 1000000;

        ProcurementRequest pr = new ProcurementRequest(reqNum, vendor, user, dto.getNotes());
        pr.setStatus(dto.getStatus() != null ? dto.getStatus() : RequestStatus.PENDING);

        BigDecimal totalCost = BigDecimal.ZERO;
        for (ProcurementItemDto itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new BadRequestException("Product not found with id: " + itemDto.getProductId()));
            ProcurementRequestItem item = new ProcurementRequestItem(pr, product, itemDto.getQuantity(), itemDto.getEstimatedUnitPrice());
            totalCost = totalCost.add(item.getSubtotal());
            pr.addItem(item);
        }
        pr.setTotalEstimatedCost(totalCost);

        ProcurementRequest saved = requestRepository.save(pr);
        return toDto(saved);
    }

    @Transactional
    public ProcurementRequestDto updateStatus(Long id, RequestStatus status) {
        ProcurementRequest pr = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Procurement request not found with id: " + id));

        if (pr.getStatus() == RequestStatus.CONVERTED_TO_ORDER) {
            throw new BadRequestException("Cannot change status of a request that has already been converted to an order.");
        }

        pr.setStatus(status);
        ProcurementRequest saved = requestRepository.save(pr);
        return toDto(saved);
    }

    @Transactional
    public PurchaseOrderDto convertToPurchaseOrder(Long id) {
        ProcurementRequest pr = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Procurement request not found with id: " + id));

        if (pr.getStatus() == RequestStatus.CONVERTED_TO_ORDER) {
            throw new BadRequestException("This procurement request is already converted to a purchase order.");
        }
        if (pr.getStatus() == RequestStatus.REJECTED) {
            throw new BadRequestException("Cannot convert a rejected procurement request to a purchase order.");
        }

        PurchaseOrderDto poDto = new PurchaseOrderDto();
        poDto.setVendorId(pr.getVendor().getId());
        poDto.setProcurementRequestId(pr.getId());
        poDto.setOrderDate(LocalDate.now());
        poDto.setExpectedDeliveryDate(LocalDate.now().plusDays(10));
        poDto.setStatus(OrderStatus.PENDING);
        poDto.setNotes("Generated from Procurement Request: " + pr.getRequestNumber() + ". " + (pr.getNotes() != null ? pr.getNotes() : ""));

        List<ProcurementRequestItem> items = itemRepository.findByProcurementRequestId(pr.getId());
        List<PurchaseOrderItemDto> poItems = items.stream().map(i -> {
            PurchaseOrderItemDto itemDto = new PurchaseOrderItemDto();
            itemDto.setProductId(i.getProduct().getId());
            itemDto.setQuantity(i.getQuantity());
            itemDto.setUnitPrice(i.getEstimatedUnitPrice());
            return itemDto;
        }).collect(Collectors.toList());
        poDto.setItems(poItems);

        PurchaseOrderDto createdPo = purchaseOrderService.createPurchaseOrder(poDto);

        pr.setStatus(RequestStatus.CONVERTED_TO_ORDER);
        requestRepository.save(pr);

        return createdPo;
    }

    @Transactional
    public void deleteRequest(Long id) {
        ProcurementRequest pr = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Procurement request not found with id: " + id));
        if (pr.getStatus() == RequestStatus.CONVERTED_TO_ORDER) {
            throw new BadRequestException("Cannot delete a procurement request that has already been converted to an order.");
        }
        requestRepository.delete(pr);
    }

    public ProcurementRequestDto toDto(ProcurementRequest pr) {
        ProcurementRequestDto dto = new ProcurementRequestDto();
        dto.setId(pr.getId());
        dto.setRequestNumber(pr.getRequestNumber());
        if (pr.getVendor() != null) {
            dto.setVendorId(pr.getVendor().getId());
            dto.setVendorCompanyName(pr.getVendor().getCompanyName());
        }
        if (pr.getRequestedBy() != null) {
            dto.setRequestedById(pr.getRequestedBy().getId());
            dto.setRequestedByName(pr.getRequestedBy().getFullName());
        }
        dto.setStatus(pr.getStatus());
        dto.setTotalEstimatedCost(pr.getTotalEstimatedCost());
        dto.setNotes(pr.getNotes());
        dto.setCreatedAt(pr.getCreatedAt());
        dto.setUpdatedAt(pr.getUpdatedAt());

        List<ProcurementRequestItem> items = itemRepository.findByProcurementRequestId(pr.getId());
        dto.setItems(items.stream().map(i -> {
            ProcurementItemDto itemDto = new ProcurementItemDto();
            itemDto.setId(i.getId());
            itemDto.setProductId(i.getProduct().getId());
            itemDto.setProductName(i.getProduct().getName());
            itemDto.setProductSku(i.getProduct().getSku());
            itemDto.setQuantity(i.getQuantity());
            itemDto.setEstimatedUnitPrice(i.getEstimatedUnitPrice());
            itemDto.setSubtotal(i.getSubtotal());
            return itemDto;
        }).collect(Collectors.toList()));

        return dto;
    }
}
