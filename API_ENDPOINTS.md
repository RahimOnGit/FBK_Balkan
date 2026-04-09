# FBK Balkan - API Endpoints Documentation

## Overview
This document contains all the API endpoints available in the FBK_Balkan Spring Boot application. The endpoints are organized by functionality and user roles.

## Authentication & Authorization
- **Admin**: Requires `ADMIN` role
- **Coach**: Requires `COACH` role  
- **Social Admin**: Requires `SOCIAL_ADMIN` role
- **Public**: No authentication required

---

## 🏠 Public Endpoints

### Home & Navigation
| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/` | Home page with latest news | Public |
| GET | `/about` | About page | Public |
| GET | `/login` | Login page | Public |
| GET | `/login-error` | Login error page | Public |

### News System
| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/news` | Public news listing | Public |
| GET | `/news/{id}` | View individual news article | Public |

### Team Management (Public)
| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/public-teams/{id}` | Public team profile page | Public |

### Trial Registration
| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/trial-registration` | Trial registration form | Public |
| POST | `/trial-registration` | Submit trial registration | Public |

---

## 👥 Admin Endpoints

### Dashboard
| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/admin/dashboard` | Admin dashboard with statistics | ADMIN |

### Coach Management
| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/admin/coaches` | List all coaches | ADMIN |
| GET | `/admin/coaches/create` | Coach creation form | ADMIN |
| GET | `/admin/coaches/edit/{id}` | Coach edit form | ADMIN |
| POST | `/admin/coaches/save` | Save/update coach | ADMIN |
| POST | `/admin/coaches/delete/{id}` | Delete coach | ADMIN |
| GET | `/admin/register-coach-form` | Coach registration form | ADMIN |
| POST | `/admin/register-coach` | Register new coach | ADMIN |
| POST | `/admin/register-coach-api` | API endpoint for coach registration | ADMIN |

### Team Management
| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/admin/teams` | List all teams | ADMIN |

### News Management
| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/admin/news` | Admin news listing | ADMIN, SOCIAL_ADMIN |
| GET | `/admin/news/create` | Create news form | ADMIN, SOCIAL_ADMIN |
| POST | `/admin/news/create` | Create news article | ADMIN, SOCIAL_ADMIN |
| GET | `/admin/news/edit/{id}` | Edit news form | ADMIN, SOCIAL_ADMIN |
| POST | `/admin/news/edit/{id}` | Update news article | ADMIN, SOCIAL_ADMIN |
| POST | `/admin/news/delete/{id}` | Delete news article | ADMIN, SOCIAL_ADMIN |
| POST | `/admin/news/toggle/{id}` | Toggle publish status | ADMIN, SOCIAL_ADMIN |
| POST | `/admin/news/delete-image/{id}` | Delete news image | ADMIN, SOCIAL_ADMIN |

---

## 🏋️ Coach Endpoints

### Dashboard
| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/coach/dashboard` | Coach dashboard with team info | COACH |

### Team Management (Coach)
| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/team-register` | Team registration form | COACH |
| POST | `/team-register` | Register new team | COACH |

---

## 🔌 REST API Endpoints

### Team API
| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/api/teams/my-teams` | Get coach's teams | COACH |
| GET | `/api/teams/coach/{coachId}` | Get teams by coach ID | ADMIN |

---

## 🔄 Controller Advice

### Global Model Attributes
| Component | Attribute | Description |
|-----------|-----------|-------------|
| NavbarTeamsProvider | `publicTeams` | Provides teams for navigation bar |

---

## 📋 Endpoint Summary by Controller

### HomeController
- `/` - Home page
- `/about` - About page

### LoginController
- `/login` - Login form
- `/login-error` - Login error page

### NewsController
- **Public**: `/news`, `/news/{id}`
- **Admin**: `/admin/news/*`

### TrialRegistrationController
- `/trial-registration` - GET/POST

### TeamController
- `/team-register` - GET/POST

### PublicTeamController
- `/public-teams/{id}` - Team profile

### AdminDashboardController
- `/admin/dashboard` - Admin dashboard

### AdminCoachController
- `/admin/coaches/*` - Coach CRUD operations

### AdminTeamController
- `/admin/teams` - Team listing

### CoachDashboardController
- `/coach/dashboard` - Coach dashboard

### TeamRestController
- `/api/teams/my-teams` - Coach's teams
- `/api/teams/coach/{coachId}` - Teams by coach

### AdminController
- `/admin/register-coach-form` - Coach registration
- `/admin/register-coach` - Register coach
- `/admin/register-coach-api` - API registration

---

## 🔐 Security Notes

- Admin endpoints require `ADMIN` role
- Coach endpoints require `COACH` role
- News management requires `ADMIN` or `SOCIAL_ADMIN`
- Public endpoints are accessible without authentication
- Role-based access is enforced via `@PreAuthorize` annotations

## 📝 Form Handling

Most endpoints use Thymeleaf templates for form rendering and submission. The application supports:
- Form validation with error handling
- File uploads (images for news)
- Flash attributes for success/error messages
- Redirect-after-POST pattern

## 🔗 Related Documentation

- [README.md](./README.md) - Project overview
- [Database Schema](./DATABASE_SCHEMA.md) - Database structure
- [Development Guide](./DEVELOPMENT.md) - Development setup