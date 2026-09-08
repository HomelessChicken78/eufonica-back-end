<!-- TOC start (generated with https://github.com/derlin/bitdowntoc) -->
# Indice

- [Analisi Concettuale dei Requisiti](#analisi-concettuale-dei-requisiti)
   * [Diagramma delle Classi (Class Diagram)](#diagramma-delle-classi-class-diagram)
   * [Specifica dei tipi di dato](#specifica-dei-tipi-di-dato)
   * [Vincoli Esterni](#vincoli-esterni)
      + [[V.Artist.registrato_dopo_fondazione]](#artistregistrato_dopo_fondazione)
      + [[V.song_credit.pubblicazione_dopo_fondazione]](#song_creditpubblicazione_dopo_fondazione)
      + [[V.Album.rilascio_originale_prima_di_pubblicazione]](#albumrilascio_originale_prima_di_pubblicazione)
      + [[V.art_album.artista_fondato_prima_rilascio_album]](#art_albumartista_fondato_prima_rilascio_ufficiale_album)
      + [[V.art_album.artista_registrato_prima_pubblicazione_album]](#art_albumartista_registrato_prima_pubblicazione_album)
      + [[V.Listen.data_di_ascolto_valida]](#listendata_di_ascolto_valida)
      + [[V.Listen.non_ascolta_piu_della_durata]](#listennon_ascolta_piu_della_durata)
      + [[V.ArtistRequest.tipo_richiesta_esclusivo]](#artistrequesttipo_richiesta_esclusivo)
      + [[V.ArtistRequest.richiesta_dopo_registrazione_ut]](#artistrequestrichiesta_dopo_registrazione_ut)
      + [[V.ArtistRequest.richiesta_dopo_registrazione_art]](#artistrequestrichiesta_dopo_registrazione_art)
   * [Specifica delle Classi](#specifica-delle-classi)
      + [Specifica della classe AppUser](#specifica-della-classe-appuser)
      + [Specifica della classe Playlist](#specifica-della-classe-playlist)
      + [Specifica della classe Song](#specifica-della-classe-song)
   * [Diagramma degli Use-Case](#diagramma-degli-use-case)
   * [Specifica degli Use-Case](#specifica-degli-use-case)
      + [Strumenti di Autenticazione e Autorizzazione](#strumenti-di-autenticazione-e-autorizzazione)
      + [Strumenti di Utilizzo Playlist e Musiche](#strumenti-di-utilizzo-playlist-e-musiche)
      + [Strumenti di Interazione](#strumenti-di-interazione)
      + [Use-Case Strumenti Gestione Catalogo Musicale](#use-case-strumenti-gestione-catalogo-musicale)
      + [Use-Case Strumenti Richiesta Artista](#use-case-strumenti-richiesta-artista)
      + [Use-Case Strumenti di Moderazione](#use-case-strumenti-di-moderazione)
- [Ristrutturazione](#ristrutturazione)
   * [Diagramma delle Classi Ristrutturato](#diagramma-delle-classi-ristrutturato)
   * [Specifica dei tipi di dato (Ristrutturata)](#specifica-dei-tipi-di-dato-ristrutturata)
   * [Vincoli Ristrutturati](#vincoli-ristrutturati)
      + [[V.song_owner.IS_A_song_credit]](#song_owneris_a_song_credit)
   * [Specifica delle Classi](#specifica-delle-classi-1)
      + [Specifica della classe Song](#specifica-della-classe-song-1)
      + [Specifica della classe Playlist](#specifica-della-classe-playlist-1)
      + [Implementazione dei Vincoli](#implementazione-dei-vincoli)

<!-- TOC end -->

<!-- TOC --><a name="analisi-concettuale-dei-requisiti"></a>
# Analisi Concettuale dei Requisiti

<!-- TOC --><a name="diagramma-delle-classi-class-diagram"></a>
## Diagramma delle Classi (Class Diagram)

![Class diagram](./Class_Diagram.png)

<!-- TOC --><a name="specifica-dei-tipi-di-dato"></a>
## Specifica dei tipi di dato

- Url: Stringa che rappresenta un URL http o https secondo la sintassi RFC 3986

<!-- TOC --><a name="vincoli-esterni"></a>
## Vincoli Esterni

<!-- TOC --><a id="artistregistrato_dopo_fondazione"></a>
### [V.Artist.registrato_dopo_fondazione] Un Artista può registrarsi solo dopo la sua data di fondazione

Per ogni _a:Artist_ deve essere vero che a.foundation_date <= a.registration_timestamp

<!-- TOC --><a id="song_creditpubblicazione_dopo_fondazione"></a>
### [V.song_credit.pubblicazione_dopo_fondazione] Un Artista non può pubblicare una Canzone se non è stato fondato

Per ogni _a:Artist_ e _s:Song_, tali che _(a, s):song_credit_, deve essere vero che a.foundation_date <= s.pub_date

<!-- TOC --><a id="albumrilascio_originale_prima_di_pubblicazione"></a>
### [V.Album.rilascio_originale_prima_di_pubblicazione] Un Album può essere pubblicato sulla piattaforma solo durante o dopo il suo rilascio

Per ogni _al:Album_, deve essere vero che al.original_release_date <= al.pub_date

<!-- TOC --><a id="art_albumartista_fondato_prima_rilascio_ufficiale_album"></a>
### [V.art_album.artista_fondato_prima_rilascio_ufficiale_album] Il rilascio ufficiale di un Album deve avvenire dopo la fondazione dei suoi Artisti

Per ogni _al:Album_ e _art:Artist_, tali che _(al, art):art_album_, deve essere vero che art.foundation_date <= al.original_release_date

<!-- TOC --><a id="art_albumartista_registrato_prima_pubblicazione_album"></a>
### [V.art_album.artista_registrato_prima_pubblicazione_album] La pubblicazione di un Album può avvenire solo da Artisti registrati prima della data di pubblicazione dell'Album stesso

Per ogni _al:Album_ e _art:Artist_, tali che _(al, art):art_album_, deve essere vero che art.registration_timestamp < al.pub_date

<!-- TOC --><a id="art_albumartista_fondato_prima_pubblicazione_album"></a>
### [V.art_album.artista_fondato_prima_pubblicazione_album] Il rilascio ufficiale di un Album deve avvenire dopo la fondazione dei suoi Artisti

Per ogni _al:Album_ e _art:Artist_, tali che _(al, art):art_album_, deve essere vero che art.foundation_date < al.pub_date

<!-- TOC --><a id="listendata_di_ascolto_valida"></a>
### [V.Listen.data_di_ascolto_valida] Un Ascolto deve esser fatto dopo la registrazione di un Utente e dopo la pubblicazione di una Canzone

Per ogni _u:AppUser_, _l:Listen_ e _s:Song_, tali che _(u, l):user_listens_ e che _(s, l):song_listened_, devono essere vere entrambe le condizioni:
- s.pub_date <= l.timestamp
- u.registration_timestamp <= l.timestamp

<!-- TOC --><a id="listennon_ascolta_piu_della_durata"></a>
### [V.Listen.non_ascolta_piu_della_durata] Un Utente non può Ascoltare per una durata superiore alla durata della Canzone stessa

Per ogni _l:Listen_ e _s:Song_, tali _(s, l):song_listened_, deve essere vero che l.sec_played <= s.duration_sec

<!-- TOC --><a id="artistrequesttipo_richiesta_esclusivo"></a>
### [V.ArtistRequest.tipo_richiesta_esclusivo] Una Richiesta deve indicare il nome per un nuovo Artista oppure riferirsi a un Artista già esistente, ma non entrambe o nessuna delle due.

Per ogni _r:ArtistRequest_, deve essere vera esattamente una e una sola (**xor**) delle due seguenti condizioni:
- Esiste un valore per l'attributo r.req_name e, contemporaneamente, un valore per l'attributo r.req_foundation_date
- Esiste un _a:Artist_, tale che esista il link _(r, a):req_art_

<!-- TOC --><a id="artistrequestrichiesta_dopo_registrazione_ut"></a>
### [V.ArtistRequest.richiesta_dopo_registrazione_ut] Un Utente non può Richiedere di diventare un Artista prima della sua registrazione

Per ogni _r:ArtistRequest_ e _u:AppUser_, tali che esista il link _(r, u):send_req_ deve essere vero che u.registration_timestamp < r.timestamp

<!-- TOC --><a id="artistrequestrichiesta_dopo_registrazione_art"></a>
### [V.ArtistRequest.richiesta_dopo_registrazione_art] Una Richiesta non può riguardare un Artista che si è registrato dopo la Richiesta stessa

Per ogni _r:ArtistRequest_ e _a:Artist, tali che esista il link _(r, a):req_art_ deve essere vero che a.registration_timestamp < r.timestamp

<!-- TOC --><a name="specifica-delle-classi"></a>
## Specifica delle Classi

<!-- TOC --><a name="specifica-della-classe-appuser"></a>
### Specifica della classe AppUser

<p>
recently_listened(sinceDate : Data): Song[0..*]<br>
Pre: Deve essere vero che sinceDate < Oggi<br>
Post: L'operazione non modifica i dati. Il risultato è così definito:
</p>

- Sia _L_ l'iniseme di tutti i l:Listen tali che esiste il link (this, l):user_listens e per i quali sia vero che **sinceDate** < l.timestamp
- result è l'iniseme di tutte le s:Song, tali che esiste il link (l, s):song_listened, per almeno un oggetto l appartenente all'insieme _L_

<!-- TOC --><a name="specifica-della-classe-playlist"></a>
### Specifica della classe Playlist

<p>
tot_duration(): Intero > 0<br>
Pre: Nessuna<br>
Post: L'operazione non modifica i dati. Il risultato è così definito:
</p>

- Sia S l'insieme di tutte le _s:Song_, tali che esiste il link (this, s):playlist_contains
- result è la somma di tutte le s.duration_sec in S, espresso in ore, minuti e secondi

<p style="margin-top: 20px">
amount_likes(): Intero > 0<br>
Pre: Nessuna<br>
Post: L'operazione non modifica i dati. Il risultato è così definito:
</p>

- Sia UserLikes l'insieme di tutti gli u:AppUser, tali che esiste il link (u, this):playlist_like
- result = |UserLikes|

<!-- TOC --><a name="specifica-della-classe-song"></a>
### Specifica della classe Song

<p>
amount_listens(): Intero > 0<br>
Pre: Nessuna<br>
Post: L'operazione non modifica i dati. Il risultato è così definito:
</p>

- Sia L l'insieme di tutti i _l:Listen_, tali che esista il link (l, this):song_listened
- result = |L|

<p style="margin-top: 20px">
amount_likes(): Intero > 0<br>
Pre: Nessuna<br>
Post: L'operazione non modifica i dati. Il risultato è così definito:
</p>

- Sia UserLikes l'insieme di tutti gli u:AppUser, tali che esiste il link (u, this):song_like
- result = |UserLikes|

<!-- TOC --><a name="diagramma-degli-use-case"></a>
## Diagramma degli Use-Case

![Use-Case diagram](./Use_Case_Diagram.png)

<!-- TOC --><a name="specifica-degli-use-case"></a>
## Specifica degli Use-Case

<!-- TOC --><a name="strumenti-di-autenticazione-e-autorizzazione"></a>
### Strumenti di Autenticazione e Autorizzazione

<p>
registra(email: Stringa, disp_name: Stringa, f_name: Stringa[0..1], m_name: Stringa[0..1], l_name: Stringa[0..1], provider_name : Stringa, provider_user_id : Intero) : AppUser<br>
Pre:
</p>

- Non deve esistere alcun _u:AppUser_ tale che u.email = **email**
- Non deve esistere alcun _u:AppUser_ tale che u.display_name = **disp_name**
- **email** e **disp_name** non devono essere vuoti
- Non deve esistere alcun _am:AccessMethod_ tale che am.provider_name = **provider_name** e am.provider_user_id = **provider_user_id**.

<p>
Post:
</p>

- Viene creato un nuovo oggetto _am:AccessMethod_, con valori am.provider_name = **provider_name** e am.provider_user_id = **provider_user_id**
- Viene creato e restituito un nuovo oggetto _res:AppUser_, con valori res.email = **email**, res.display_name = **disp_name**, res.first_name = **f_name**, res.middle_name = **m_name**, res.last_name = **l_name** e res.registration_timestamp = Oggi in questo momento
- Viene creato il link _(am, res):ut_acc_

<p style="margin-top: 20px">
associa_metodo_accesso(ut: AppUser, provider_name: Stringa, provider_user_id: Intero) : AccessMethod<br>
Pre:
</p>

- Non deve esistere alcun _am:AccessMethod_ tale che am.provider_name = **provider_name** e am.provider_user_id = **provider_user_id**.

<p>
Post:
</p>

- Viene creato e restituito un nuovo oggetto _res:AccessMethod_, con valori res.provider_name = **provider_name** e res.provider_user_id = **provider_user_id**
- Viene creato il link _(res, **ut**):ut_acc_

<!-- TOC --><a name="strumenti-di-utilizzo-playlist-e-musiche"></a>
### Strumenti di Utilizzo Playlist e Musiche

<p>
crea_playlist(ut: AppUser, name: Stringa, vis: Stringa) : Playlist<br>
Pre:
</p>

- Il valore di **vis** deve appartenere all'insieme {public, private, unlisted}
- **name** non deve essere vuoto

<p>
Post:
</p>

- Viene creato e restituito un nuovo oggetto _res:Playlist_, con valori res.name = **name**, res.creation_date = Oggi e res.visibility = **vis**
- Viene creato il link _(**ut**, res):playlist_creator_

<p style="margin-top: 20px">
aggiungi_brano_a_playlist(ut: AppUser, pl: Playlist, s: Song)<br>
Pre:
</p>

- Deve esistere il link _(**ut**, **pl**):playlist_creator_
- Non deve esistere il link _(**pl**, **s**):playlist_contains_

<p>
Post:
</p>

- Viene creato il link _(**pl**, **s**):playlist_contains_

<!-- TOC --><a name="strumenti-di-interazione"></a>
### Strumenti di Interazione

<p>
registra_ascolto(ut: AppUser, s: Song, sec: Intero)<br>
Pre:
</p>

- Deve essere vero che **sec** >= 0 e **sec** <= **s**.duration_sec

<p>
Post:
</p>

- Viene creato un nuovo oggetto _l:Listen_, con valori l.timestamp = Oggi in questo momento e l.sec_played = **sec**
- Viene creato il link _(**ut**, l):user_listens_
- Viene creato il link _(**s**, l):song_listened_

<p style="margin-top: 20px">
like_canzone(ut: AppUser, s: Song)<br>
Pre:
</p>

- Non deve esistere il link _(**ut**, **s**):song_like_

<p>
Post:
</p>

- Viene creato il link _(**ut**, **s**):song_like_

<p style="margin-top: 20px">
like_playlist(ut: AppUser, pl: Playlist)<br>
Pre:
</p>

- Non deve esistere il link _(**ut**, **pl**):playlist_like_

<p>
Post:
</p>

- Viene creato il link _(**ut**, **pl**):playlist_like_

<!-- TOC --><a name="use-case-strumenti-gestione-catalogo-musicale"></a>
### Use-Case Strumenti Gestione Catalogo Musicale

<p>
pubblica_canzone(art_owner: Artist, art_credits: Artist[1..*], song_title: Stringa, duration: Intero >= 0, audio_url: Url): Song<br>
Pre:
</p>

- **art_owner** deve essere incluso nell'insieme degli artisti **art_credits**
- Per ogni _art:Artist_ in **art_credits**, deve essere vero che art.foundation_date <= Oggi
- **song_title** non deve essere vuoto

<p>
Post:
</p>

- Viene creato e restituito un nuovo oggetto _res:Song_, con valori res.title = **song_title**, res.duration_sec = **duration**, res.audio = **audio_url** e res.pub_date = Oggi
- Per ogni _art:Artist_ in **art_credits**, vengono creati i link _(art, res):song_credit_
   - Tra questi, il link _(**art_owner**, res)_ viene specializzato come istanza anche dell'associazione _song_ownership_

<p style="margin-top: 20px">
pubblica_album(artists: Artist[1..*], songs: Song[0..*], alb_name: Stringa, orig_rel_date: Data): Album<br>
Pre:
</p>

- **alb_name** non deve essere vuoto
- **orig_rel_date** <= Oggi
- Per ogni _art:Artist_ in **artists**, deve essere vero che art.foundation_date <= **orig_rel_date**

<p>
Post:
</p>

- Viene creato e restituito un nuovo oggetto _res:Album_, con valori res.name = **alb_name**, res.original_release_date = **orig_rel_date** e res.pub_date = Oggi
- Per ogni _art:Artist_ in **artists**, vengono creati i link _(art, res):art_album_
- Per ogni _s:Song_ in **songs**, vengono creati i link _(res, s):album_contains_

<!-- TOC --><a name="use-case-strumenti-richiesta-artista"></a>
### Use-Case Strumenti Richiesta Artista

<p>
invia_richiesta_nuovo_artista(ut: AppUser, req_name: Stringa, req_foundation_date : Data) : ArtistRequest<br>
Pre:
</p>

- Non deve esistere alcun _r:ArtistRequest_ tale che _(**ut**, r):send_req_ e che (r.status = 'pending' oppure r.status = 'accepted').
- **req_name** non deve essere vuoto
- Deve essere vero che **req_foundation_date** <= Oggi

<p>
Post:
</p>

- Viene creato e restituito un nuovo oggetto _res:ArtistRequest_, con valori res.status = 'pending', res.req_name = **req_name**, res.req_foundation_date = **req_foundation_date** e  res.timestamp = Oggi in questo momento
- Viene creato il link _(**ut**, res):send_req_

<p style="margin-top: 20px">
invia_richiesta_artista_esistente(ut: AppUser, art: Artist) : ArtistRequest<br>
Pre:
</p>

- Non deve esistere alcun _r:ArtistRequest_ tale che _(**ut**, r):send_req_ e che (r.status = 'pending' oppure r.status = 'accepted').

<p>
Post:
</p>

- Viene creato e restituito un nuovo oggetto _res:ArtistRequest_, con valori res.status = 'pending', res.req_name = **req_name** e res.timestamp = Oggi in questo momento
- Viene creato il link _(**ut**, res):send_req_
- Viene creato il link _(res, **art**):req_art_

<!-- TOC --><a name="use-case-strumenti-di-moderazione"></a>
### Use-Case Strumenti di Moderazione

<p>
valuta_richiesta(req: ArtistRequest, accepted: Booleano)<br>
Pre:
</p>

- Deve essere vero che **req**.status = 'pending'

<p>
Post:
</p>

- Se il valore di **accepted** è falso, allora **req**.status diventa uguale a 'rejected'
- Se il valore di **accepted** è vero, allora **req**.status diventa uguale a 'accepted'. Inoltre:
   - Sia _u:AppUser_, tale che esista il link _(u, **req**):send_req_
   - Se esiste un _art:Artist_, tale che esista il link _(**req**, art):req_art, viene creato il link _(u, art):art_affiliation_
   - Se non esiste alcun link dell'associazione req_art che coinvolga **req**
      - Viene creato un nuovo oggetto _new_art:Artist_, con valori new_art.name = **req**.req_name, new_art.foundation_date = **req**.req_foundation_date e new_art.registration_timestamp = Oggi in questo momento

<!-- TOC --><a name="ristrutturazione"></a>
# Ristrutturazione

<!-- TOC --><a name="diagramma-delle-classi-ristrutturato"></a>
## Diagramma delle Classi Ristrutturato

![Class diagram (Restructured)](./Class_Diagram_Ristrutturato.png)

<!-- TOC --><a name="specifica-dei-tipi-di-dato-ristrutturata"></a>
## Specifica dei tipi di dato (Ristrutturata)
- Tipo `IntGZ`<br>
````java
@Positive
@Column(nullable = false)
private Integer nomeAttributo;
````

- Tipo `IntGEZ`<br>
````java
@PositiveOrZero
@Column(nullable = false)
private Integer nomeAttributo;
````

- Tipo `RequestStatus`<br>
````java
public enum RequestStatus {
   PENDING,
   ACCEPTED,
   REJECTED
}
````

- Tipo `VisibilityType`<br>
````java
public enum VisibilityType {
   PUBLIC,
   PRIVATE,
   UNLISTED
}
````

- Tipo `ObjectKey`
````java
@Pattern(
    regexp = "^[a-zA-Z0-9!_.*'()/-]+$"
)
private String objectKey;
````

<!-- TOC --><a name="vincoli-ristrutturati"></a>
## Vincoli Ristrutturati

_NB: Solo i nuovi vincoli o quelli che sono cambiati rispetto all'analisi verranno riportati qui. Se un vincolo non è riportato, la sua logica non è cambiata_

<!-- TOC --><a id="song_owneris_a_song_credit"></a>
### [V.song_owner.IS_A_song_credit] Un proprietario del brano deve anche essere un artista accreditato

Per ogni _art:Artist_ e _s:Song_, tali che _(art, s):song_ownership_, deve esistere anche il link _(art, s):song_credit_

<!-- TOC --><a name="specifica-delle-classi-1"></a>
## Specifica delle Classi

_NB: Solo le nuove specifiche o quelle che sono cambiati rispetto all'analisi verranno riportati qui. Se una specifica non è riportata, la sua logica non è cambiata_

<!-- TOC --><a name="specifica-della-classe-song-1"></a>
### Specifica della classe Song

Nel passaggio all'implementazione relazionale, le operazioni di calcolo aggregato sono state sostituite da attributi a ridondanza controllata per ottimizzare le prestazioni in lettura del database:

**Song.amount_listens** e **Song.amount_likes**: Sostituiscono le operazioni correlate dell'analisi concettuale. Questi campi verranno aggiornati dinamicamente dall'applicazione ad ogni nuovo inserimento o rimozione delle entità/relazioni `Listen` e `song_like`.

<!-- TOC --><a name="specifica-della-classe-playlist-1"></a>
### Specifica della classe Playlist

Nel passaggio all'implementazione relazionale, le operazioni di calcolo aggregato sono state sostituite da attributi a ridondanza controllata per ottimizzare le prestazioni in lettura del database:

**Playlist.amount_likes**: Sostituisce l'operazione correlata dell'analisi concettuale. Questo campo verrà aggiornati dinamicamente dall'applicazione ad ogni nuovo inserimento o rimozione della relazione `playlist_like`

<!-- TOC --><a name="implementazione-dei-vincoli"></a>
### Implementazione dei Vincoli
Nel passaggio al design con Spring JPA e database relazionale, l'integrità del modello sarà garantita su più livelli:

* Vincoli di dominio: saranno controllati nell'applicazione usando le annotazioni di Bean Validation (es. @Positive, @Pattern) nelle classi @Entity.
* Vincoli strutturali: saranno controllati dal database tramite Foreign Key e vincoli UNIQUE.
* Vincoli complessi inter-classe e temporali: saranno controllati con regole più complesse, tramite la logica dei metodi dello strato @Service prima di salvare i dati nel database.
