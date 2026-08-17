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

### Specifica della Classe X

<p>
operazione(esempio: Stringa): booleano<br>
Pre: Nessuna<br>
Post: ...
</p>

### Specifica della Classe Y

<p>
operazione(esempio: Stringa): booleano<br>
Pre: Nessuna<br>
Post: ...
</p>

### Specifica della Classe Z

<p>
operazione(esempio: Stringa): booleano<br>
Pre: Nessuna<br>
Post: ...
</p>

## Diagramma degli Use-Case

_inserire il diagramma qui_

## Specifica degli Use-Case

### Use-Case X

### Use-Case Y

### Use-Case Z
