# TaskFlow - Full Stack Task Manager


TaskFlow es una aplicacion de gestion de tareas de pila completa (Full Stack) construida con **Java (Spring Boot)** en el Backend y **JavaFX** en el Frontend.

### Autor
**Arnold Cardenas** - [GitHub](https://github.com/ArnoldACG)

### Caracteristicas
- **Autenticacion Segura:** Registro e inicio de sesion con contrasenas encriptadas (BCrypt) y proteccion de rutas mediante **JWT (JSON Web Tokens)**.
- **Gestion Completa:** Crear, leer, actualizar y eliminar tareas.
- **Sistema de Estados:** Las tareas pueden estar "Pendiente", "En Proceso" o "Completada" (con bloqueo automatico al completar).
- **Prioridades:** Clasificacion de tareas por prioridad (Alta, Media, Baja) con indicadores visuales.
- **Buscador y Filtros:** Busqueda en tiempo real y filtros por estado.
- **Fechas de Auditoria:** Registro automatico de fecha de creacion y actualizacion.
- **Docker:** Configuracion lista para desplegar el Backend en un contenedor.

### Tecnologias
- **Backend:** Java 27, Spring Boot 4.1.2, Spring Security, JWT, Spring Data JPA, H2 Database.
- **Frontend:** JavaFX, CSS (tema oscuro), HttpClient.
- **Herramientas:** Maven, IntelliJ IDEA, Docker.

### Como ejecutarlo

#### Opcion 1: Localmente (Recomendado)
1. Clona el repositorio.
2. Ejecuta el Backend: `mvn spring-boot:run` (o desde IntelliJ).
3. Ejecuta el Frontend: `mvn javafx:run` (o abre el `.jar` en la carpeta `target`).

#### Opcion 2: Con Docker
El proyecto incluye un `Dockerfile` y un `docker-compose.yml` listos para usar. Si tienes Docker instalado, ejecuta:
```bash
docker compose up --build

### Primeros pasos

Al abrir la aplicacion por primera vez, debes crear una cuenta nueva usando el boton "Crear Cuenta" en la pantalla de inicio de sesion.

## English

TaskFlow is a Full Stack Task Manager application built with **Java (Spring Boot)** on the Backend and **JavaFX** on the Frontend.

### Author
**Arnold Cardenas** - [GitHub](https://github.com/ArnoldACG)

### Features
- **Secure Authentication:** Registration and login with encrypted passwords (BCrypt) and route protection using **JWT (JSON Web Tokens)**.
- **Full Management:** Create, read, update and delete tasks.
- **Status System:** Tasks can be "Pendiente" (Pending), "En Proceso" (In Progress) or "Completada" (Completed) with automatic lock upon completion.
- **Priorities:** Task classification by priority (High, Medium, Low) with visual indicators.
- **Search and Filters:** Real-time search and status filters.
- **Audit Dates:** Automatic record of creation and update dates.
- **Docker:** Ready-to-use configuration to deploy the Backend in a container.

### Technologies
- **Backend:** Java 27, Spring Boot 4.1.2, Spring Security, JWT, Spring Data JPA, H2 Database.
- **Frontend:** JavaFX, CSS (dark theme), HttpClient.
- **Tools:** Maven, IntelliJ IDEA, Docker.

### How to run it

#### Option 1: Locally (Recommended)
1. Clone the repository.
2. Run the Backend: `mvn spring-boot:run` (or from IntelliJ).
3. Run the Frontend: `mvn javafx:run` (or open the `.jar` in the `target` folder).

#### Option 2: With Docker
The project includes a ready-to-use `Dockerfile` and `docker-compose.yml`. If you have Docker installed, run:
```bash
docker compose up --build

### First steps 

When opening the application for the first time, you must create a new account using the "Crear Cuenta" (Create Account) button on the login screen.


