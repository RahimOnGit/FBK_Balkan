# FBK_BALKAN_DOCUMENTATION.md

## FBK Balkan — Webbplatsdokumentation

Denna dokumentation beskriver hur FBK Balkans webbplats fungerar, vilka sidor som finns, hur systemet är uppbyggt och hur de olika rollerna används.

---

## Innehållsförteckning

1. Teknisk översikt  
2. Databas och entiteter  
3. Autentisering och säkerhet  
4. Roller  
5. Offentliga sidor  
6. Adminpanelen  
7. Coachpanelen  
8. Social Admin-panelen  
9. Profilhantering  
10. SVFF-integration (matchdata)  
11. E-posttjänster  
12. URL-översikt  

---

## 1. Teknisk översikt

| Komponent | Val |
|----------|-----|
| Backend-ramverk | Spring Boot 4.0.0 (Jakarta EE) |
| Programmeringsspråk | Java 21 |
| Databas | SQLite (via JPA / Hibernate) |
| Mallmotor | Thymeleaf 3.1 |
| CSS-ramverk | Tailwind CSS 3.4 + DaisyUI 4.12 |
| Säkerhet | Spring Security 7 |
| Lösenordskryptering | BCrypt |
| E-post | Spring Mail via Gmail SMTP |
| Port | 8080 (via PORT-miljövariabel) |

Start av applikation:

```bash
PORT=8080 java -jar target/FBK_Balkan-0.0.1-SNAPSHOT.jar
````

**Viktigt:**
Thymeleaf-mallar är inbäddade i JAR-filen. Ändringar kräver:

```bash
mvn -B -DskipTests package
```

och omstart av applikationen.

---

## 2. Databas och entiteter

SQLite används som relationsdatabas. Hibernate hanterar schemat automatiskt.

### User (Användare)

* id
* firstName / lastName
* email (unik)
* password (BCrypt)
* phone
* role: ADMIN, COACH, SOCIAL_ADMIN, ASSISTANT_COACH
* enabled
* failedLoginAttempts
* lockedUntil

Relationer:

* One-to-Many med Team (huvudtränare)
* Many-to-Many med Team (assisterande tränare)

---

### Team (Lag)

* id
* name
* ageGroup
* gender
* trainingLocation
* description
* active
* svffTeamId

Relationer: coach, assistantCoaches, players

---

### Match

* gameId
* gameNumber
* homeTeamName / awayTeamName
* homeTeamImageUrl / awayTeamImageUrl
* goalsScoredHomeTeam / goalsScoredAwayTeam
* competitionName
* seasonName
* venueName
* timeAsDateTime

---

### Player

* id
* firstName / lastName
* position
* birthDate
* active

Relation: Many-to-One med Team

---

### News

* id
* title
* content
* imageUrl
* externalImageUrl
* linkUrl
* published
* createdAt / updatedAt
* authorUsername
* authorFullName

---

### TrialRegistration

* id
* firstName / lastName
* birthDate
* gender
* relativeName / relativeEmail / relativeNumber
* preferredTrainingDate
* status: PENDING, APPROVED, REJECTED
* currentClub / clubYears
* referralSource

Relation: Many-to-One med User

---

### Sponsor

* name
* logoUrl / websiteUrl
* category
* contactName / contactEmail / contactPhone
* agreementStart / agreementEnd
* amountSek
* active

---

### FAQ

* question
* answer
* visible
* displayOrder

---

### PasswordResetToken

* token (UUID)
* expiresAt
* used

---

## 3. Autentisering och säkerhet

### Inloggning

Via `/login` med e-post och lösenord.

---

### Kontolåsning

* 5 felaktiga försök → lås i 15 minuter
* Felmeddelanden:

  * Felaktig inloggning
  * Konto låst

---

### Lösenordsåterställning

Flöde:

1. `/forgot-password`
2. E-post skickas (30 min giltighet)
3. `/reset-password?token=...`
4. Nytt lösenord sätts

Säkerhet: systemet avslöjar aldrig om e-post finns.

---

### Omdirigering

| Roll         | Route                  |
| ------------ | ---------------------- |
| ADMIN        | /admin/dashboard       |
| COACH        | /coach/dashboard       |
| SOCIAL_ADMIN | /socialadmin/dashboard |
| Övriga       | /                      |

---

## 4. Roller

### ADMIN

Full tillgång:

* Användare
* Lag
* Nyheter
* FAQ
* Sponsorer
* Statistik

---

### SOCIAL_ADMIN

* Hantera nyheter
* Uppdatera profil

---

### COACH

* Hantera provträning (egna lag)
* Skapa nyhetsutkast
* Uppdatera profil

---

### ASSISTANT_COACH

Begränsad coach-roll.

---

### Åtkomstmatris

| Funktion             | ADMIN | SOCIAL_ADMIN | COACH |
| -------------------- | ----- | ------------ | ----- |
| Användare            | ✅     | ❌            | ❌     |
| Lag                  | ✅     | ❌            | ❌     |
| Publicera nyheter    | ✅     | ✅            | ❌     |
| Nyhetsutkast         | ✅     | ✅            | ✅     |
| Provträning global   | ✅     | ❌            | ❌     |
| Provträning eget lag | ✅     | ❌            | ✅     |
| FAQ                  | ✅     | ❌            | ❌     |
| Sponsorer            | ✅     | ❌            | ❌     |
| Profil               | ✅     | ✅            | ✅     |

---

## 5. Offentliga sidor

| URL                 | Beskrivning |
| ------------------- | ----------- |
| /                   | Startsida   |
| /matcher            | Matchschema |
| /news               | Nyheter     |
| /news/{id}          | Artikel     |
| /about              | Om klubben  |
| /faq                | FAQ         |
| /kontakt            | Kontakt     |
| /sponsors           | Sponsorer   |
| /trial-registration | Anmälan     |
| /public-teams/{id}  | Lag         |
| /ungdomsportalen    | Ungdom      |
| /verksamhet         | Verksamhet  |

---

## 6. Adminpanelen

### Dashboard

* Lag
* Tränare
* Anmälningar
* Nyheter

---

### Funktioner

#### Användare

* /admin/coaches

#### Lag

* /admin/teams

#### Nyheter

* /admin/news

#### Provträning

* /admin/trials

#### FAQ

* /admin/faqs

#### Sponsorer

* /admin/sponsors

---

## 7. Coachpanelen

### Dashboard

* Lag
* Anmälningar

### Funktioner

* Godkänna / avvisa
* Mailto-länkar
* Nyhetsutkast

---

## 8. Social Admin-panelen

### Dashboard

* Statistik
* Egna artiklar

### Funktioner

* Full nyhetshantering

---

## 9. Profilhantering

| Route                    | Funktion     |
| ------------------------ | ------------ |
| /profile                 | Visa         |
| /profile/edit            | Redigera     |
| /profile/change-password | Byt lösenord |

---

## 10. SVFF-integration

* Automatisk synk via ScheduledTasks
* Hämtar matcher via svffTeamId

Matchstatus:

* null / -1 → ej spelad
* ≥ 0 → spelad

---

## 11. E-posttjänster

### Lösenordsåterställning

* Skickas via SMTP
* Fallback loggas i konsol

### Provträning

* Mailto-länkar

---

## 12. URL-översikt

### Offentliga

* /
* /matcher
* /news
* /about
* /faq
* /kontakt
* /sponsors
* /trial-registration

---

### Inloggade

* /profile
* /profile/edit
* /profile/change-password

---

### Admin

* /admin/dashboard
* /admin/coaches
* /admin/teams
* /admin/news
* /admin/faqs
* /admin/sponsors
* /admin/trials

---

### Coach

* /coach/dashboard
* /coach/news

---

### Social Admin

* /socialadmin/dashboard

---

## Dokumentversion

Senast uppdaterad: Maj 2026
