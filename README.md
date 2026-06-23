# ✅ TaskFlow — Task Management Application

A full-stack task management app built with **Java Spring Boot** (backend) and **HTML/CSS/JavaScript** (frontend), featuring JWT authentication and RESTful APIs.

---

## 🚀 Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2, Spring Security |
| Auth | JWT (JSON Web Tokens) |
| Database | H2 In-Memory (dev) |
| Frontend | HTML5, CSS3, Vanilla JavaScript |
| Testing | JUnit 5, Mockito |
| Deploy | Render (backend) + GitHub Pages (frontend) |

---

## ✨ Features

- User Registration & Login with JWT auth
- Create, view, and delete tasks
- Update task status (Pending → In Progress → Completed)
- Filter tasks by status
- Priority levels (Low / Medium / High)
- Live stats dashboard
- Fully responsive UI

---

## 🏃 Running Locally

### Prerequisites
- Java 17+
- Maven 3.8+

### Steps

```bash
# Clone the repo
git clone https://github.com/YOUR_USERNAME/taskmanager.git
cd taskmanager

# Run the backend
./mvnw spring-boot:run
```

Open `http://localhost:8080` in your browser — the frontend is served from the backend.

H2 Console available at: `http://localhost:8080/h2-console`

---

## 🧪 Running Tests

```bash
./mvnw test
```

Tests cover: AuthService (register, login, duplicate email), TaskService (CRUD, filters, error cases).

---

## ☁️ Deploying to Render

1. Push this repo to GitHub
2. Go to [render.com](https://render.com) → New → Web Service
3. Connect your GitHub repo
4. Set these settings:
   - **Build Command:** `./mvnw clean package -DskipTests`
   - **Start Command:** `java -jar target/taskmanager-0.0.1-SNAPSHOT.jar`
   - **Environment:** Java
5. Click Deploy
6. Once deployed, copy your Render URL (e.g. `https://taskmanager-xyz.onrender.com`)
7. Open `src/main/resources/static/index.html` and replace:
   ```js
   'https://YOUR-RENDER-URL.onrender.com'
   ```
   with your actual Render URL, then push again.

---

## 📁 Project Structure

```
taskmanager/
├── src/main/java/com/satyajeet/taskmanager/
│   ├── config/         # SecurityConfig, GlobalExceptionHandler
│   ├── controller/     # AuthController, TaskController
│   ├── dto/            # Request/Response DTOs
│   ├── entity/         # User, Task (with enums)
│   ├── repository/     # UserRepository, TaskRepository
│   ├── security/       # JwtUtil, JwtAuthFilter
│   └── service/        # AuthService, TaskService, UserDetailsServiceImpl
├── src/main/resources/
│   ├── static/         # Frontend (index.html)
│   └── application.properties
└── src/test/           # JUnit + Mockito tests
```

---

## 📄 API Endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | /api/auth/register | ❌ | Register user |
| POST | /api/auth/login | ❌ | Login, get JWT |
| GET | /api/tasks | ✅ | Get all tasks |
| GET | /api/tasks?status=PENDING | ✅ | Filter by status |
| POST | /api/tasks | ✅ | Create task |
| PATCH | /api/tasks/{id}/status | ✅ | Update status |
| DELETE | /api/tasks/{id} | ✅ | Delete task |

---

## 👤 Author

**Satyajeet Sharma** — [GitHub](https://github.com/YOUR_USERNAME) · [LinkedIn](https://linkedin.com/in/YOUR_PROFILE)
