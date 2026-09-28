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
- **Comunicazione asincrona**:
    - **Code Messaggi**: Servono per mettere in coda operazioni specifiche. Garantisce che il messaggio venga elaborato almeno una volta (at-least-once).
    - **Publisher/Subscriber**: Usato nel pattern CQRS. Il Catalog Command Service pubblica eventi, come “Canzone Pubblicata”. Il Catalog Query Service li riceve e aggiorna il database di lettura, mantenendolo sincronizzato nel tempo (consistenza eventuale).

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

## Ambiente Docker

Tutti i servizi del sistema (microservizi di Spring, database, broker dei messaggi etc.) vengono lanciati tramite container Docker.

Un `docker-compose.yml` permette di avviare tutti i servizi.
