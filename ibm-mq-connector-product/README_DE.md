# IBM MQ Connector

Der IBM-MQ-Connector verbindet Axon-Ivy-Prozesse zuverlässig mit IBM-MQ-Warteschlangen. Er unterstützt sowohl das Senden von Nachrichten nach außen als auch das Abrufen von Nachrichten aus Warteschlangen, damit du warteschlangenbasierte Workflows ohne eigene Integrationslogik aufbauen kannst.

**Wichtigste Funktionen**
- Sende IBM-MQ-Nachrichten direkt aus deinen Axon-Ivy-Prozessen heraus.
- Hole Nachrichten aus Warteschlangen ab und leite sie zurück in deine Geschäftsprozesse.
- Nutze den Connector über aufrufbare Subprozesse für einheitliche Nachrichtenverarbeitung.
- Verwende Demo-Implementierungen, um zu zeigen, wie Nachrichten vollständig versendet und verarbeitet werden.
- Integriere warteschlangenbasierte Freigaben und Ereignisverarbeitung in deine bestehenden Prozessmodelle.
- Halte die Integration einfach mit wiederverwendbarer Connector-Logik und einem schlanken Setup.

## Demo

Nutze die integrierten Demo-Module, um zu sehen, wie warteschlangenbasierte Nachrichten in eine Axon-Ivy-Anwendung passen. Die Demos zeigen, wie eine Nachricht vorbereitet, an IBM MQ gesendet und in einem Folgeprozess verarbeitet wird.

### Demo-Workflows

#### ibm-mq-connector-demo (ibm-mq-connector-demo)

##### Initial Messages
1. Starte die Demo „Initial Messages“ über das Demo-Menü.
2. Prüfe die Beispiel-Nachricht, die für den Warteschlangen-Flow geladen wird.
3. Sende die vorbereitete Nachricht über den Connector.
4. Bestätige, dass der Prozess abgeschlossen ist und die Nachricht wie erwartet zugestellt wird.

##### Loan Request Processing Demo
1. Starte die Demo „Loan Request Processing“ über das Demo-Menü.
2. Folge dem Freigabe-Flow und prüfe die Anfragedaten im Dialog.
3. Fahre durch die manuelle oder automatische Freigabe-Schritte fort.
4. Prüfe den resultierenden Task und den Status der Nachrichtenverarbeitung.

## Einrichtung

- **Rollen:** Manager (konfiguriert in config/roles.xml)
- **OpenAPI:** Keine Informationen verfügbar für diesen Abschnitt.

1. Konfiguriere die IBM-MQ-Verbindungsdetails und Warteschlangen-Namen in den Anwendungsvariablen für deine Umgebung.
2. Deploye das Connector-Modul und stelle sicher, dass die Demo-Module in deinem Axon-Ivy-Workspace verfügbar sind.
3. Starte einen der Demo-Prozesse, um zu prüfen, ob Nachrichten gesendet und abgerufen werden können.
4. Prüfe die Ergebnisse in deinem Prozessmodell und passe Warteschlangen-Namen oder Nachrichteninhalte bei Bedarf an.

### Variablen

```
@variables.yaml@
```

## Komponenten

### Aufrufbare Subprozesse

#### MessageManagement.p.json

- **Signatur**: fetch(com.axonivy.connector.model.MessageFetchRequest messageFetchRequest) -> messageFetchResult: com.axonivy.connector.model.MessageFetchResult
    - Eingabe:
        - `messageFetchRequest` (com.axonivy.connector.model.MessageFetchRequest) - Daten der Abrufanfrage
    - Ergebnis:
        - `messageFetchResult` (com.axonivy.connector.model.MessageFetchResult) - Daten des Abruf-Ergebnisses

- **Signatur**: send(com.axonivy.connector.model.MessagePushRequest messagePushRequest)
    - Eingabe:
        - `messagePushRequest` (com.axonivy.connector.model.MessagePushRequest) - Zu sendende Nachricht
    - Ergebnis: (keine)

### Dialog-Komponenten

- Für diese Market-Erweiterung stellen wir keine Dialog-Komponenten bereit.

### Rest-Clients

- Für diese Market-Erweiterung stellen wir keine Rest-Clients bereit.

### Webdienste

- Für diese Market-Erweiterung stellen wir keine Webdienste bereit.

### Maven-Artefakte

1. ibm-mq-connector

```xml
<dependency>
  <groupId>com.axonivy.connector.imb.mq</groupId>
  <artifactId>ibm-mq-connector</artifactId>
  <type>iar</type>
</dependency>
```

2. ibm-mq-connector-demo

```xml
<dependency>
  <groupId>com.axonivy.connector.imb.mq</groupId>
  <artifactId>ibm-mq-connector-demo</artifactId>
  <type>iar</type>
</dependency>
```
