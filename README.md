# FBK Balkan 

![FBK Balkan](src/main/resources/static/images/FBK_BALKAN_logo.png) 

En modern och omfattande webbapplikation byggd med **Spring Boot** för att digitalisera och effektivisera hanteringen av FBK Balkans idrottsverksamhet. Systemet hanterar allt från publika webbsidor och provträningar till avancerad lagadministration och integrationer med externa sport-API:er.

## ✨ Huvudfunktioner

### För Besökare & Föräldrar
*   **Intresseanmälan & Provträning:** Ett automatiserat system där föräldrar kan boka provträning med inbyggd 14-dagars uppföljningslogik och GDPR-hantering.
*   **Matchschema & Resultat:** Realtidsuppdaterad matchdata hämtad direkt från **SvFF/Fogis API**.
*   **Publik Webbplats:** Responsiv design med nyheter, FAQ, information om åldersgrupper och YouTube-integration.
*   **Sponsorsida:** Visualisering av partners kategoriserade som Guld, Silver och Brons.

### För Tränare & Administratörer
*   **Rollbaserad Dashboard:** Skräddarsydda vyer för Admin, Coach och Social Admin.
*   **Nyhetshantering:** Ett arbetsflöde där tränare/social admins skapar utkast som sedan granskas och publiceras av en huvudadmin.
*   **Lag- & Spelarhantering:** Fullständig kontroll över trupper, assisterande tränare och spelarkort.
*   **Kommunikation:** Stöd för massutskick via e-post och WhatsApp-integration för snabb kontakt med föräldrar.

## 🛠 Teknisk Stack

### Backend
*   **Ramverk:** Spring Boot 4.0.0 (körs på **Java 21**).
*   **Säkerhet:** Spring Security med rollbaserad åtkomstkontroll, BCrypt för lösenordshantering och inloggningsskydd mot brute force (kontolåsning efter 5 försök).
*   **Byggverktyg:** Maven.

### Frontend
*   **Mallmotor:** Thymeleaf.
*   **Styling:** Tailwind CSS 3.4.17 tillsammans med DaisyUI för moderna UI-komponenter.
*   **Dynamik:** **HTMX** används för att skapa en snabb och interaktiv användarupplevelse utan tunga JavaScript-ramverk.

### Databas & Miljö
*   **Utveckling:** SQLite för snabb lokal iteration.
*   **Produktion:** PostgreSQL (via Supabase) driftsatt på **Render**.
*   **CI/CD:** Automatiserad distribution från GitHub till Render-miljöer.

## 👥 Användarroller
Systemet är uppdelat i fyra huvudsakliga behörighetsnivåer:
1.  **Admin:** Fullständig tillgång till systemet, inklusive användarhantering, lagadministration och publiceringsrättigheter.
2.  **Coach:** Hanterar egna lag, ser inkomna provträningar och kan skapa nyhetsutkast.
3.  **Social Admin:** Dedikerad roll för att hantera klubbens nyhetsflöde och media.
4.  **Publik användare:** Kan läsa nyheter, se matcher och registrera barn för provträning.

## 🚀 Snabbstart

### Förutsättningar
*   Java 21
*   Maven 3.6+
*   Node.js (för CSS-processering)

### Installation
1.  **Klona repot:**
    `git clone https://github.com/RahimOnGit/FBK_Balkan.git`
2.  **Konfigurera miljö:**
    Kopiera `application-example.properties` till `application.properties` och fyll i API-nycklar för SvFF och e-postinställningar.
3.  **Bygg och kör:**
    `mvn clean install`
    `mvn spring-boot:run`
4.  **Access:**
    Öppna [http://localhost:8080](http://localhost:8080) i din webbläsare.

## 🗄 Databasschema
De centrala entiteterna i systemet inkluderar:
*   **Users:** Hanterar inloggning och roller.
*   **Teams:** Information om åldersgrupper och lag.
*   **Players:** Spelarregister och kopplingar till lag.
*   **News:** Artiklar och media.
*   **TrialRegistration:** Spårar provträningsansökningar och deras status.

---
**Tack för att du tittar på projektet!** ⚽  
Ett projekt byggt med kärlek för en klubb som gör skillnad i Malmö.
