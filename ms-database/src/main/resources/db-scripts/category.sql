CREATE TABLE "public"."db_category"
(
    "id"            bigint                   NOT NULL,
    "title"         varchar(255)             NOT NULL,
    "description"   varchar(4096)            NULL,
    "cover_url"     varchar(2048)            NULL,
    "created_date"  timestamp with time zone NULL,
    "deleted_date"  timestamp with time zone NULL,
    "modified_date" timestamp with time zone NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_category_videos"
(
    "id"          bigint NOT NULL,
    "id_category" bigint NOT NULL,
    "id_video"    bigint NOT NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_category_articles"
(
    "id"          bigint NOT NULL,
    "id_category" bigint NOT NULL,
    "id_article"  bigint NOT NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_category_events"
(
    "id"          bigint NOT NULL,
    "id_category" bigint NOT NULL,
    "id_event"    bigint NOT NULL,
    PRIMARY KEY ("id")
);

ALTER TABLE "public"."db_category_videos"
    ADD FOREIGN KEY ("id_category") REFERENCES "public"."db_category" ("id");

ALTER TABLE "public"."db_category_articles"
    ADD FOREIGN KEY ("id_category") REFERENCES "public"."db_category" ("id");

ALTER TABLE "public"."db_category_events"
    ADD FOREIGN KEY ("id_category") REFERENCES "public"."db_category" ("id");

CREATE SEQUENCE "public"."pk_db_category" INCREMENT 1 START 1;

CREATE SEQUENCE "public"."pk_db_category_articles" INCREMENT 1 START 1;

CREATE SEQUENCE "public"."pk_db_category_events" INCREMENT 1 START 1;

CREATE SEQUENCE "public"."pk_db_category_videos" INCREMENT 1 START 1;

