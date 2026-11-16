-- =============================================================================
-- SupplySync Enterprise - MySQL 8.x Database Schema & Demonstration Data Script
-- =============================================================================

CREATE DATABASE IF NOT EXISTS `supplysync` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `supplysync`;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `email` VARCHAR(120) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(120) NOT NULL,
    `role` VARCHAR(20) NOT NULL,
    `phone` VARCHAR(30) NULL,
    `vendor_id` BIGINT NULL,
    `active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Vendors Table
CREATE TABLE IF NOT EXISTS `vendors` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `company_name` VARCHAR(150) NOT NULL,
    `contact_person` VARCHAR(100) NOT NULL,
    `email` VARCHAR(120) NOT NULL UNIQUE,
    `phone` VARCHAR(30) NOT NULL,
    `address` VARCHAR(255) NULL,
    `city` VARCHAR(100) NULL,
    `business_category` VARCHAR(100) NULL,
    `vendor_status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `registration_date` DATE NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE `users` ADD CONSTRAINT `fk_users_vendor` FOREIGN KEY (`vendor_id`) REFERENCES `vendors` (`id`) ON DELETE SET NULL;

-- 3. Categories Table
CREATE TABLE IF NOT EXISTS `categories` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL UNIQUE,
    `description` VARCHAR(255) NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Products Table
CREATE TABLE IF NOT EXISTS `products` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(150) NOT NULL,
    `sku` VARCHAR(50) NOT NULL UNIQUE,
    `description` TEXT NULL,
    `category_id` BIGINT NOT NULL,
    `unit_price` DECIMAL(12,2) NOT NULL,
    `unit_of_measurement` VARCHAR(50) NOT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_products_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Inventory Table
CREATE TABLE IF NOT EXISTS `inventory` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `product_id` BIGINT NOT NULL UNIQUE,
    `available_quantity` INT NOT NULL DEFAULT 0,
    `minimum_stock_level` INT NOT NULL DEFAULT 10,
    `maximum_stock_level` INT NOT NULL DEFAULT 1000,
    `last_updated` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_inventory_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Procurement Requests Table
CREATE TABLE IF NOT EXISTS `procurement_requests` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `request_number` VARCHAR(50) NOT NULL UNIQUE,
    `vendor_id` BIGINT NOT NULL,
    `requested_by_id` BIGINT NOT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    `total_estimated_cost` DECIMAL(14,2) NOT NULL DEFAULT 0.00,
    `notes` TEXT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_pr_vendor` FOREIGN KEY (`vendor_id`) REFERENCES `vendors` (`id`),
    CONSTRAINT `fk_pr_user` FOREIGN KEY (`requested_by_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Procurement Request Items Table
CREATE TABLE IF NOT EXISTS `procurement_request_items` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `procurement_request_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `quantity` INT NOT NULL,
    `estimated_unit_price` DECIMAL(12,2) NOT NULL,
    `subtotal` DECIMAL(14,2) NOT NULL,
    CONSTRAINT `fk_pri_request` FOREIGN KEY (`procurement_request_id`) REFERENCES `procurement_requests` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_pri_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Purchase Orders Table
CREATE TABLE IF NOT EXISTS `purchase_orders` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_number` VARCHAR(50) NOT NULL UNIQUE,
    `vendor_id` BIGINT NOT NULL,
    `procurement_request_id` BIGINT NULL,
    `order_date` DATE NOT NULL,
    `expected_delivery_date` DATE NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    `subtotal` DECIMAL(14,2) NOT NULL DEFAULT 0.00,
    `tax_rate` DECIMAL(5,2) NOT NULL DEFAULT 5.00,
    `tax_amount` DECIMAL(14,2) NOT NULL DEFAULT 0.00,
    `total_amount` DECIMAL(14,2) NOT NULL DEFAULT 0.00,
    `notes` TEXT NULL,
    `inventory_received` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_po_vendor` FOREIGN KEY (`vendor_id`) REFERENCES `vendors` (`id`),
    CONSTRAINT `fk_po_pr` FOREIGN KEY (`procurement_request_id`) REFERENCES `procurement_requests` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Purchase Order Items Table
CREATE TABLE IF NOT EXISTS `purchase_order_items` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `purchase_order_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `quantity` INT NOT NULL,
    `unit_price` DECIMAL(12,2) NOT NULL,
    `total_price` DECIMAL(14,2) NOT NULL,
    CONSTRAINT `fk_poi_order` FOREIGN KEY (`purchase_order_id`) REFERENCES `purchase_orders` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_poi_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. Deliveries Table
CREATE TABLE IF NOT EXISTS `deliveries` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `delivery_number` VARCHAR(50) NOT NULL UNIQUE,
    `purchase_order_id` BIGINT NOT NULL,
    `tracking_number` VARCHAR(100) NULL,
    `carrier_name` VARCHAR(100) NULL,
    `shipment_status` VARCHAR(30) NOT NULL DEFAULT 'PREPARING',
    `expected_delivery_date` DATE NULL,
    `actual_delivery_date` TIMESTAMP NULL,
    `delivery_notes` TEXT NULL,
    `recipient_name` VARCHAR(100) NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_deliveries_po` FOREIGN KEY (`purchase_order_id`) REFERENCES `purchase_orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. Stock Transactions Table
CREATE TABLE IF NOT EXISTS `stock_transactions` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `product_id` BIGINT NOT NULL,
    `transaction_type` VARCHAR(30) NOT NULL,
    `quantity` INT NOT NULL,
    `previous_quantity` INT NULL,
    `new_quantity` INT NULL,
    `transaction_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `reference_order` VARCHAR(100) NULL,
    `description` VARCHAR(255) NULL,
    `created_by` VARCHAR(100) NULL,
    CONSTRAINT `fk_st_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
