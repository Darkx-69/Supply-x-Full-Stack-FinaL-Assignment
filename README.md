# SupplySync – Enterprise Supply Chain & Vendor Management System

[![Java Version](https://img.shields.io/badge/Java-21%20LTS-007396?style=flat&logo=java)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?style=flat&logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL Database](https://img.shields.io/badge/MySQL-8.x-4479A1?style=flat&logo=mysql)](https://www.mysql.com/)
[![Frontend](https://img.shields.io/badge/Frontend-HTML5%20%7C%20CSS3%20%7C%20Vanilla%20JS-F7DF1E?style=flat&logo=javascript)](https://developer.mozilla.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

SupplySync is a full-stack, enterprise-grade business management and supply chain operations platform developed as an integrated college full-stack web application. The platform helps organizations centralize vendor relationships, product catalogs, warehouse inventory, procurement requisitions, commercial purchase orders, logistics shipment tracking, stock movement audit trails, and financial intelligence analytics.

---

## 1. Problem Statement & Project Objectives

### Problem Statement
Modern enterprises struggle with fragmented logistics data, manual inventory spreadsheets, lack of visibility into supplier lead times, and delayed purchase orders. Errors in inventory intake lead to stock-outs and excess holding costs, while disparate paper and email records create communication gaps between buyers and vendors.

### Project Objectives
- **Centralize Supply Chain Operations**: Provide a single pane of glass for administrators and vendors.
- **Real-Time Warehouse Inventory Health**: Automatically track quantities, calculate valuations, enforce minimum safety thresholds, and flag replenishment deficits.
- **Automated Order-to-Inventory Lifecycle**: Seamlessly transition from Procurement Requisitions &rarr; Approved Purchase Orders &rarr; Dispatched Freight Tracking &rarr; Automatic Inventory Intake & Stock Transaction Logging upon delivery.
- **Enterprise UI/UX Design**: Deliver a clean, responsive SaaS interface using standard HTML5, CSS3, Vanilla JavaScript, and Chart.js without heavyweight third-party frontend frameworks.
- **Stateless Security**: Enforce role-based access control (RBAC) via BCrypt hashed passwords and stateless HMAC-SHA256 JWT bearer tokens.

---

## 2. Technology Stack

| Layer | Technologies |
| :--- | :--- |
| **Frontend** | HTML5, CSS3, Vanilla JavaScript (ES6+), Chart.js (v4.4), Font Awesome 6 |
| **Backend** | Java 21 LTS, Spring Boot 3.3.4, Spring Web, Spring Data JPA, Spring Security 6, Hibernate 6, Bean Validation |
| **Database** | MySQL 8.x (InnoDB engine, utf8mb4 character set) |
| **Authentication** | Stateless JSON Web Tokens (JJWT 0.12.6) with HMAC-SHA256, BCrypt password encoder |
| **Build & Package** | Apache Maven 3.10+, Embedded Tomcat |

---

## 3. System Architecture & Relational Database Design

### Layered Architecture
```
[ Browser Client: HTML5 / CSS3 / Vanilla JS / Fetch API / Chart.js ]
                               |  HTTP / REST (JSON) + Bearer JWT
                               v
 [ Security Filter Chain: JwtAuthenticationFilter -> DaoAuthenticationProvider ]
                               |
                   [ REST Controllers Layer ]
          (Auth, Vendors, Products, Categories, Inventory,
           Procurement, Purchase Orders, Deliveries, Reports)
                               |
                     [ Service Layer (DTOs) ]
              (Business Logic, Transaction Management)
                               |
                    [ Spring Data JPA Repositories ]
                               |
                     [ Hibernate 6 ORM ]
                               |
               [ MySQL 8.x Database Engine ]
```

### Relational Database Schema (11 Normalized Tables)
1. `users`: Stores user credentials, roles (`ADMIN`, `VENDOR`), profile info, and optional foreign key to `vendors`.
2. `vendors`: Supplier directory (company name, contact person, email, phone, address, city, category, status).
3. `categories`: Classification taxonomy for inventory items.
4. `products`: Catalog items (SKU, name, description, unit price, unit of measurement, category FK).
5. `inventory`: Warehouse stock tracking (product FK, available quantity, minimum and maximum thresholds, last updated).
6. `procurement_requests`: Requisitions submitted by buyers for vendor approval (`DRAFT`, `PENDING`, `APPROVED`, `REJECTED`, `CONVERTED_TO_ORDER`).
7. `procurement_request_items`: Line items for requisitions (quantity, estimated unit cost, subtotal).
8. `purchase_orders`: Binding orders (`PENDING`, `APPROVED`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`, subtotal, 5% tax, grand total).
9. `purchase_order_items`: Line items for purchase orders (product, quantity, unit price, total price).
10. `deliveries`: Freight tracking (`PREPARING`, `DISPATCHED`, `IN_TRANSIT`, `OUT_FOR_DELIVERY`, `DELIVERED`, `DELAYED`, carrier, tracking number, milestones).
11. `stock_transactions`: Immutable audit ledger recording inventory delta (`STOCK_ADDED`, `STOCK_REMOVED`, `PURCHASE_RECEIVED`, `MANUAL_ADJUSTMENT`).

---

## 4. Key Application Features

### 1. Operations Analytics Dashboard
- Dynamic KPI cards: Total Vendors, Total Products, Warehouse Stock Units, Purchase Orders, Pending Orders, Completed Deliveries, Low-Stock Alerts, Total Spend.
- **Chart.js Visualizations**:
  - Monthly Orders & Expenditure (Dual-axis Line Chart).
  - Order Status Breakdown (Doughnut Chart).
  - Inventory Stock Distribution by Category (Bar Chart).
- Operational Feed: Recent purchase orders, stock movements, and registered vendors.

### 2. Vendor Management
- Complete CRUD operations with search and status/category filters.
- Supplier profile modal displaying contact coordinates and complete order history.

### 3. Product & Category Management
- Categorized SKU catalog with unit pricing, unit of measurement, and listing status.
- Category management with real-time product count aggregation.
- Automatic creation of warehouse inventory records upon product creation.

### 4. Inventory Management & Safety Stock Alerts
- Real-time stock health monitoring with color-coded status badges.
- Configurable minimum and maximum safety stock thresholds.
- Low-stock alert cards with 1-click restock actions.
- Stock adjustment modal: Add stock, remove stock (with negative balance protection), or manual inventory overrides.

### 5. Procurement Requisitions & PO Conversion
- Create multi-item requisition requests with dynamic item rows and live total calculations.
- Administrative review workflow (Approve / Reject).
- **1-Click PO Conversion**: Automatically converts approved requisitions into active Purchase Orders.

### 6. Commercial Purchase Orders & Printable PO Document
- Multi-item line creation with tax computation (5.00% VAT/sales tax) and grand total.
- Order lifecycle states: `PENDING` &rarr; `APPROVED` &rarr; `PROCESSING` &rarr; `SHIPPED` &rarr; `DELIVERED` &rarr; `CANCELLED`.
- **Automatic Warehouse Intake**: Marking an order as `DELIVERED` automatically increments inventory stock and logs `PURCHASE_RECEIVED` ledger records.
- **Printable Purchase Order**: Formatted invoice layout with company header, vendor address, itemized table, payment terms, and dual signature lines.

### 7. Logistics & Delivery Management
- Shipment tracking linked to Purchase Orders.
- Carrier tracking numbers (FedEx, DHL, BlueDart, Freight Hub).
- Visual 5-step milestone stepper (`PREPARING` &rarr; `DISPATCHED` &rarr; `IN_TRANSIT` &rarr; `OUT_FOR_DELIVERY` &rarr; `DELIVERED`).

### 8. Stock Movement Audit Trail
- Immutable transaction history tracking previous quantity &rarr; new quantity, date, operator, and order references.

### 9. Business Intelligence Reports & CSV Export
- Tabbed reporting for Inventory Valuation, Vendor Performance, Purchase Orders, and Low-Stock Replenishment.
- Live graphical distributions for each report.
- Direct CSV data export (`/api/reports/export/{type}`) and browser print styling.

---

## 5. Sample Login Credentials

On initial application startup, `DataInitializer` automatically provisions the MySQL database with verified demonstration accounts and sample data:

| User Role | Email Address | Password | Permissions & Scope |
| :--- | :--- | :--- | :--- |
| **System Administrator** | `admin@supplysync.com` | `admin123` | Full access across all 14 modules, approvals, adjustments, and settings |
| **Vendor Representative** | `vendor@apexsupplies.com` | `vendor123` | Vendor dashboard, view assigned purchase orders, update shipment tracking |

> **Quick Login Tip**: The login page (`login.html`) includes 1-click credential filler buttons for both the Administrator and Vendor accounts.

---

## 6. Installation & Startup Instructions

### Prerequisites
1. **Java Development Kit**: Java 21 LTS installed and on `PATH` (`java -version`).
2. **Apache Maven**: Maven 3.9+ or 3.10+ installed (`mvn -version`).
3. **MySQL Server**: MySQL 8.x running on port `3306`.

### Step 1: Database Setup
The Spring Boot backend will automatically create the `supplysync` schema if your MySQL user has permission. Alternatively, you can create it manually:

```sql
CREATE DATABASE IF NOT EXISTS supplysync CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Verify your MySQL connection credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/supplysync?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### Step 2: Build the Application
In the project root folder, build the project and compile the executable JAR:
```bash
mvn clean package -DskipTests
```

### Step 3: Run the Application
Launch the standalone Spring Boot application:
```bash
java -jar target/supplysync-backend-1.0.0.jar
```

The application will start on port `8080`.

### Step 4: Access the Frontend
Open your web browser and navigate to:
```
http://localhost:8080/
```
Or directly open `http://localhost:8080/login.html`.

You can also run or preview the static frontend by serving the `frontend/` folder with any static web server (such as Python's `python -m http.server 3000` or VS Code Live Server) — the Fetch API client dynamically connects to `http://localhost:8080/api`.

### Step 5: Run Automated Verification Test Suite
You can execute the automated end-to-end integration test suite at any time to verify the health of all 14 pages, static assets, APIs, and business workflows:
```bash
python test_suite.py
```
This runs 16 comprehensive tests and outputs a pass/fail summary report.

---

## 7. RESTful API Endpoint Reference

### Authentication (`/api/auth`)
- `POST /api/auth/login` - Authenticate credentials and retrieve JWT bearer token.
- `POST /api/auth/register` - Register a new user account.

### User Profile (`/api/users`)
- `GET /api/users/me` - Retrieve current authenticated user profile.
- `PUT /api/users/me` - Update full name and contact phone.
- `POST /api/users/me/change-password` - Update account password.
- `GET /api/users` - List all system users (Admin only).

### Vendors (`/api/vendors`)
- `GET /api/vendors` - List vendors with search, status, and category filtering.
- `GET /api/vendors/{id}` - Retrieve vendor details.
- `POST /api/vendors` - Create new vendor (Admin only).
- `PUT /api/vendors/{id}` - Update vendor details (Admin only).
- `DELETE /api/vendors/{id}` - Delete vendor (Admin only).
- `GET /api/vendors/{id}/orders` - Retrieve historical purchase orders for a vendor.

### Product Catalog (`/api/products`)
- `GET /api/products` - List products with search, category, and status filters.
- `GET /api/products/{id}` - Retrieve product details.
- `POST /api/products` - Create product and initialize inventory record (Admin only).
- `PUT /api/products/{id}` - Update product details (Admin only).
- `DELETE /api/products/{id}` - Delete product and inventory (Admin only).

### Categories (`/api/categories`)
- `GET /api/categories` - List categories with associated product counts.
- `GET /api/categories/{id}` - Retrieve category details.
- `POST /api/categories` - Create new category (Admin only).
- `PUT /api/categories/{id}` - Update category (Admin only).
- `DELETE /api/categories/{id}` - Delete category (Admin only).
- `GET /api/categories/{id}/products` - List products under a specific category.

### Inventory & Stock Movements (`/api/inventory` & `/api/transactions`)
- `GET /api/inventory` - List inventory stock items with search and low-stock filter.
- `GET /api/inventory/low-stock` - List items currently below safety reorder levels.
- `POST /api/inventory/adjust` - Adjust stock (Stock Added, Removed, Manual Override) and write audit ledger.
- `PATCH /api/inventory/{id}/thresholds` - Update minimum and maximum stock levels.
- `GET /api/transactions` - Retrieve stock transaction history with search and type filters.

### Procurement Requisitions (`/api/procurement-requests`)
- `GET /api/procurement-requests` - List procurement requests with status and vendor filters.
- `POST /api/procurement-requests` - Create new multi-item requisition (Admin only).
- `PATCH /api/procurement-requests/{id}/status` - Approve or reject requisition.
- `POST /api/procurement-requests/{id}/convert-to-po` - Convert approved requisition into a Purchase Order.

### Purchase Orders (`/api/purchase-orders`)
- `GET /api/purchase-orders` - List purchase orders (automatically filtered for vendor users).
- `GET /api/purchase-orders/{id}` - Retrieve detailed purchase order with line items.
- `POST /api/purchase-orders` - Generate purchase order with 5% tax and total calculation.
- `PATCH /api/purchase-orders/{id}/status` - Update status (marking `DELIVERED` automatically updates warehouse inventory).
- `DELETE /api/purchase-orders/{id}` - Delete unfulfilled purchase order.

### Freight Deliveries (`/api/deliveries`)
- `GET /api/deliveries` - List deliveries with carrier, tracking, and shipment status.
- `POST /api/deliveries` - Associate delivery tracking with a Purchase Order.
- `PATCH /api/deliveries/{id}/status` - Update delivery milestone (`PREPARING` to `DELIVERED`).

### Analytics & Reports (`/api/dashboard` & `/api/reports`)
- `GET /api/dashboard/admin` - Comprehensive admin analytics, 3 Chart.js graphs, and live operational stats.
- `GET /api/dashboard/vendor` - Vendor specific dashboard metrics and order statistics.
- `GET /api/reports/inventory` - Inventory valuation report data.
- `GET /api/reports/vendors` - Supplier performance report data.
- `GET /api/reports/purchase-orders` - Purchase order spend report data.
- `GET /api/reports/low-stock` - Replenishment deficit report data.
- `GET /api/reports/export/{type}` - Download CSV spreadsheet for any report.

---

## 8. Frontend Structure

```
frontend/
├── index.html              # Landing page and auto-redirect guard
├── login.html              # Modern login page with 1-click demo credentials
├── dashboard.html          # Operations dashboard with Chart.js analytics
├── vendors.html            # Vendor directory and CRUD management
├── categories.html         # Category classification and product list
├── products.html           # Product catalog and SKU pricing
├── inventory.html          # Warehouse stock management and thresholds
├── procurement.html        # Procurement requisitions & PO conversion
├── purchase-orders.html    # Commercial purchase orders & printable view
├── deliveries.html         # Freight shipments and milestone stepper
├── transactions.html       # Stock movement audit ledger
├── reports.html            # Business intelligence reports & CSV export
├── profile.html            # User credentials and password updates
├── settings.html           # System parameters and warehouse diagnostics
├── css/
│   ├── style.css           # Global theme, variables, typography, buttons, tables
│   ├── dashboard.css       # Sidebar, topbar, KPI cards, analytics grid
│   ├── components.css      # Modals, toasts, search bars, printable PO
│   └── responsive.css     # Mobile drawer navigation and responsive breakpoints
└── js/
    ├── api.js              # Central Fetch API client with Bearer JWT injection
    ├── auth.js             # Authentication state, session handling, route guards
    ├── common.js           # Dynamic sidebar generator, modals, toasts, formatters
    ├── dashboard.js        # Analytics data fetcher and Chart.js renderer
    ├── vendors.js          # Vendor CRUD and order history
    ├── categories.js       # Category management
    ├── products.js         # Product management and SKU listing
    ├── inventory.js        # Stock level management and adjustments
    ├── procurement.js      # Procurement requisitions and PO conversion
    ├── orders.js           # Purchase orders and printable document view
    ├── deliveries.js       # Logistics tracking and milestone stepper
    ├── transactions.js     # Stock movement audit logs
    ├── reports.js          # Reports generation, charts, and CSV download
    └── profile.js          # User profile and password change
```

---

## 9. Verification & Quality Assurance Summary

The entire stack has been verified against the acceptance criteria:
1. **Compilation**: Clean compilation of all 80 Java source files under Java 21 with Maven 3.10.
2. **Database Integration**: MySQL 8.x schema auto-creation and verification on `localhost:3306`.
3. **Seed Data**: Automatic initialization of demo administrators, vendors, categories, products, inventory with low-stock alerts, procurement requests, purchase orders, deliveries, and stock transactions.
4. **Security**: Stateless JWT authentication with BCrypt hashing verified for both Admin and Vendor roles.
5. **Business Logic**:
   - Stock adjustments accurately update available stock and record audit transactions.
   - Low-stock alerts trigger dynamically when stock falls below minimum thresholds.
   - Procurement requisitions convert into active Purchase Orders with 1 click.
   - Changing PO / Delivery status to `DELIVERED` automatically increments warehouse stock and logs a `PURCHASE_RECEIVED` ledger entry.
   - Real-time CSV generation and downloads verified.
6. **Frontend Integration**: Static pages served directly on port `8080`, communicating cleanly via REST Fetch API.
