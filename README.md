<<<<<<< HEAD
# AutoMart — Used Car Marketplace

Full-stack project: **Spring Boot 3.3 (Java 17) + MySQL** backend, **React (Vite)** frontend,
JWT authentication, role-based access (Buyer / Seller / Admin), search & filters, favorites,
enquiries, and an admin approval workflow for listings.

```
automart/
├── backend/     Spring Boot REST API
├── frontend/    React (Vite) single-page app
└── README.md    (this file)
```

---

## 1. What you need to install

| Tool | Version | Check with |
|---|---|---|
| **JDK** | 17 or newer | `java -version` |
| **Maven** | 3.6+ | `mvn -version` |
| **MySQL** | 8.x | `mysql --version` |
| **Node.js** | 18+ (includes npm) | `node -v` |
| An IDE | IntelliJ IDEA (Community is fine) or VS Code | — |

Notes:
- If you use **IntelliJ IDEA**, it bundles its own Maven — you don't strictly need to install
  Maven separately; just open the `backend` folder as a Maven project and let it download
  dependencies.
- If you don't have MySQL installed, the easiest options are the MySQL Installer (Windows),
  Homebrew `brew install mysql` (Mac), or `sudo apt install mysql-server` (Linux). MySQL
  Workbench is handy for viewing the tables once things are running.

---

## 2. Database setup

Open a MySQL client (Workbench, DBeaver, or `mysql -u root -p`) and run:

```sql
CREATE DATABASE automart;
```

That's it — Hibernate will create all the tables for you the first time the backend starts,
because `spring.jpa.hibernate.ddl-auto=update` is set in `application.properties`.

---

## 3. Backend setup (Spring Boot)

1. Open `backend/src/main/resources/application.properties` and set your MySQL password:
   ```properties
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```
2. From a terminal:
   ```bash
   cd automart/backend
   mvn clean install
   mvn spring-boot:run
   ```
   Or just run `AutomartApplication.java` directly from your IDE (right-click → Run).
3. The API starts on **http://localhost:8080**. You should see Hibernate logs creating tables
   (`users`, `cars`, `car_images`, `favorites`, `enquiries`) in the console.

### Creating the first ADMIN account

Registration (`POST /api/auth/register`) only allows `BUYER` or `SELLER` — this is deliberate,
so nobody can grant themselves admin rights through the public API. To create an admin:

1. Register a normal account (any role) through the app.
2. In MySQL, promote it:
   ```sql
   UPDATE users SET role = 'ADMIN' WHERE email = 'you@example.com';
   ```
3. Log out and log back in (so a fresh JWT is issued with the ADMIN role).

---

## 4. Frontend setup (React + Vite)

```bash
cd automart/frontend
npm install
npm run dev
```

The app starts on **http://localhost:3000**. The Vite dev server proxies any request to
`/api/**` and `/uploads/**` straight through to the backend on port 8080 (see
`vite.config.js`), so you don't need to configure CORS URLs by hand while developing.

For a production build: `npm run build` outputs static files into `frontend/dist/`, which you
can serve from any static host (or drop into Spring Boot's `src/main/resources/static/` if you
want a single deployable JAR — same trick used in the FoodieHub project).

---

## 5. Trying it out end-to-end

1. Register as a **Seller**, log in, go to **Sell Car**, and list a car. It starts out
   `PENDING`.
2. Promote a second account to **ADMIN** (see above), log in as them, open **Admin**, and
   **Approve** the listing.
3. Log in as a **Buyer** (or browse without logging in), go to **Buy Cars**, search/filter,
   open the car, **Save** it to favorites, and send an **Enquiry**.
4. Log back in as the seller and check **My Listings** to see the enquiry-worthy listing, or
   mark it **Sold**.

---

## 6. Project architecture

```
React (Axios, JWT in localStorage)
        │  REST + Authorization: Bearer <token>
        ▼
Spring Boot
  Controller  →  Service  →  Repository (Spring Data JPA)
        │                         │
   DTOs in/out              Hibernate / JPA
        │                         │
  JWT filter + SecurityConfig    MySQL
```

**Backend package layout** (`backend/src/main/java/com/automart/`):

```
entity/       User, Car, CarImage, Favorite, Enquiry + enums (Role, CarStatus, FuelType, Transmission, EnquiryStatus)
repository/   Spring Data JPA interfaces + CarSpecification (dynamic search filters)
dto/          Request/response objects — the API never exposes entities directly
service/      Business logic: AuthService, CarService, FavoriteService, EnquiryService, FileStorageService
controller/   REST endpoints, thin — validate input, delegate to services
security/     JwtUtil, JwtAuthFilter, UserPrincipal, CustomUserDetailsService
config/       SecurityConfig (JWT filter chain, role rules, CORS), WebConfig (serves /uploads)
exception/    Custom exceptions + GlobalExceptionHandler (@RestControllerAdvice)
```

**Frontend layout** (`frontend/src/`):

```
pages/        One component per route (Home, Login, Register, CarList, CarDetails, SellCar, MyListings, Favorites, AdminDashboard)
components/   Reusable pieces (Navbar, CarCard, SearchFilter, CarForm, ProtectedRoute, Footer)
services/     Axios wrappers per resource (authService, carService, favoriteService, enquiryService, adminService)
context/      AuthContext — holds the logged-in user + JWT, exposes login/register/logout
```

### Key design decisions (good interview talking points)

- **Why DTOs instead of returning entities?** Returning a `Car` entity directly would leak
  Hibernate lazy-loading proxies into JSON (and risk `LazyInitializationException`), and it
  couples your API's shape to your database schema. `CarResponse`/`CarRequest` decouple those.
- **Why `Favorite` as its own entity instead of `@ManyToMany`?** A plain `@ManyToMany` gives
  you a bare join table with no room to grow (e.g. a `createdAt` timestamp) and makes queries
  like "when did I favorite this?" awkward. Modelling it as its own entity with a unique
  `(user_id, car_id)` constraint is the more realistic, more interview-defensible pattern.
- **Why JPA `Specification` for search instead of a `@Query` per filter combination?** With 6
  independent optional filters (keyword, brand, price range, fuel type, year, location), a
  derived-method or fixed-`@Query` approach would need 2⁶ method variants. `Specification`
  composes only the predicates that are actually present into one dynamic SQL query.
- **Why is a JWT filter needed if Spring Security already does auth?** Spring Security's
  default flow assumes a login form and a session. We're stateless — every request must prove
  who it is via the `Authorization` header — so `JwtAuthFilter` runs once per request, reads
  the token, and manually populates the `SecurityContext` before the rest of the chain runs.
- **Why does editing a car listing reset it to PENDING?** So a seller can't get a listing
  approved and then silently swap in different (possibly non-compliant) details — every
  content change goes back through moderation, same as a real marketplace would enforce.
- **Ownership checks live in the service layer, not just `@PreAuthorize`.** Role checks
  (`hasRole('SELLER')`) only tell you *what kind* of user is calling — they can't tell you
  whether *this* seller owns *this* car. That row-level check (`car.getSeller().getId().equals(requesterId)`)
  has to happen after the row is loaded, in `CarService`.

---

## 7. REST API reference

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register as BUYER or SELLER |
| POST | `/api/auth/login` | Public | Returns JWT + user profile |
| GET | `/api/cars` | Public | Paginated approved cars |
| GET | `/api/cars/search` | Public | Filter by keyword/brand/price/fuel/year/location |
| GET | `/api/cars/{id}` | Public | Car details |
| GET | `/api/cars/my-listings` | Seller/Admin | Your own listings, any status |
| POST | `/api/cars` | Seller/Admin | Create listing (starts PENDING) |
| PUT | `/api/cars/{id}` | Owner/Admin | Update listing (resets to PENDING) |
| DELETE | `/api/cars/{id}` | Owner/Admin | Delete listing |
| PATCH | `/api/cars/{id}/sold` | Owner/Admin | Mark as SOLD |
| POST | `/api/cars/{id}/images` | Seller/Admin | Upload an image file, returns its URL |
| GET / POST / DELETE | `/api/favorites` `/api/favorites/{carId}` | Logged-in | Manage favorites |
| POST | `/api/enquiries` | Logged-in | Send a message to a seller about a car |
| GET | `/api/enquiries/my` | Logged-in | Enquiries you sent |
| GET | `/api/enquiries/seller` | Seller/Admin | Enquiries received on your cars |
| GET | `/api/admin/cars/pending` | Admin | Listings awaiting approval |
| PATCH | `/api/admin/cars/{id}/approve` \| `/reject` | Admin | Moderate a listing |

---

## 8. Suggested next features (if you want to keep leveling it up)

- Pagination + sorting controls in the UI (price low→high, newest first)
- Email/SMS notification when an enquiry arrives
- "Compare two cars" side-by-side view
- Docker Compose file for `mysql` + backend + frontend, for one-command startup
- Deploy backend to Render/Railway and frontend to Vercel/Netlify for a live demo link on
  your resume
=======
# Used-Car-Sale-System
AutoMart – Full-Stack Used Car Marketplace built with React, Spring Boot, MySQL, JWT Authentication, and REST APIs. Features car listings, search &amp; filters, favorites, enquiries, image uploads, seller management, and admin approval workflow.
>>>>>>> 15f4836e986ddc4fdb03493293ddfde086a27438
