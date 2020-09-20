CREATE TABLE "public"."db_smtp_template"
(
    "id"            bigint                   NOT NULL,
    "title"         varchar(1048)            NULL,
    "content"       varchar(65000)           NULL,
    "description"   varchar(4096)            NULL,
    "created_date"  timestamp with time zone NULL,
    "deleted_date"  timestamp with time zone NULL,
    "modified_date" timestamp with time zone NULL,
    PRIMARY KEY ("id")
);

CREATE SEQUENCE "public"."pk_db_smtp_template" INCREMENT 1 START 1;