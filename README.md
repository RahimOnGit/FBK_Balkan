# FBK Balkan - Sports Club Management System

A comprehensive Spring Boot web application for managing sports club operations including teams, coaches, players, and trial registrations.

## 🏗️ Project Overview

FBK Balkan is a full-stack web application built with **Spring Boot 4.0.0** and **Java 21** that provides a complete management system for sports clubs. The application features role-based access control, team management, coach administration, player registration, and a news publishing system.

## ✨ Features

### Core Functionality
- **Team Management**: Create, update, and manage sports teams
- **Coach Administration**: Coach profiles and management system
- **Player Registration**: Player enrollment and trial registration
- **News System**: Publish and manage club news and announcements
- **Role-Based Access**: Admin, Coach, and Public user roles
- **Trial Registration**: Online trial sign-up system

### Technical Features
- **Responsive Design**: Tailwind CSS with DaisyUI components
- **Database**: SQLite with JPA/Hibernate
- **Security**: Spring Security with custom authentication
- **Templates**: Thymeleaf with HTMX integration
- **File Upload**: Support for team logos and images

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 4.0.0
- **Java**: Version 21
- **Database**: SQLite with Hibernate
- **Security**: Spring Security
- **Build Tool**: Maven

### Frontend
- **Templates**: Thymeleaf
- **Styling**: Tailwind CSS 3.4.17
- **UI Components**: DaisyUI
- **Interactivity**: HTMX
- **Build**: PostCSS with Autoprefixer

## 📁 Project Structure

```
FBK_Balkan/
├── src/main/java/com/example/fbk_balkan/
│   ├── config/           # Configuration classes
│   ├── controller/       # MVC Controllers
│   ├── entity/          # JPA Entities
│   ├── repository/      # Data repositories
│   ├── service/         # Business logic
│   ├── dto/             # Data Transfer Objects
│   ├── mapper/          # Entity-DTO mapping
│   └── security/        # Security configuration
├── src/main/resources/
│   ├── templates/       # Thymeleaf templates
│   ├── static/          # CSS, JS, images
│   └── application.properties
├── pom.xml              # Maven configuration
└── package.json         # Frontend dependencies
```

## 🚀 Quick Start

### Prerequisites
- Java 21
- Maven 3.6+
- Node.js (for CSS processing)

### Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd FBK_Balkan
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   npm install
   ```

3. **Build CSS assets**
   ```bash
   npm run build
   # or for development with watch mode
   npm run dev
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - Main application: http://localhost:8080
   - Admin dashboard: http://localhost:8080/admin/dashboard

## 🗄️ Database Configuration

The application uses SQLite database (`fbkbalkan.db`) with the following configuration:

```properties
spring.datasource.url=jdbc:sqlite:fbkbalkan.db
spring.jpa.hibernate.ddl-auto=update
```

### Key Entities
- **Team**: Sports teams with name, description, logo
- **Coach**: Team coaches with contact information
- **Player**: Team players with personal details
- **News**: Club announcements and updates
- **TrialRegistration**: Trial session registrations

## 👥 User Roles

### Admin
- Full system access
- Manage teams, coaches, players
- Publish news
- View all registrations

### Coach
- Access to assigned teams
- View registeraion requests
- Limited administrative functions

### Public User
- View public team information
- Register for trials
- Read news and announcements

## 🎨 Frontend Development

### CSS Processing
```bash
# Development mode (watch for changes)
npm run dev

# Production build
npm run build
```

### Styling Guidelines
- Use Tailwind CSS utility classes
- DaisyUI components for consistent UI
- Responsive design with mobile-first approach

## 🔧 Configuration

### Application Properties
Key configuration options in `application.properties`:

```properties
# Server settings
server.servlet.session.timeout=30m

# File upload limits
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Security settings
# (Custom security configuration in SecurityConfig.java)
```


The project includes comprehensive test suites for:
- Controller endpoints
- Service layer logic
- Security configurations
- Data repository tests

## 📱 API Endpoints

### Public Endpoints
- `GET /` - Home page with latest news
- `GET /about` - About page
- `GET /teams` - Public team listings
- `GET /trials/register` - Trial registration form

### Admin Endpoints
- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/teams` - Team management
- `GET /admin/coaches` - Coach management
- `GET /admin/news` - News management

### Coach Endpoints
- `GET /coach/dashboard` - Coach dashboard
- `GET /coach/teams` - Coach's team management

## 📄 License

This project is licensed under the terms specified in the project configuration.

## 📞 Support

For support and questions:
- Check the project documentation
- Review the code comments
- Contact the development team
