# Analisi Concettuale dei Requisiti

## Diagramma delle Classi (Class Diagram)

![Class diagram](/docs/Class_Diagram.png)

## Specifica dei tipi di dato

- Url: Stringa che rappresenta un URL http o https secondo la sintassi RFC 3986

## Vincoli Esterni

### [V.Artist.registrato_dopo_fondazione] Un Artista può registrarsi solo dopo la sua data di fondazione

Per ogni _a:Artist_ deve essere vero che a.foundation_date < a.registration_timestamp

### [V.art_album.pubblicazione_dopo_fondazione] Un Artista non può pubblicare un Album se non è stato fondato

Per ogni _art:Artist_ e _alb:Album_, tali che _(art, alb):art_album_, deve essere vero che art.foundation_date < alb.pub_date

### [V.song_credit.pubblicazione_dopo_fondazione] Un Artista non può pubblicare una Canzone se non è stato fondato

Per ogni _a:Artist_ e _s:Song_, tali che _(a, s):song_credit_, deve essere vero che a.foundation_date < s.pub_date

### [V.Listen.data_di_ascolto_valida] Un Ascolto deve esser fatto dopo la registrazione di un Utente e dopo la pubblicazione di una Canzone

Per ogni _u:AppUser_, _l:Listen_ e _s:Song_, tali che _(u, l):user_listens_ e che _(s, l):song_listened_, devono essere vere entrambe le condizioni:
- s.pub_date <= l.timestamp
- u.registration_timestamp <= l.timestamp

### [V.Listen.non_ascolta_piu_della_durata] Un Utente non può Ascoltare per una durata superiore alla durata della Canzone stessa

Per ogni _l:Listen_ e _s:Song_, tali _(s, l):song_listened_, deve essere vero che l.sec_played <= s.duration_sec

## Specifica delle Classi

### Specifica della classe AppUser
<p>
recently_listened(sinceDate : Data): Song[0..*]<br>
Pre: Deve essere vero che sinceDate < Oggi<br>
Post: L'operazione non modifica i dati. Il risultato è così definito:
</p>

- Sia _L_ l'iniseme di tutti i l:Listen tali che esiste il link (this, l):user_listens e per i quali sia vero che **sinceDate** < l.timestamp
- result è l'iniseme di tutte le s:Song, tali che esiste il link (l, s):song_listened, per almeno un oggetto l appartenente all'insieme _L_

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

## Diagramma degli Use-Case

_inserire il diagramma qui_

## Specifica degli Use-Case

### Use-Case X

### Use-Case Y

### Use-Case Z
