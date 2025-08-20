# like-hero-to-zero

Dieses Projekt verwendet MySQL als Datenbank.  
Im Repository ist ein SQL-Dump enthalten, mit dem die Datenbank schnell wiederhergestellt werden kann.

Die Anwendung ist erreichbar unter:  
- **Frontend:** [http://localhost:8080](http://localhost:8080)  
- **Login:** [http://localhost:8080/login](http://localhost:8080/login)

## Demo-Zugangsdaten

**Wissenschaftler:**
| Benutzername | Passwort |
|--------------|----------|
| sci          | sci123   |

**Admin:**
| Benutzername | Passwort |
|--------------|----------|
| admin        | admin123 |

## Projekt lokal starten

### 1) Voraussetzungen
- **Java 17** (oder neuer)
- **MySQL** (lokal)
- **Maven Wrapper** ist schon dabei (`mvnw` / `mvnw.cmd`) – du brauchst kein globales Maven.

### 2) Repository klonen
git clone https://github.com/<dein-user>/<repo-name>.git
cd <repo-name>

### 3) Datenbank anlegen & Dump importieren
- **Datenbank erstellen:**
CREATE DATABASE lh2z CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
- **SQL-Dump importieren:**
mysql -u root -p lh2z < db/lh2z_dump.sql

### 4) Anwendung starten
 - **Unix/Mac**
./mvnw spring-boot:run
 - **Windows**
mvnw.cmd spring-boot:run

➡ Danach ist die App verfügbar unter:
👉 http://localhost:8080
