CREATE TABLE "public"."db_event"
(
    "id"            bigint                   NOT NULL,
    "title"         varchar(1048)            NULL,
    "description"   varchar(4096)            NULL,
    "date"          date                     NULL,
    "content"       varchar(65000)           NULL,
    "cover_url"     varchar(2048)            NULL,
    "created_date"  timestamp with time zone NULL,
    "deleted_date"  timestamp with time zone NULL,
    "modified_date" timestamp with time zone NULL,
    PRIMARY KEY ("id")
);

CREATE SEQUENCE "public"."pk_db_event" INCREMENT 1 START 1;