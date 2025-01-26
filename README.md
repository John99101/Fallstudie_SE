# Shop_n_Bake

Ein Java-basiertes Bäckerei-Management-System.

## Voraussetzungen

- Java 17 oder höher
- Maven
- MySQL Server

## Installation und Start

1. Datenbank einrichten:
```bash
# Datenbank mit root-Rechten einrichten
sudo mysql -u root < src/main/resources/database.sql
```

2. Projekt bauen:
```bash
mvn clean package
```

3. Anwendung starten:
```bash
java -cp "target/Shop_n_Bake-1.0-SNAPSHOT.jar:mysql-connector-j-9.2.0.jar" main.Main
```

## Anmeldedaten

### Kunde
- Email: test@test.com
- Passwort: test

### Mitarbeiter
- Email: admin@shop.com
- Passwort: admin

## Funktionen

- Kunden-Dashboard
- Mitarbeiterverwaltung
- Auftragsbearbeitung
- Produktkatalog
- Bestellverwaltung
