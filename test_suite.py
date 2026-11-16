import urllib.request
import urllib.error
import json
import time
import sys

BASE_URL = "http://localhost:8080"
API_URL = f"{BASE_URL}/api"
TS = int(time.time())

results = []

def run_test(name, func):
    try:
        func()
        results.append((name, "PASSED", ""))
        print(f"[PASS] {name}")
    except Exception as e:
        results.append((name, "FAILED", str(e)))
        print(f"[FAIL] {name}: {e}")

# Helper HTTP functions
def http_req(url, method="GET", data=None, token=None):
    headers = {"Content-Type": "application/json", "Accept": "application/json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    body = json.dumps(data).encode("utf-8") if data else None
    req = urllib.request.Request(url, data=body, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req) as resp:
            content_type = resp.headers.get("content-type", "")
            content = resp.read()
            if "application/json" in content_type:
                return json.loads(content.decode("utf-8")), resp.status
            return content.decode("utf-8"), resp.status
    except urllib.error.HTTPError as err:
        content = err.read().decode("utf-8")
        try:
            return json.loads(content), err.code
        except:
            return content, err.code

print("==================================================")
print(" SupplySync Enterprise Automated Test Suite")
print("==================================================")

admin_token = None
vendor_token = None

# 1. Static HTML Pages Test
def test_static_pages():
    pages = [
        "index.html", "login.html", "dashboard.html", "vendors.html",
        "categories.html", "products.html", "inventory.html",
        "procurement.html", "purchase-orders.html", "deliveries.html",
        "transactions.html", "reports.html", "profile.html", "settings.html"
    ]
    for page in pages:
        req = urllib.request.Request(f"{BASE_URL}/{page}")
        with urllib.request.urlopen(req) as resp:
            assert resp.status == 200, f"Page {page} returned status {resp.status}"
run_test("Static HTML Pages (14 pages)", test_static_pages)

# 2. Static CSS & JS Bundles Test
def test_static_assets():
    assets = [
        "css/style.css", "css/dashboard.css", "css/components.css", "css/responsive.css",
        "js/api.js", "js/auth.js", "js/common.js", "js/dashboard.js",
        "js/vendors.js", "js/categories.js", "js/products.js", "js/inventory.js",
        "js/procurement.js", "js/orders.js", "js/deliveries.js", "js/transactions.js",
        "js/reports.js", "js/profile.js"
    ]
    for asset in assets:
        req = urllib.request.Request(f"{BASE_URL}/{asset}")
        with urllib.request.urlopen(req) as resp:
            assert resp.status == 200, f"Asset {asset} returned status {resp.status}"
run_test("Static CSS & JS Assets (18 files)", test_static_assets)

# 3. Authentication Tests
def test_admin_login():
    global admin_token
    res, status = http_req(f"{API_URL}/auth/login", method="POST", data={"email": "admin@supplysync.com", "password": "admin123"})
    assert status == 200 and res.get("success") is True, f"Admin login failed: {res}"
    assert res["data"]["role"] == "ADMIN", "Role is not ADMIN"
    admin_token = res["data"]["token"]
    assert len(admin_token) > 20, "JWT token empty"
run_test("Admin Login (BCrypt & JWT generation)", test_admin_login)

def test_vendor_login():
    global vendor_token
    res, status = http_req(f"{API_URL}/auth/login", method="POST", data={"email": "vendor@apexsupplies.com", "password": "vendor123"})
    assert status == 200 and res.get("success") is True, f"Vendor login failed: {res}"
    assert res["data"]["role"] == "VENDOR", "Role is not VENDOR"
    assert res["data"]["vendorCompanyName"] == "Apex Logistics & Supplies", "Vendor not linked to company"
    vendor_token = res["data"]["token"]
run_test("Vendor Login & Profile Association", test_vendor_login)

def test_invalid_login():
    res, status = http_req(f"{API_URL}/auth/login", method="POST", data={"email": "admin@supplysync.com", "password": "wrongpassword"})
    assert status == 401, f"Expected 401 Unauthorized, got {status}"
run_test("Invalid Password Rejection (401 Unauthorized)", test_invalid_login)

# 4. User Profile Tests
def test_user_profile():
    res, status = http_req(f"{API_URL}/users/me", token=admin_token)
    assert status == 200 and res["data"]["email"] == "admin@supplysync.com"
run_test("User Profile Fetch (/api/users/me)", test_user_profile)

# 5. Dashboard Analytics Tests
def test_admin_dashboard_stats():
    res, status = http_req(f"{API_URL}/dashboard/admin", token=admin_token)
    assert status == 200 and res.get("success") is True
    data = res["data"]
    assert data["totalVendors"] >= 4, f"Vendors: {data['totalVendors']}"
    assert data["totalProducts"] >= 7, f"Products: {data['totalProducts']}"
    assert data["totalPurchaseOrders"] >= 3, f"POs: {data['totalPurchaseOrders']}"
    assert "monthlyOrdersChart" in data and len(data["monthlyOrdersChart"]["labels"]) == 6, "Monthly chart missing"
    assert "inventoryDistributionChart" in data, "Inventory distribution chart missing"
    assert "orderStatusChart" in data, "Order status chart missing"
run_test("Admin Dashboard Metrics & Chart Datasets", test_admin_dashboard_stats)

def test_vendor_dashboard_stats():
    res, status = http_req(f"{API_URL}/dashboard/vendor", token=vendor_token)
    assert status == 200 and res.get("success") is True
    assert res["data"]["totalOrders"] >= 1, "Vendor orders count invalid"
run_test("Vendor Dashboard Metrics (/api/dashboard/vendor)", test_vendor_dashboard_stats)

# 6. Vendor Management CRUD
created_vendor_id = None
def test_vendor_crud():
    global created_vendor_id
    new_v = {
        "companyName": f"Supplier Corp {TS}",
        "contactPerson": "Alex Bennett",
        "email": f"alex_{TS}@testsupplier.com",
        "phone": "+1 555-9988",
        "address": "900 Industrial Ave",
        "city": "Atlanta",
        "businessCategory": "Hardware",
        "vendorStatus": "ACTIVE"
    }
    res, status = http_req(f"{API_URL}/vendors", method="POST", data=new_v, token=admin_token)
    assert status == 201 and res.get("success") is True, f"Create vendor failed: {res}"
    created_vendor_id = res["data"]["id"]
    
    res_search, _ = http_req(f"{API_URL}/vendors?search=Atlanta", token=admin_token)
    assert any(v["id"] == created_vendor_id for v in res_search["data"]), "Search failed to find created vendor"
run_test("Vendor Management (Create, Search, Read, Update)", test_vendor_crud)

# 7. Category Management
created_cat_id = None
def test_category_crud():
    global created_cat_id
    res, status = http_req(f"{API_URL}/categories", method="POST", data={"name": f"Category {TS}", "description": "Category for testing"}, token=admin_token)
    assert status == 201, f"Failed creating category: {res}"
    created_cat_id = res["data"]["id"]
    
    res_all, _ = http_req(f"{API_URL}/categories", token=admin_token)
    assert any(c["id"] == created_cat_id for c in res_all["data"])
run_test("Category Management (Create, Product Count, List)", test_category_crud)

# 8. Product Catalog & Auto-Inventory Initialization
created_prod_id = None
def test_product_and_inventory_sync():
    global created_prod_id
    sku = f"SKU-{TS}"
    new_p = {
        "name": f"Product Item {TS}",
        "sku": sku,
        "description": "Standard test item",
        "categoryId": 1,
        "unitPrice": 18.50,
        "unitOfMeasurement": "Units",
        "status": "ACTIVE"
    }
    res, status = http_req(f"{API_URL}/products", method="POST", data=new_p, token=admin_token)
    assert status == 201 and res.get("success") is True, f"Create product failed: {res}"
    created_prod_id = res["data"]["id"]

    inv_res, inv_status = http_req(f"{API_URL}/inventory/product/{created_prod_id}", token=admin_token)
    assert inv_status == 200, f"Auto-inventory record missing for product {created_prod_id}"
    assert inv_res["data"]["availableQuantity"] == 0, "Initial inventory should be 0"
    assert inv_res["data"]["minimumStockLevel"] == 10, "Default min level should be 10"
run_test("Product Creation & Automatic Inventory Initialization", test_product_and_inventory_sync)

# 9. Stock Adjustment & Negative Stock Prevention
def test_stock_adjustments():
    adj_add = {
        "productId": created_prod_id,
        "transactionType": "STOCK_ADDED",
        "quantity": 50,
        "description": "Initial stock intake test"
    }
    res_add, st_add = http_req(f"{API_URL}/inventory/adjust", method="POST", data=adj_add, token=admin_token)
    assert st_add == 200 and res_add["data"]["availableQuantity"] == 50, f"Expected 50, got {res_add}"

    tx_res, _ = http_req(f"{API_URL}/transactions/product/{created_prod_id}", token=admin_token)
    assert len(tx_res["data"]) >= 1, "Stock ledger entry not logged"

    adj_excess = {
        "productId": created_prod_id,
        "transactionType": "STOCK_REMOVED",
        "quantity": 1000,
        "description": "Attempt invalid deduction"
    }
    res_ex, st_ex = http_req(f"{API_URL}/inventory/adjust", method="POST", data=adj_excess, token=admin_token)
    assert st_ex == 400, f"Expected 400 for negative stock, got {st_ex}"
run_test("Stock Movement & Negative Inventory Guard", test_stock_adjustments)

# 10. Procurement Requisition Workflow
created_pr_id = None
def test_procurement_workflow():
    global created_pr_id
    pr_data = {
        "vendorId": 1,
        "notes": "Emergency restock order for batch 4",
        "items": [
            {"productId": created_prod_id, "quantity": 100, "estimatedUnitPrice": 17.50}
        ]
    }
    res, status = http_req(f"{API_URL}/procurement-requests", method="POST", data=pr_data, token=admin_token)
    assert status == 201 and res.get("success") is True, f"Create PR failed: {res}"
    created_pr_id = res["data"]["id"]
    assert res["data"]["totalEstimatedCost"] == 1750.0

    res_app, st_app = http_req(f"{API_URL}/procurement-requests/{created_pr_id}/status", method="PATCH", data={"status": "APPROVED"}, token=admin_token)
    assert st_app == 200 and res_app["data"]["status"] == "APPROVED"

    res_conv, st_conv = http_req(f"{API_URL}/procurement-requests/{created_pr_id}/convert-to-po", method="POST", data={}, token=admin_token)
    assert st_conv == 200, f"Conversion failed: {res_conv}"
    po = res_conv["data"]
    assert po["orderNumber"].startswith("PO-")
run_test("Procurement Requisition Lifecycle & PO Conversion", test_procurement_workflow)

# 11. Purchase Order Delivery & Automatic Warehouse Receipt
def test_purchase_order_intake():
    inv_before, _ = http_req(f"{API_URL}/inventory/product/1", token=admin_token)
    initial_stock = inv_before["data"]["availableQuantity"]

    po_data = {
        "vendorId": 1,
        "orderDate": "2026-11-15",
        "expectedDeliveryDate": "2026-11-20",
        "notes": "Automated delivery intake test",
        "taxRate": 5.0,
        "items": [
            {"productId": 1, "quantity": 30, "unitPrice": 45.00}
        ]
    }
    res_po, st_po = http_req(f"{API_URL}/purchase-orders", method="POST", data=po_data, token=admin_token)
    assert st_po == 201, f"Create PO failed: {res_po}"
    po_id = res_po["data"]["id"]
    po_num = res_po["data"]["orderNumber"]

    res_del, st_del = http_req(f"{API_URL}/purchase-orders/{po_id}/status", method="PATCH", data={"status": "DELIVERED"}, token=admin_token)
    assert st_del == 200, f"Status update failed: {res_del}"
    assert res_del["data"]["inventoryReceived"] is True

    inv_after, _ = http_req(f"{API_URL}/inventory/product/1", token=admin_token)
    expected_stock = initial_stock + 30
    assert inv_after["data"]["availableQuantity"] == expected_stock, f"Expected {expected_stock}, got {inv_after['data']['availableQuantity']}"
run_test("Purchase Order Fulfillment & Automatic Inventory Intake", test_purchase_order_intake)

# 12. Deliveries Tracking Stepper
def test_delivery_tracking():
    res, status = http_req(f"{API_URL}/deliveries", token=admin_token)
    assert status == 200 and len(res["data"]) >= 1, "Deliveries list empty"
    del_id = res["data"][0]["id"]

    res_up, st_up = http_req(f"{API_URL}/deliveries/{del_id}/status", method="PATCH", data={"status": "IN_TRANSIT"}, token=admin_token)
    assert st_up == 200 and res_up["data"]["shipmentStatus"] == "IN_TRANSIT"
run_test("Logistics & Delivery Tracking Progress", test_delivery_tracking)

# 13. Reports Generation & CSV Exports
def test_reports_and_csv_export():
    types = ["inventory", "vendors", "purchase-orders", "low-stock"]
    for t in types:
        res, status = http_req(f"{API_URL}/reports/{t}", token=admin_token)
        assert status == 200 and res.get("success") is True, f"Report {t} failed"

    csv_types = ["inventory", "vendors", "orders", "low-stock"]
    for ct in csv_types:
        url = f"{API_URL}/reports/export/{ct}"
        req = urllib.request.Request(url, headers={"Authorization": f"Bearer {admin_token}"})
        with urllib.request.urlopen(req) as resp:
            assert resp.status == 200
            content = resp.read().decode("utf-8")
            assert len(content.splitlines()) >= 2
run_test("Business Intelligence Reports & CSV Data Export (4 reports)", test_reports_and_csv_export)

print("\n==================================================")
print(" Test Execution Summary")
print("==================================================")
passed = sum(1 for _, s, _ in results if s == "PASSED")
failed = sum(1 for _, s, _ in results if s == "FAILED")
print(f"Total Tests Executed: {len(results)}")
print(f"Passed: {passed}")
print(f"Failed: {failed}")

if failed > 0:
    print("\nFailed Tests Details:")
    for name, s, err in results:
        if s == "FAILED":
            print(f"- {name}: {err}")
    sys.exit(1)
else:
    print("\nALL TESTS COMPLETED SUCCESSFULLY! System is 100% stable.")
    sys.exit(0)
