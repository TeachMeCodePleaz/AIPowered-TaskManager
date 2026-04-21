# AI-Powered Task Manager

### Eulerity Backend Developer Summer Intern Assessment

This repository contains the take-home assessment for the Eulerity Backend Engineering Intern position. It is a RESTful API built with Java 17 and Spring Boot that functions as a personal task manager. 

Beyond standard CRUD operations, this application integrates with a Large Language Model to provide an AI-powered "Breakdown" feature, which analyzes a task's title and automatically generates a list of actionable subtasks. A minimal frontend is included to easily explore and test the API endpoints.

## 🛠 Tech Stack

* **Backend:** Java 17, Spring Boot (Web, Data JPA, Validation)
* **Database:** H2 In-Memory Database
* **AI Integration:** Google Gemini 2.5 Flash API (via native Java `HttpClient`)
* **Frontend:** Vanilla JavaScript, HTML, CSS (Fetch API)
* **Build Tool:** Maven

## 🚀 How to Run

This project is designed to run "cold" with zero external dependencies other than having Java 17 installed on your machine. The database is in-memory and the frontend is served automatically by Spring Boot.

1.  Clone or unzip this repository and navigate to the project root directory:
    ```bash
    cd task-manager
    ```

2.  Run the application using the Maven Wrapper:
    ```bash
    ./mvnw spring-boot:run
    ```

3.  Once the server starts, open your web browser and navigate to:
    **http://localhost:8080**

You can interact with all CRUD endpoints and the AI Breakdown feature directly through the UI.

## ⚠️ Security Tradeoff Note

**Important context for the reviewer:** To strictly satisfy the assessment's requirement that the application must build and start with a *single command cold* (`./mvnw spring-boot:run`) with no prior setup, I have intentionally hardcoded a temporary Google Gemini API key inside the `src/main/resources/application.properties` file.

I recognize that in any production or shared environment, committing an API key to version control is a severe security anti-pattern. Under normal circumstances, this key would be injected at runtime via an environment variable (e.g., `google.api.key=${GEMINI_API_KEY}`) or a secure vault. 

This is a deliberate, contained tradeoff made solely for the convenience of the reviewer. **The hardcoded API key is temporary and will be permanently revoked shortly after the assessment review period concludes.**