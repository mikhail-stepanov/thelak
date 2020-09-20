CREATE TABLE "public"."db_subscription"
(
    "id"            bigint                   NOT NULL,
    "months"        integer                  NULL,
    "next"          varchar(1048)            NULL,
    "pre"           varchar(1048)            NULL,
    "price"         integer                  NOT NULL,
    "type"          varchar(1048)            NULL,
    "cover"         integer                  NULL,
    "days"          integer                  NULL,
    "created_date"  timestamp with time zone NULL,
    "deleted_date"  timestamp with time zone NULL,
    "modified_date" timestamp with time zone NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_certificate"
(
    "id"            bigint                   NOT NULL,
    "months"        integer                  NULL,
    "name"          varchar(1048)            NULL,
    "price"         integer                  NULL,
    "price_str"     varchar(1048)            NULL,
    "price_str_2"   varchar(1048)            NULL,
    "cover"         integer                  NULL,
    "description"   varchar(1048)            NULL,
    "length"        varchar(1048)            NULL,
    "created_date"  timestamp with time zone NULL,
    "deleted_date"  timestamp with time zone NULL,
    "modified_date" timestamp with time zone NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_options"
(
    "id"   bigint        NOT NULL,
    "text" varchar(1048) NOT NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_option_certificate"
(
    "id"             bigint NOT NULL,
    "id_certificate" bigint NOT NULL,
    "id_option"      bigint NOT NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_option_subscription"
(
    "id"              bigint NOT NULL,
    "id_option"       bigint NOT NULL,
    "id_subscription" bigint NOT NULL,
    PRIMARY KEY ("id")
);

ALTER TABLE "public"."db_option_certificate"
    ADD FOREIGN KEY ("id_certificate") REFERENCES "public"."db_certificate" ("id");
ALTER TABLE "public"."db_option_certificate"
    ADD FOREIGN KEY ("id_option") REFERENCES "public"."db_options" ("id");
ALTER TABLE "public"."db_option_subscription"
    ADD FOREIGN KEY ("id_option") REFERENCES "public"."db_options" ("id");
ALTER TABLE "public"."db_option_subscription"
    ADD FOREIGN KEY ("id_subscription") REFERENCES "public"."db_subscription" ("id");

CREATE SEQUENCE "public"."pk_db_certificate" INCREMENT 1 START 1;
CREATE SEQUENCE "public"."pk_db_option_certificate" INCREMENT 1 START 1;
CREATE SEQUENCE "public"."pk_db_option_subscription" INCREMENT 1 START 1;
CREATE SEQUENCE "public"."pk_db_options" INCREMENT 1 START 1;
CREATE SEQUENCE "public"."pk_db_subscription" INCREMENT 1 START 1;