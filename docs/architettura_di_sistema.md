# Struttura del Sistema

## Microservizi

Il sistema possiede i seguenti microservizi:

- **API Gateway**: è il punto di accesso centrale. Riceve le richieste e le invia al microservizio giusto.
- **Auth Service**: gestisce l'autenticazione e l'autorizzazione. Utilizza AWS Cognito e gestisce la JWT.
- **Catalog Command Service**: gestisce le operazioni di modifica del catalogo, come la pubblicazione di brani e la creazione di album. Si occupa anche del caricamento dei file audio su AWS S3 e invia eventi quando il catalogo viene modificato.
- **Catalog Query Service**: gestisce le richieste di lettura del catalogo. Riceve gli eventi dal Command Service e aggiorna il proprio database. Utilizza Redis per velocizzare le richieste più frequenti.
- **Engagement Service**: gestisce i dati degli utenti, come i like, gli ascolti e le playlist. Contiene anche i processi eseguiti in background, ad esempio l'invio di email automatiche.

## Comunicazione tra i servizi

I microservizi comunicano tra loro in due modi:

- **Comunicazione sincrona**: viene usato `RestClient` quando un servizio ha bisogno di ottenere dei dati subito da un altro microservizio.
- **Comunicazione asincrona** con **Publisher/Subscriber**: Usato nel pattern CQRS. Il Catalog Command Service pubblica eventi, come “Canzone Pubblicata”. Il Catalog Query Service li riceve e aggiorna il database di lettura, mantenendolo sincronizzato nel tempo (consistenza eventuale).

## Autenticazione e autorizzazione con AWS Cognito

L'identità degli utenti è gestita da **AWS Cognito**. L'**Auth Service** non si occupa di login né di registrazione delle credenziali: sono compiti di Cognito. L'Auth Service agisce da **resource server**: valida i JWT emessi da Cognito e gestisce i dati applicativi degli utenti (`AppUser`, `AccessMethod`, `ArtistRequest`).

### Email

L'email dell'utente non è salvata nel database: Cognito è l'unico punto dove è presente. Per questo motivo, anche l'univocità dell'email è garantita da Cognito. Se un servizio avesse bisogno dell'email, deve richiederla a Cognito.

### Ruoli

I ruoli sono gestiti tramite i **Cognito Groups** (`admin` e `user`). Cognito inserisce l'elenco dei gruppi dell'utente nel claim `cognito:groups` dell'access token. Non serve consultare il database per conoscere il ruolo di un utente.

Un nuovo gruppo o un cambio di gruppo compare nel token solo alla generazione del token successivo (nuovo login o refresh).

### Affiliazione utente-artista

Quando una richiesta artista viene approvata, l'Auth Service scrive l'id dell'artista nell'attributo custom Cognito `custom:artist_id`. Una Lambda Pre Token Generation copia quell'attributo nel claim `artist_id` dell'access token, così gli altri microservizi conoscono l'artista collegato all'utente leggendo il token, senza chiamate aggiuntive.

Anche questo claim compare solo alla generazione del token successivo, quindi c'è un ritardo tra l'approvazione e la sua visibilità nel token.

Il codice della Lambda è versionato nel repository (`services/auth-service/lambda/pre-token-generation`) e caricato manualmente su AWS.

## Database e migrazione

Il sistema usa **Postgres 17**, con 4 database separati (uno per ciascun microservizio: `auth`, `catalog_command`, `catalog_query`, `engagement`), tutti ospitati dalla stessa istanza Postgres ma logicamente isolati.

Nella progettazione si è cercato di rimanere il più possibile technology-agnostic rispetto al database relazionale specifico: dove possibile si preferisce **JPQL** a query native, evitando costrutti o sintassi specifiche di Postgres. Questo rende più semplice, in linea di principio, sostituire il database relazionale con un altro senza riscrivere la logica di accesso ai dati.

### Flyway

Le migrazioni dello schema sono gestite con **Flyway**, un file di migrazione versionato per ciascun database. Questo sostituisce la generazione automatica dello schema (`ddl-auto`), che in fase di sviluppo veniva impostata temporaneamente su `create` per generare la prima migrazione a partire dalle entità JPA/Hibernate, per poi tornare permanentemente su `validate`: Hibernate verifica che lo schema (creato da Flyway) corrisponda alle entità, senza mai modificarlo direttamente.

## Ambiente Docker

Tutti i servizi del sistema (microservizi di Spring, database, broker dei messaggi etc.) vengono lanciati tramite container Docker.

Un `docker-compose.yml` permette di avviare tutti i servizi.

### Avvio del sistema

Prima del primo avvio, copiare `.env.example` in `.env` nella root del progetto e compilare i valori richiesti (credenziali database, cluster ID Kafka, credenziali AWS/Cognito).

Per costruire e avviare tutto lo stack:

```bash
mvn clean install -DskipTests
docker compose up --build -d
```

Il primo comando compila tutti i moduli e tutte le librerie, incluse quelle comuni (presenti in `<root>/services/common/`), per poter produrre i rispettivi jar. Il secondo costruisce le immagini Docker a partire da quei jar e avvia all'interno lo stack (Postgres, Kafka, Redis, i 4 microservizi applicativi e il Gateway).

L'unico servizio mappato all'esterno (e dunque raggiungibile dalla macchina host) è il Gateway. Gli altri comunicano tramite rete Docker interna.
