CREATE TABLE "public"."db_article"
(
    "id"            bigint                   NOT NULL,
    "title"         varchar(1048)            NULL,
    "author"        varchar(1048)            NULL,
    "description"   varchar(4096)            NULL,
    "content"       varchar(65000)           NULL,
    "source_url"    varchar(2048)            NULL,
    "cover_url"     varchar(2048)            NULL,
    "rating"        integer                  NULL,
    "created_date"  timestamp with time zone NULL,
    "deleted_date"  timestamp with time zone NULL,
    "modified_date" timestamp with time zone NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_article_rating"
(
    "id"            bigint                   NOT NULL,
    "id_user"       bigint                   NOT NULL,
    "id_article"    bigint                   NOT NULL,
    "score"         integer                  NOT NULL,
    "modified_date" timestamp with time zone NULL,
    "created_date"  timestamp with time zone NULL,
    "deleted_date"  timestamp with time zone NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_article_view"
(
    "id"            bigint                   NOT NULL,
    "id_article"    bigint                   NOT NULL,
    "id_user"       bigint                   NOT NULL,
    "created_date"  timestamp with time zone NULL,
    "deleted_date"  timestamp with time zone NULL,
    "midified_date" timestamp with time zone NULL,
    PRIMARY KEY ("id")
);

CREATE SEQUENCE "public"."pk_db_article" INCREMENT 1 START 1;
CREATE SEQUENCE "public"."pk_db_article_rating" INCREMENT 1 START 1;
CREATE SEQUENCE "public"."pk_db_article_view" INCREMENT 1 START 1;

ALTER TABLE "public"."db_article_view"
    ADD FOREIGN KEY ("id_article") REFERENCES "public"."db_article" ("id");

ALTER TABLE "public"."db_article_rating"
    ADD FOREIGN KEY ("id_article") REFERENCES "public"."db_article" ("id");