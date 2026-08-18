<!-- TOC start (generated with https://github.com/derlin/bitdowntoc) -->

- [Analisi Concettuale dei Requisiti](#analisi-concettuale-dei-requisiti)
   * [Diagramma delle Classi (Class Diagram)](#diagramma-delle-classi-class-diagram)
   * [Specifica dei tipi di dato](#specifica-dei-tipi-di-dato)
   * [Vincoli Esterni](#vincoli-esterni)
      + [[V.Artist.registrato_dopo_fondazione] Un Artista può registrarsi solo dopo la sua data di fondazione](#vartistregistrato_dopo_fondazione-un-artista-può-registrarsi-solo-dopo-la-sua-data-di-fondazione)
      + [[V.art_album.pubblicazione_dopo_fondazione] Un Artista non può pubblicare un Album se non è stato fondato](#vart_albumpubblicazione_dopo_fondazione-un-artista-non-può-pubblicare-un-album-se-non-è-stato-fondato)
      + [[V.song_credit.pubblicazione_dopo_fondazione] Un Artista non può pubblicare una Canzone se non è stato fondato](#vsong_creditpubblicazione_dopo_fondazione-un-artista-non-può-pubblicare-una-canzone-se-non-è-stato-fondato)
      + [[V.Listen.data_di_ascolto_valida] Un Ascolto deve esser fatto dopo la registrazione di un Utente e dopo la pubblicazione di una Canzone](#vlistendata_di_ascolto_valida-un-ascolto-deve-esser-fatto-dopo-la-registrazione-di-un-utente-e-dopo-la-pubblicazione-di-una-canzone)
      + [[V.Listen.non_ascolta_piu_della_durata] Un Utente non può Ascoltare per una durata superiore alla durata della Canzone stessa](#vlistennon_ascolta_piu_della_durata-un-utente-non-può-ascoltare-per-una-durata-superiore-alla-durata-della-canzone-stessa)
      + [[V.ArtistRequest.tipo_richiesta_esclusivo] Una Richiesta deve indicare il nome per un nuovo Artista oppure riferirsi a un Artista già esistente, ma non entrambe o nessuna delle due.](#vartistrequesttipo_richiesta_esclusivo-una-richiesta-deve-indicare-il-nome-per-un-nuovo-artista-oppure-riferirsi-a-un-artista-già-esistente-ma-non-entrambe-o-nessuna-delle-due)
      + [[V.ArtistRequest.richiesta_dopo_registrazione_ut] Un Utente non può Richiedere di diventare un Artista prima della sua registrazione](#vartistrequestrichiesta_dopo_registrazione_ut-un-utente-non-può-richiedere-di-diventare-un-artista-prima-della-sua-registrazione)
      + [[V.ArtistRequest.richiesta_dopo_registrazione_art] Una Richiesta non può riguardare un Artista che si è registrato dopo la Richiesta stessa](#vartistrequestrichiesta_dopo_registrazione_art-una-richiesta-non-può-riguardare-un-artista-che-si-è-registrato-dopo-la-richiesta-stessa)
   * [Specifica delle Classi](#specifica-delle-classi)
      + [Specifica della classe AppUser](#specifica-della-classe-appuser)
      + [Specifica della classe Playlist](#specifica-della-classe-playlist)
      + [Specifica della classe Song](#specifica-della-classe-song)
   * [Diagramma degli Use-Case](#diagramma-degli-use-case)
   * [Specifica degli Use-Case](#specifica-degli-use-case)
      + [Use-Case X](#use-case-x)
      + [Use-Case Y](#use-case-y)
      + [Use-Case Z](#use-case-z)

<!-- TOC end -->

<!-- TOC --><a name="analisi-concettuale-dei-requisiti"></a>
# Analisi Concettuale dei Requisiti

<!-- TOC --><a name="diagramma-delle-classi-class-diagram"></a>
## Diagramma delle Classi (Class Diagram)

![Class diagram](/docs/Class_Diagram.png)

<!-- TOC --><a name="specifica-dei-tipi-di-dato"></a>
## Specifica dei tipi di dato

- Url: Stringa che rappresenta un URL http o https secondo la sintassi RFC 3986

<!-- TOC --><a name="vincoli-esterni"></a>
## Vincoli Esterni

<!-- TOC --><a name="vartistregistrato_dopo_fondazione-un-artista-può-registrarsi-solo-dopo-la-sua-data-di-fondazione"></a>
### [V.Artist.registrato_dopo_fondazione] Un Artista può registrarsi solo dopo la sua data di fondazione

Per ogni _a:Artist_ deve essere vero che a.foundation_date < a.registration_timestamp

<!-- TOC --><a name="vart_albumpubblicazione_dopo_fondazione-un-artista-non-può-pubblicare-un-album-se-non-è-stato-fondato"></a>
### [V.art_album.pubblicazione_dopo_fondazione] Un Artista non può pubblicare un Album se non è stato fondato

Per ogni _art:Artist_ e _alb:Album_, tali che _(art, alb):art_album_, deve essere vero che art.foundation_date < alb.pub_date

<!-- TOC --><a name="vsong_creditpubblicazione_dopo_fondazione-un-artista-non-può-pubblicare-una-canzone-se-non-è-stato-fondato"></a>
### [V.song_credit.pubblicazione_dopo_fondazione] Un Artista non può pubblicare una Canzone se non è stato fondato

Per ogni _a:Artist_ e _s:Song_, tali che _(a, s):song_credit_, deve essere vero che a.foundation_date < s.pub_date

<!-- TOC --><a name="vlistendata_di_ascolto_valida-un-ascolto-deve-esser-fatto-dopo-la-registrazione-di-un-utente-e-dopo-la-pubblicazione-di-una-canzone"></a>
### [V.Listen.data_di_ascolto_valida] Un Ascolto deve esser fatto dopo la registrazione di un Utente e dopo la pubblicazione di una Canzone

Per ogni _u:AppUser_, _l:Listen_ e _s:Song_, tali che _(u, l):user_listens_ e che _(s, l):song_listened_, devono essere vere entrambe le condizioni:
- s.pub_date <= l.timestamp
- u.registration_timestamp <= l.timestamp

<!-- TOC --><a name="vlistennon_ascolta_piu_della_durata-un-utente-non-può-ascoltare-per-una-durata-superiore-alla-durata-della-canzone-stessa"></a>
### [V.Listen.non_ascolta_piu_della_durata] Un Utente non può Ascoltare per una durata superiore alla durata della Canzone stessa

Per ogni _l:Listen_ e _s:Song_, tali _(s, l):song_listened_, deve essere vero che l.sec_played <= s.duration_sec

<!-- TOC --><a name="vartistrequesttipo_richiesta_esclusivo-una-richiesta-deve-indicare-il-nome-per-un-nuovo-artista-oppure-riferirsi-a-un-artista-già-esistente-ma-non-entrambe-o-nessuna-delle-due"></a>
### [V.ArtistRequest.tipo_richiesta_esclusivo] Una Richiesta deve indicare il nome per un nuovo Artista oppure riferirsi a un Artista già esistente, ma non entrambe o nessuna delle due.

Per ogni _r:ArtistRequest_, deve essere vera esattamente una e una sola (**xor**) delle due seguenti condizioni:
- Esiste un valore per l'attributo r.req_name
- Esiste un _a:Artist_, tale che esista il link _(r, a):req_art_

<!-- TOC --><a name="vartistrequestrichiesta_dopo_registrazione_ut-un-utente-non-può-richiedere-di-diventare-un-artista-prima-della-sua-registrazione"></a>
### [V.ArtistRequest.richiesta_dopo_registrazione_ut] Un Utente non può Richiedere di diventare un Artista prima della sua registrazione

Per ogni _r:ArtistRequest_ e _u:AppUser_, tali che esista il link _(r, u):send_req_ deve essere vero che u.registration_timestamp < r.timestamp

<!-- TOC --><a name="vartistrequestrichiesta_dopo_registrazione_art-una-richiesta-non-può-riguardare-un-artista-che-si-è-registrato-dopo-la-richiesta-stessa"></a>
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

![Use-Case diagram](/docs/Use_Case_Diagram.png)

<!-- TOC --><a name="specifica-degli-use-case"></a>
## Specifica degli Use-Case

<!-- TOC --><a name="use-case-x"></a>
### Use-Case X

<!-- TOC --><a name="use-case-y"></a>
### Use-Case Y

<!-- TOC --><a name="use-case-z"></a>
### Use-Case Z
