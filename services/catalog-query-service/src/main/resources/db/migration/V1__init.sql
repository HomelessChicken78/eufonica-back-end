create table album (original_release_date date, version integer, pub_date timestamp(6) with time zone, id uuid not null, name varchar(255), primary key (id));
create table album_contains (album_id uuid not null, id uuid not null, song_id uuid not null, primary key (id), unique (album_id, song_id));
create table art_album (album_id uuid not null, artist_id uuid not null, id uuid not null, primary key (id), unique (album_id, artist_id));
create table artist (foundation_date date, version integer, registration_timestamp timestamp(6) with time zone, id uuid not null, name varchar(255), primary key (id));
create table processed_event (processed_at timestamp(6) with time zone not null, id uuid not null, aggregate_id varchar(255) not null, event_type varchar(255) not null, primary key (id));
create table song (amount_likes integer, amount_listens integer, duration_sec integer, published_date date, version integer, artist_owner_id uuid, id uuid not null, audio varchar(255), title varchar(255), primary key (id));
create table song_credit (artist_id uuid not null, id uuid not null, song_id uuid not null, primary key (id), unique (song_id, artist_id));
