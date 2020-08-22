CREATE TABLE "public"."db_video"
(
    "id"                  bigint                   NOT NULL,
    "title"               varchar(255)             NOT NULL,
    "description"         varchar(2048)            NULL,
    "year"                integer                  NULL,
    "country"             varchar(255)             NULL,
    "category"            varchar(255)             NOT NULL,
    "duration"            varchar(64)              NULL,
    "speaker"             varchar(64)              NULL,
    "speaker_information" varchar(2048)            NULL,
    "content_url"         varchar(2048)            NOT NULL,
    "partner_logo_url"    varchar(2048)            NULL,
    "cover_url"           varchar(2048)            NULL,
    "created_date"        timestamp with time zone NULL,
    "deleted_date"        timestamp with time zone NULL,
    "modified_date"       timestamp with time zone NULL,
    PRIMARY KEY ("id")
);

CREATE SEQUENCE "public"."pk_db_video" INCREMENT 1 START 1;

CREATE INDEX db_video_id_indx ON db_video(id);
CREATE INDEX db_video_title_indx ON db_video(title);
CREATE INDEX db_video_description_indx ON db_video(description);
CREATE INDEX db_video_speaker_indx ON db_video(speaker);