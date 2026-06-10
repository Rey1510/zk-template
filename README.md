# ZK Framework Spring Boot Template

A clean template for building web applications using **Java**, **Spring Boot**, **ZK Framework**, and **PostgreSQL**.

## 🛠️ Tech Stack

This project leverages the following technologies and versions from [pom.xml](file:///d:/Iseng/zk-template/pom.xml):
- **Core Language**: **Java 21**
- **Framework**: **Spring Boot 3.5.14**
- **UI Architecture**: **ZK Framework 10.0.0-jakarta** (running with **zkspring-core 6.0.0**)
- **Data Persistence**: **Spring Data JPA** with **Hibernate**
- **Database Driver**: **PostgreSQL JDBC 42.2.12**
- **Database Migration**: **Flyway Migration 10.x**
- **Utilities**: **Apache POI (OOXML) 5.4.1** (for Excel spreadsheet processing)
- **Code Optimization**: **Lombok** (annotation library to reduce boilerplate code)
- **Data Validation**: **Spring Boot Validation Starter** (Hibernate Validator)

---

## 🚀 Getting Started

### 1. Clone the Repository
Clone the project repository to your local machine:
```bash
git clone https://github.com/Rey1510/zk-template.git
cd zk-template
```

### 2. Database Configuration & Setup

The application connects to **PostgreSQL**. Configure your credentials in [application.properties](file:///d:/Iseng/zk-template/src/main/resources/application.properties):

```properties
spring.datasource.url=jdbc:postgresql://<host>:<port>/<database>
spring.datasource.username=<username>
spring.datasource.password=<password>
spring.jpa.properties.hibernate.default_schema=zktmp
```

#### DDL Schema Definition
Run the following SQL statements on your PostgreSQL database under the schema `zktmp` (create the schema first with `CREATE SCHEMA zktmp;` if it does not exist) to initialize the tables:

```sql
-- 1. Create Master Tables
CREATE TABLE zktmp.mst_user (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE zktmp.mst_role (
    role_id SERIAL PRIMARY KEY,
    role_code VARCHAR(30) NOT NULL UNIQUE,
    role_name VARCHAR(100) NOT NULL
);

CREATE TABLE zktmp.mst_menu (
    menu_id SERIAL PRIMARY KEY,
    menu_code VARCHAR(50) NOT NULL UNIQUE,
    menu_name VARCHAR(100) NOT NULL,
    zul_path VARCHAR(200) NOT NULL,
    menu_order INTEGER NOT NULL,
    active BOOLEAN DEFAULT TRUE
);

-- 2. Create Relation Tables
CREATE TABLE zktmp.rel_user_role (
    rel_user_role_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES zktmp.mst_user(user_id) ON DELETE CASCADE,
    role_id INTEGER NOT NULL REFERENCES zktmp.mst_role(role_id) ON DELETE CASCADE
);

CREATE TABLE zktmp.rel_role_menu (
    rel_role_menu_id SERIAL PRIMARY KEY,
    role_id INTEGER NOT NULL REFERENCES zktmp.mst_role(role_id) ON DELETE CASCADE,
    menu_id INTEGER NOT NULL REFERENCES zktmp.mst_menu(menu_id) ON DELETE CASCADE
);

-- 3. Create Feature Table
CREATE TABLE zktmp.report (
    id SERIAL PRIMARY KEY,
    report_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_by VARCHAR(50),
    created_date TIMESTAMP,
    updated_by VARCHAR(50),
    updated_date TIMESTAMP
);
```

#### Seed Mock Data
Insert sample roles, menus, users, and relationships to get started:

```sql
-- Insert Roles
INSERT INTO zktmp.mst_role (role_code, role_name) VALUES 
('ADMIN', 'Administrator'),
('MAKER', 'Maker');

-- Insert Menus
INSERT INTO zktmp.mst_menu (menu_code, menu_name, zul_path, menu_order, active) VALUES 
('HOME', 'Home', '/pages/home.zul', 1, TRUE),
('REPORT', 'Report', '/pages/report.zul', 2, TRUE);

-- Insert Users (Passwords are plain text for template demonstration)
INSERT INTO zktmp.mst_user (username, full_name, email, password, active) VALUES 
('admin', 'Administrator', 'admin@example.com', 'admin123', TRUE),
('maker', 'Maker', 'maker@example.com', 'maker123', TRUE);

-- Map Users to Roles (admin gets ADMIN and MAKER roles, maker gets MAKER role)
INSERT INTO zktmp.rel_user_role (user_id, role_id) VALUES 
(1, 1), -- admin -> ADMIN
(1, 2), -- admin -> MAKER
(2, 2); -- maker -> MAKER

-- Map Menus to Roles (ADMIN gets HOME & REPORT, MAKER gets HOME only)
INSERT INTO zktmp.rel_role_menu (role_id, menu_id) VALUES 
(1, 1), -- ADMIN -> HOME
(1, 2), -- ADMIN -> REPORT
(2, 1); -- MAKER -> HOME

-- Insert Initial Reports
INSERT INTO zktmp.report (report_name, status, created_by, created_date) VALUES 
('Quarterly Financial Audit', 'SUCCESS', 'admin', CURRENT_TIMESTAMP),
('Weekly Server Status Logs', 'PENDING', 'admin', CURRENT_TIMESTAMP),
('Customer Transaction Batch', 'FAILED', 'maker', CURRENT_TIMESTAMP);
```

---

## 🔑 Authentication & Navigation Flow

The security mapping and menu rendering model utilizes a **Role-Based Access Control (RBAC)** architecture integrated with Keycloak Single Sign-On (SSO):

```mermaid
sequenceDiagram
    actor User
    participant App as ZK Application
    participant Filter as AuthenticationFilter
    participant Keycloak as Keycloak Server
    participant Controller as SSOController
    participant DB as PostgreSQL Database

    User->>App: Access App (e.g. /)
    App->>Filter: Check Session
    Note over Filter: User not logged in?
    Filter-->>User: Redirect to Keycloak Auth Screen
    User->>Keycloak: Enter Credentials
    Keycloak-->>User: Redirect to Callback with Auth Code
    User->>Controller: GET /login/oauth2/code/keycloak?code=XYZ
    Controller->>Keycloak: POST /token (Exchange Code)
    Keycloak-->>Controller: Return Access, ID & Refresh Tokens
    Controller->>Keycloak: GET /userinfo (Bearer Token)
    Keycloak-->>Controller: Return User Profile (preferred_username)
    Controller->>DB: Query MstUser & Roles by Username
    DB-->>Controller: Return User details & Assigned Roles
    Note over Controller: Establish local UserSession & store ID Token
    Controller-->>User: Redirect to main page (/layout/main.zul)
```

### 1. User Authenticating (SSO Flow)
- Unauthenticated requests are intercepted by **`AuthenticationFilter`** and redirected to Keycloak's login screen (`/realms/demo/protocol/openid-connect/auth`).
- After successful credentials input, Keycloak redirects the user back to the application callback `/login/oauth2/code/keycloak` with an authorization code.
- **`SSOController`** exchanges this code for access and ID tokens, calls Keycloak's `/userinfo` endpoint to fetch the user's `preferred_username`, and initiates a passwordless login locally via `CurrentUserService.loginSso(username)`.
- Keycloak username lookup matches the `mst_user` table in the database to verify active status and load assigned roles.

### 2. Role Resolution (Responsibilities)
- Upon successful authentication, **`RelUserRoleRepository`** is queried to find all active roles associated with that user.
- These roles are mapped to **`ResponsibilityDTO`** models and stored inside the session-scoped **`UserSession`** bean.
- The user can switch their active role/responsibility dynamically during the session.

### 3. Dynamic Menu Filtering
- The sidebar or main layout queries **`MenuService`** for the list of available menus.
- **`MenuServiceImpl`** fetches active menus using **`RelRoleMenuRepository`** based on the currently selected role.
- Menus are ordered sequentially using the `menu_order` field.

### 4. Direct Logout
- Clicking logout triggers `/logout` which invalidates the local session, retrieves the stored `id_token` from the session, and redirects to Keycloak's logout endpoint with `id_token_hint` and `post_logout_redirect_uri` to cleanly terminate the SSO session without prompting confirmation.

### 5. Idle Timeout
- The local HTTP session is configured to expire after 10 minutes of inactivity via `server.servlet.session.timeout=10m` in [application.properties](file:///d:/Project/Rey/zk-template/src/main/resources/application.properties). Once expired, the user will be prompted to re-authenticate on Keycloak on their next request.

---

## 🛠️ Build & Run

Ensure you have **Java 21** installed, then run:

```bash
# Compile the project
./mvnw clean compile

# Launch the Spring Boot application
./mvnw spring-boot:run
```
Open **`http://localhost:8080/`** in your browser. You will be automatically redirected to your Keycloak SSO login page.
