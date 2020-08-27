CREATE TABLE "public"."db_video"
(
    "id"                  bigint                   NOT NULL,
    "title"               varchar(255)             NOT NULL,
    "description"         varchar(2048)            NULL,
    "year"                integer                  NULL,
    "country"             varchar(255)             NULL,
    "language"            varchar(64)              NULL,
    "category"            varchar(255)             NOT NULL,
    "duration"            varchar(64)              NULL,
    "speaker"             varchar(64)              NULL,
    "speaker_information" varchar(2048)            NULL,
    "playground"          varchar(255)             NULL,
    "content_url_1080"    varchar(2048)            NULL,
    "content_url_360"     varchar(2048)            NULL,
    "content_url_480"     varchar(2048)            NULL,
    "content_url_720"     varchar(2048)            NULL,
    "partner_logo_url"    varchar(2048)            NULL,
    "cover_url"           varchar(2048)            NULL,
    "created_date"        timestamp with time zone NULL,
    "deleted_date"        timestamp with time zone NULL,
    "modified_date"       timestamp with time zone NULL,
    PRIMARY KEY ("id")
);


CREATE TABLE "public"."db_video_timecode"
(
    "id"            bigint                   NOT NULL,
    "id_user"       bigint                   NOT NULL,
    "id_video"      bigint                   NOT NULL,
    "timecode"      varchar(256)             NOT NULL,
    "modified_date" timestamp with time zone NULL,
    "created_date"  timestamp with time zone NULL,
    "deleted_date"  timestamp with time zone NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_video_history"
(
    "id"            bigint                   NOT NULL,
    "id_user"       bigint                   NOT NULL,
    "id_video"      bigint                   NOT NULL,
    "modified_date" timestamp with time zone NULL,
    "created_date"  timestamp with time zone NULL,
    "deleted_date"  timestamp with time zone NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_video_favorites"
(
    "id"            bigint                   NOT NULL,
    "id_user"       bigint                   NOT NULL,
    "id_video"      bigint                   NOT NULL,
    "modified_date" timestamp with time zone NULL,
    "created_date"  timestamp with time zone NULL,
    "deleted_date"  timestamp with time zone NULL,
    PRIMARY KEY ("id")
);

ALTER TABLE "public"."db_video_timecode"
    ADD FOREIGN KEY ("id_video") REFERENCES "public"."db_video" ("id");

ALTER TABLE "public"."db_video_history"
    ADD FOREIGN KEY ("id_video") REFERENCES "public"."db_video" ("id");

ALTER TABLE "public"."db_video_favorites"
    ADD FOREIGN KEY ("id_video") REFERENCES "public"."db_video" ("id");

CREATE SEQUENCE "public"."pk_db_video" INCREMENT 1 START 1;

CREATE SEQUENCE "public"."pk_db_video_favorites" INCREMENT 1 START 1;

CREATE SEQUENCE "public"."pk_db_video_history" INCREMENT 1 START 1;

CREATE SEQUENCE "public"."pk_db_video_timecode" INCREMENT 1 START 1;


CREATE INDEX db_video_id_indx ON db_video(id);
CREATE INDEX db_video_title_indx ON db_video(title);
CREATE INDEX db_video_description_indx ON db_video(description);
CREATE INDEX db_video_speaker_indx ON db_video(speaker);