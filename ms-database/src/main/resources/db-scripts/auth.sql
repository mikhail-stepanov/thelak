CREATE TABLE "public"."db_user"
(
    "id"            bigint                   NOT NULL,
    "name"          varchar(255)             NOT NULL,
    "email"         varchar(255)             NOT NULL,
    "phone"         varchar(64)              NOT NULL,
    "birthday"      date                     NULL,
    "city"          varchar(64)              NULL,
    "country"       varchar(64)              NULL,
    "password"      varchar(64)              NOT NULL,
    "salt"          varchar(64)              NOT NULL,
    "is_subscribe"  boolean                  NULL,
    "subscription_date"  timestamp with time zone NULL,
    "created_date"  timestamp with time zone NULL,
    "deleted_date"  timestamp with time zone NULL,
    "modified_date" timestamp with time zone NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_user_session"
(
    "id"            bigint                   NOT NULL,
    "id_customer"   bigint                   NOT NULL,
    "token"         varchar(1024)            NULL,
    "created_date"  timestamp with time zone NULL,
    "deleted_date"  timestamp with time zone NULL,
    "modified_date" timestamp with time zone NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_notification"
(
    "content"       boolean NULL,
    "id"            bigint  NOT NULL,
    "id_user"       bigint  NOT NULL,
    "news"          boolean NULL,
    "recommendation" boolean NULL,
    "sales"         boolean NULL,
    PRIMARY KEY ("id")
);

ALTER TABLE "public"."db_notification"
    ADD FOREIGN KEY ("id_user") REFERENCES "public"."db_user" ("id");

ALTER TABLE "public"."db_user_session"
    ADD FOREIGN KEY ("id_customer") REFERENCES "public"."db_user" ("id");

CREATE SEQUENCE "public"."pk_db_user" INCREMENT 1 START 1;

CREATE SEQUENCE "public"."pk_db_user_session" INCREMENT 1 START 1;

CREATE SEQUENCE "public"."pk_db_notification" INCREMENT 1 START 1;
