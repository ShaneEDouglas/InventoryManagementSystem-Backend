# InvenFlow - Enterprise Inventory Management System

InvenFlow is a robust, multi-tenant backend built with **Spring Boot** designed to manage inventory for small to medium-sized businesses. It features secure Role-Based Access Control (RBAC), company-wide data isolation, and comprehensive inventory tracking.

## Key Features

###  Security & Authentication
* **HttpOnly Cookies:** Secure session management that prevents XSS attacks.
* **RBAC (Role-Based Access Control):** distinct permissions for **ADMIN**, **MANAGER**, **EMPLOYEE**, and  **VIEWER".
* **Invitation System:** Admins can generate unique **Invite Keys** to onboard new employees securely.

###  Multi-Tenancy and privilege isolation
* **Company Isolation:** Users only see data belonging to their specific company.
* **Data Relationships:** Complex mapping between Users, Companies, and Products.
* **Audit Trails:** Every product tracks who created it and when.

###  Inventory Management
* **Stock Adjustments:** dedicated endpoints for restocking and auditing inventory counts.
* **Global Visibility:** Employees can view all company assets while maintaining data integrity.
* **Product Categories:** Organized management of assets (Electronics, Furniture, etc.).

---

## 🛠️ Tech Stack

* **Language:** Java 21+
* **Framework:** Spring Boot 3
* **Database:** PostgreSQL (with JPA/Hibernate)
* **Documentation:** OpenAPI / Swagger UI
* **Build Tool:** Gradle

---

## API Documentation

The API is fully documented using Swagger UI.
To open the Swagger UI, run the application and paste the URL in the browser:
http://localhost:8080/swagger-ui/index.html

### Core Endpoints

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Secure login with cookie generation | Public |
| `POST` | `/api/company/join` | Join a company using an Invite Key | User |
| `GET` | `/api/product/get` | Retrieve all company inventory | Employee/Admin |
| `PATCH` | `/api/product/stock/{id}` | Adjust stock levels | Admin/Manager |
| `DELETE` | `/api/company/delete` | Delete company data | **Admin Only** |

---
and more to be listed

## Getting Started 💻

* **Clone the Repo in your choice of Java-based IDE**
* **Update the "application. properties" file with your PostgreSQL database credentials**
* **Run the Spring project**
* **Go to the Swagger UI using the link and test the endpoints**
  ```bash
  http://localhost:8080/swagger-ui/index.html
  ```

