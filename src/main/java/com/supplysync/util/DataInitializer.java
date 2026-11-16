package com.supplysync.util;

import com.supplysync.entity.*;
import com.supplysync.enums.*;
import com.supplysync.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ProcurementRequestRepository procurementRequestRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final DeliveryRepository deliveryRepository;
    private final StockTransactionRepository stockTransactionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           VendorRepository vendorRepository,
                           CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           InventoryRepository inventoryRepository,
                           ProcurementRequestRepository procurementRequestRepository,
                           PurchaseOrderRepository purchaseOrderRepository,
                           DeliveryRepository deliveryRepository,
                           StockTransactionRepository stockTransactionRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.vendorRepository = vendorRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.procurementRequestRepository = procurementRequestRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.deliveryRepository = deliveryRepository;
        this.stockTransactionRepository = stockTransactionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            logger.info("Database already seeded. Skipping initial data generation.");
            return;
        }

        logger.info("Seeding SupplySync demonstration database...");

        // 1. Seed Categories
        Category rawMaterials = categoryRepository.save(new Category("Raw Materials", "Base industrial metals, composites, and crude stock"));
        Category electronics = categoryRepository.save(new Category("Electronics", "Sensors, microcontrollers, circuits, and boards"));
        Category packaging = categoryRepository.save(new Category("Packaging Supplies", "Boxes, cartons, tapes, and protective wrappers"));
        Category hardware = categoryRepository.save(new Category("Industrial Hardware", "Fasteners, bolts, hinges, and heavy tools"));
        Category safety = categoryRepository.save(new Category("Safety Equipment", "PPE gear, face shields, gloves, and harnesses"));

        // 2. Seed Vendors
        Vendor apex = vendorRepository.save(new Vendor("Apex Logistics & Supplies", "Robert Taylor", "vendor@apexsupplies.com", "+1 555-0192", "450 Industrial Parkway", "Chicago", "Raw Materials"));
        Vendor silicon = vendorRepository.save(new Vendor("Global Silicon Components", "Sarah Jenkins", "sarah@globalsilicon.com", "+1 555-0283", "88 Technology Drive", "San Jose", "Electronics"));
        Vendor precision = vendorRepository.save(new Vendor("Precision Packaging Ltd", "Michael Chang", "mchang@precisionpack.com", "+1 555-0374", "12 Logistic Way", "Dallas", "Packaging Supplies"));
        Vendor nexus = vendorRepository.save(new Vendor("Nexus Industrial Tools", "David Miller", "david@nexusindustrial.com", "+1 555-0465", "700 Foundry Road", "Cleveland", "Industrial Hardware"));

        // 3. Seed Users
        // Admin user
        User admin = new User("admin@supplysync.com", passwordEncoder.encode("admin123"), "System Administrator", Role.ADMIN);
        admin.setPhone("+1 555-0001");
        userRepository.save(admin);

        // Vendor user linked to Apex
        User vendorUser = new User("vendor@apexsupplies.com", passwordEncoder.encode("vendor123"), "Robert Taylor (Apex)", Role.VENDOR);
        vendorUser.setPhone("+1 555-0192");
        vendorUser.setVendor(apex);
        userRepository.save(vendorUser);

        // 4. Seed Products
        Product pSteel = productRepository.save(new Product("Industrial Steel Rods 2m", "STL-ROD-01", "Standard grade 304 structural steel rods", rawMaterials, new BigDecimal("45.00"), "Units"));
        Product pMcu = productRepository.save(new Product("Silicon Microcontroller MCU-32", "ELEC-MCU-32", "32-bit ARM Cortex M4 core microcontroller", electronics, new BigDecimal("12.50"), "Units"));
        Product pBox = productRepository.save(new Product("Corrugated Shipping Box Large", "PKG-BOX-LRG", "Heavy-duty double-wall shipping carton 24x18x18", packaging, new BigDecimal("2.80"), "Boxes"));
        Product pBolts = productRepository.save(new Product("Heavy-duty Hex Bolts M10", "HRD-BLT-M10", "Zinc-plated carbon steel hex head bolts pack", hardware, new BigDecimal("0.75"), "Units"));
        Product pCopper = productRepository.save(new Product("High-Grade Copper Wire 100m", "RAW-CPR-100", "99.9% pure annealed copper spool", rawMaterials, new BigDecimal("85.00"), "Spools"));
        Product pGloves = productRepository.save(new Product("Protective Nitrile Gloves (Box 100)", "SFT-GLV-NIT", "Powder-free medical grade blue nitrile gloves", safety, new BigDecimal("14.20"), "Boxes"));
        Product pPaste = productRepository.save(new Product("Thermal Paste 50g Tube", "ELEC-THM-50G", "High-efficiency thermal compound", electronics, new BigDecimal("6.50"), "Tubes"));

        // 5. Seed Inventory (with some low-stock alerts)
        Inventory invSteel = inventoryRepository.save(new Inventory(pSteel, 120, 30, 500));
        Inventory invMcu = inventoryRepository.save(new Inventory(pMcu, 15, 25, 600)); // LOW STOCK ALERT! (15 <= 25)
        Inventory invBox = inventoryRepository.save(new Inventory(pBox, 350, 50, 1500));
        Inventory invBolts = inventoryRepository.save(new Inventory(pBolts, 8, 50, 2000)); // LOW STOCK ALERT! (8 <= 50)
        Inventory invCopper = inventoryRepository.save(new Inventory(pCopper, 42, 15, 300));
        Inventory invGloves = inventoryRepository.save(new Inventory(pGloves, 80, 20, 400));
        Inventory invPaste = inventoryRepository.save(new Inventory(pPaste, 5, 20, 250)); // LOW STOCK ALERT! (5 <= 20)

        // 6. Seed Initial Stock Transactions
        stockTransactionRepository.save(new StockTransaction(pSteel, TransactionType.STOCK_ADDED, 120, 0, 120, "INIT-001", "Initial warehouse intake", "admin@supplysync.com"));
        stockTransactionRepository.save(new StockTransaction(pMcu, TransactionType.STOCK_ADDED, 15, 0, 15, "INIT-001", "Initial warehouse intake", "admin@supplysync.com"));
        stockTransactionRepository.save(new StockTransaction(pBox, TransactionType.STOCK_ADDED, 350, 0, 350, "INIT-001", "Initial warehouse intake", "admin@supplysync.com"));
        stockTransactionRepository.save(new StockTransaction(pBolts, TransactionType.STOCK_REMOVED, -42, 50, 8, "CONSUMPTION-04", "Production assembly line requisition", "admin@supplysync.com"));
        stockTransactionRepository.save(new StockTransaction(pCopper, TransactionType.STOCK_ADDED, 42, 0, 42, "INIT-001", "Initial warehouse intake", "admin@supplysync.com"));

        // 7. Seed Procurement Requests
        ProcurementRequest pr1 = new ProcurementRequest("PR-2026-001", apex, admin, "Urgent stock replenishment for steel manufacturing cycle");
        pr1.setStatus(RequestStatus.APPROVED);
        ProcurementRequestItem pr1Item = new ProcurementRequestItem(pr1, pSteel, 100, new BigDecimal("42.00"));
        pr1.addItem(pr1Item);
        pr1.setTotalEstimatedCost(new BigDecimal("4200.00"));
        procurementRequestRepository.save(pr1);

        ProcurementRequest pr2 = new ProcurementRequest("PR-2026-002", silicon, admin, "Microcontroller restocking for IoT telemetry modules");
        pr2.setStatus(RequestStatus.PENDING);
        ProcurementRequestItem pr2Item = new ProcurementRequestItem(pr2, pMcu, 200, new BigDecimal("11.50"));
        pr2.addItem(pr2Item);
        pr2.setTotalEstimatedCost(new BigDecimal("2300.00"));
        procurementRequestRepository.save(pr2);

        // 8. Seed Purchase Orders
        // PO 1: Delivered
        PurchaseOrder po1 = new PurchaseOrder("PO-2026-001", apex, LocalDate.now().minusDays(14), LocalDate.now().minusDays(2), "Quarterly batch shipment");
        po1.setStatus(OrderStatus.DELIVERED);
        po1.setInventoryReceived(true);
        po1.addItem(new PurchaseOrderItem(po1, pSteel, 50, new BigDecimal("45.00")));
        po1.addItem(new PurchaseOrderItem(po1, pCopper, 20, new BigDecimal("85.00")));
        po1.recalculateTotals();
        purchaseOrderRepository.save(po1);

        // PO 2: In-Transit / Shipped
        PurchaseOrder po2 = new PurchaseOrder("PO-2026-002", silicon, LocalDate.now().minusDays(5), LocalDate.now().plusDays(3), "Sensor and MCU production lot");
        po2.setStatus(OrderStatus.SHIPPED);
        po2.addItem(new PurchaseOrderItem(po2, pMcu, 100, new BigDecimal("12.50")));
        po2.addItem(new PurchaseOrderItem(po2, pPaste, 50, new BigDecimal("6.50")));
        po2.recalculateTotals();
        purchaseOrderRepository.save(po2);

        // PO 3: Pending
        PurchaseOrder po3 = new PurchaseOrder("PO-2026-003", precision, LocalDate.now().minusDays(1), LocalDate.now().plusDays(7), "Packaging bulk order");
        po3.setStatus(OrderStatus.PENDING);
        po3.addItem(new PurchaseOrderItem(po3, pBox, 500, new BigDecimal("2.80")));
        po3.recalculateTotals();
        purchaseOrderRepository.save(po3);

        // 9. Seed Deliveries
        Delivery del1 = new Delivery("DEL-2026-001", po1, "TRK-98327110", "FedEx Freight", LocalDate.now().minusDays(2), "Dock 4 delivery - signed by warehouse lead");
        del1.setShipmentStatus(DeliveryStatus.DELIVERED);
        del1.setActualDeliveryDate(LocalDateTime.now().minusDays(2));
        del1.setRecipientName("Mark Anderson (Receiving)");
        deliveryRepository.save(del1);

        Delivery del2 = new Delivery("DEL-2026-002", po2, "TRK-44210984", "DHL Express", LocalDate.now().plusDays(3), "Customs cleared, transit to regional hub");
        del2.setShipmentStatus(DeliveryStatus.IN_TRANSIT);
        deliveryRepository.save(del2);

        logger.info("SupplySync demonstration database initialized successfully with sample accounts:");
        logger.info("  Admin Login:  admin@supplysync.com  /  admin123");
        logger.info("  Vendor Login: vendor@apexsupplies.com / vendor123");
    }
}
