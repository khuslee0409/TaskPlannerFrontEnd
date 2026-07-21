# Student Planner — Desktop Client (Frontend)

A JavaFX desktop application for **Student Planner**, letting students register, log in, and manage tasks with deadlines, progress tracking, and drag-and-drop reordering — all backed by the [Student Planner API](https://github.com/khuslee0409/TaskPlanner).

This repository contains the **frontend only** (JavaFX desktop client). The backend REST API lives in a separate repo.

---

## Features

- **Landing page** → login/register entry point
- **Registration** with email verification (6-digit code)
- **Login** with JWT-based session (token held in memory for the app session)
- **Forgot password flow** — request code → verify code → set new password
- **Task table view** with:
  - Priority/order column
  - Task title
  - Progress bar per task (visual, editable)
  - Deadline column (parsed and displayed from the API's date format)
  - **Drag-and-drop row reordering**, synced back to the API
- Create, rename, update progress, and complete tasks — all calling the backend in real time

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| UI Framework | JavaFX 21.0.2 (Controls + FXML) |
| Build Tool | Maven, with `javafx-maven-plugin` (jlink packaging) |
| HTTP Client | Java's built-in `java.net.http.HttpClient` |
| JSON | Jackson Databind |
| Styling | Custom CSS (`magic.css`) |
| Packaging | jlink runtime image + Windows `.exe` installer |

---

## Prerequisites

- **JDK 17+**
- Maven (or use your IDE's built-in Maven support — no wrapper script is included in this repo)
- Network access to the backend API (see [Backend Configuration](#backend-configuration) below)

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/khuslee0409/TaskPlannerFrontEnd.git
cd TaskPlannerFrontEnd
```

### 2. Run the application

Using the JavaFX Maven plugin:

```bash
mvn clean javafx:run
```

Or build and run the packaged jlink image:

```bash
mvn clean javafx:jlink
./target/studentplanner-image/bin/StudentPlanner
```

### 3. Windows installer

A prebuilt Windows installer is included at:

```
installer/StudentPlanner-1.0.exe
```

---

## Backend Configuration

 **The API base URL is currently hardcoded** across the UI controllers (`LoginController`, `RegisterController`, `TasksController`, `ConfirmationPage`, `ChangePassword`, `NewPassword`), pointing to the deployed backend:

```
https://taskplanner-production-7e23.up.railway.app
```

To point this app at a local backend instead (e.g. `http://localhost:8080` while developing the API), update the `ApiClient(...)` base URL in each of these files:

- `src/main/java/planner/ui/LoginController.java`
- `src/main/java/planner/ui/RegisterController.java`
- `src/main/java/planner/ui/TasksController.java`
- `src/main/java/planner/ui/ConfirmationPage.java`
- `src/main/java/planner/ui/ChangePassword.java`
- `src/main/java/planner/ui/NewPassword.java`

> Consider extracting this into a single constant or config file so it only needs to be changed in one place.

---

## Project Structure

```
TaskPlannerFrontEnd/
├── installer/
│   └── StudentPlanner-1.0.exe          # Prebuilt Windows installer
├── src/main/java/
│   ├── module-info.java                # JPMS module descriptor
│   └── planner/
│       ├── App.java                    # JavaFX application entry point
│       ├── SceneNavigator.java         # Handles switching between FXML scenes
│       ├── Session.java                # Holds JWT token + username in memory
│       ├── api/
│       │   ├── ApiClient.java          # Generic HTTP client (GET/POST/PUT + JSON)
│       │   ├── AuthApi.java            # Login
│       │   ├── RegisterApi.java        # Registration
│       │   ├── VerifyApi.java          # Email verification
│       │   ├── ForgotPassword.java     # Request password reset code
│       │   ├── VerifyResetCodeApi.java # Verify reset code, get reset token
│       │   ├── ResetCode.java          # Submit new password
│       │   ├── TaskApi.java            # Task CRUD + reorder
│       │   └── dto/                    # Request/response objects (mirrors backend DTOs)
│       └── ui/
│           ├── LandingPage.java
│           ├── LoginController.java
│           ├── RegisterController.java
│           ├── ConfirmationPage.java   # Email verification screen
│           ├── ChangePassword.java     # "Forgot password" entry screen
│           ├── NewPassword.java        # Set new password screen
│           └── TasksController.java    # Main task table, drag-and-drop, progress bars
└── src/main/resources/planner/
    ├── landingPage.fxml
    ├── login.fxml
    ├── register.fxml
    ├── confirmation.fxml
    ├── changePassword.fxml
    ├── newPasswordPage.fxml
    ├── tasks.fxml
    ├── magic.css                       # App-wide styling
    ├── icon.png / icon.ico
```

---

## App Flow

1. **Landing page** → "Get Started" → Login screen
2. **New user?** → Register → email verification code sent → Confirmation screen → verify → back to Login
3. **Login** → JWT stored in `Session` (in-memory, cleared on app restart) → Tasks screen
4. **Forgot password?** → Change Password screen → request code → verify code (returns reset token) → New Password screen → set new password
5. **Tasks screen** → table of active tasks with drag-to-reorder rows, inline progress bars, and deadline display; create/rename/complete tasks call the API directly

---

## Related Repositories

| Repo | Description |
|---|---|
| [TaskPlanner (backend)](https://github.com/khuslee0409/TaskPlanner) | Spring Boot REST API — authentication, JWT, and task management that this client consumes. |

---

## Known Notes

- The session token is **held only in memory** (`Session.java`) — closing the app logs the user out; there's no "remember me" / persisted session yet.
- The backend API base URL is duplicated across six files rather than centralized (see [Backend Configuration](#backend-configuration)).
- No `.gitignore` for `/target` was seen at the root during inspection — verify build artifacts aren't being committed.

---

## License

No license specified yet.
