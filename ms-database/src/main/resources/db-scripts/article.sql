CREATE TABLE "public"."db_article"
(
    "id"            bigint                   NOT NULL,
    "title"         varchar(1048)            NULL,
    "author"        varchar(1048)            NULL,
    "description"   varchar(4096)            NULL,
    "content"       varchar(65000)           NULL,
    "source_url"    varchar(2048)            NULL,
    "created_date"  timestamp with time zone NULL,
    "deleted_date"  timestamp with time zone NULL,
    "modified_date" timestamp with time zone NULL,
    PRIMARY KEY ("id")
);

CREATE SEQUENCE "public"."pk_db_article" INCREMENT 1 START 1;