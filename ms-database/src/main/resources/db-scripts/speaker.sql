CREATE TABLE "public"."db_speaker"
(
    "id"                bigint                   NOT NULL,
    "name"              varchar(255)             NOT NULL,
    "short_description" varchar(255)             NULL,
    "description"       text                     NULL,
    "country"           varchar(64)              NULL,
    "country_flag_code" varchar(64)              NULL,
    "created_date"      timestamp with time zone NULL,
    "deleted_date"      timestamp with time zone NULL,
    "modified_date"     timestamp with time zone NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_speaker_videos"
(
    "id"         bigint NOT NULL,
    "id_speaker" bigint NOT NULL,
    "id_video"   bigint NOT NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_speaker_events"
(
    "id"         bigint NOT NULL,
    "id_event"   bigint NOT NULL,
    "id_speaker" bigint NOT NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_speaker_articles"
(
    "id"         bigint NOT NULL,
    "id_article" bigint NOT NULL,
    "id_speaker" bigint NOT NULL,
    PRIMARY KEY ("id")
);

ALTER TABLE "public"."db_speaker_videos"
    ADD FOREIGN KEY ("id_speaker") REFERENCES "public"."db_speaker" ("id");

ALTER TABLE "public"."db_speaker_events"
    ADD FOREIGN KEY ("id_speaker") REFERENCES "public"."db_speaker" ("id");

ALTER TABLE "public"."db_speaker_articles"
    ADD FOREIGN KEY ("id_speaker") REFERENCES "public"."db_speaker" ("id");

CREATE SEQUENCE "public"."pk_db_speaker" INCREMENT 1 START 1;

CREATE SEQUENCE "public"."pk_db_speaker_articles" INCREMENT 1 START 1;

CREATE SEQUENCE "public"."pk_db_speaker_events" INCREMENT 1 START 1;

CREATE SEQUENCE "public"."pk_db_speaker_videos" INCREMENT 1 START 1;