CREATE TABLE "public"."db_options"
(
    "id"   bigint        NOT NULL,
    "text" varchar(1048) NOT NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_certificate"
(
    "cover"         integer                  NULL,
    "created_date"  timestamp with time zone NULL,
    "deleted_date"  timestamp with time zone NULL,
    "description"   varchar(1048)            NULL,
    "id"            bigint                   NOT NULL,
    "length"        varchar(1048)            NULL,
    "modified_date" timestamp with time zone NULL,
    "months"        bigint                   NULL,
    "name"          varchar(1048)            NULL,
    "price"         integer                  NULL,
    "price_str"     varchar(1048)            NULL,
    "price_str_2"   varchar(1048)            NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_payment_config"
(
    "id"    bigint        NOT NULL,
    "name"  varchar(1048) NULL,
    "value" varchar(2056) NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_issued_certificate"
(
    "active"         boolean                  NULL,
    "active_date"    timestamp with time zone NULL,
    "created_date"   timestamp with time zone NULL,
    "deleted_date"   timestamp with time zone NULL,
    "fio"            varchar(1048)            NULL,
    "id"             bigint                   NOT NULL,
    "id_certificate" bigint                   NULL,
    "modified_date"  timestamp with time zone NULL,
    "uuid"           varchar(256)             NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_subscription"
(
    "cover"         integer                  NULL,
    "created_date"  timestamp with time zone NULL,
    "days"          integer                  NULL,
    "deleted_date"  timestamp with time zone NULL,
    "id"            bigint                   NOT NULL,
    "modified_date" timestamp with time zone NULL,
    "months"        integer                  NULL,
    "next"          varchar(1048)            NULL,
    "pre"           varchar(1048)            NULL,
    "price"         integer                  NOT NULL,
    "type"          varchar(1048)            NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_payments_recurrent"
(
    "id"                   bigint NOT NULL,
    "amount"               integer                  NOT NULL,
    "created_date"         timestamp with time zone NOT NULL,
    "currency"             varchar(32)              NOT NULL,
    "deleted_date"         timestamp with time zone NULL,
    "description"          varchar(2048)            NOT NULL,
    "email"                varchar(2048)            NOT NULL,
    "id_subscription"      bigint                   NULL,
    "id_user"              bigint                   NOT NULL,
    "interval"             varchar(256)             NOT NULL,
    "modified_date"        timestamp with time zone NULL,
    "period"               integer                  NOT NULL,
    "require_confirmation" boolean                  NOT NULL,
    "startDate"            timestamp with time zone NOT NULL,
    "status"               boolean                  NULL,
    "token"                bigint                   NOT NULL,
    "id_recurrent"         varchar(100000),
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_option_certificate"
(
    "id"             bigint NOT NULL,
    "id_certificate" bigint NOT NULL,
    "id_option"      bigint NOT NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_payments_cryptogramm"
(
    "amount"          integer                  NOT NULL,
    "card_cryptogram" varchar(2048)            NOT NULL,
    "created_date"    timestamp with time zone NOT NULL,
    "currency"        varchar(32)              NOT NULL,
    "deleted_date"    timestamp with time zone NULL,
    "description"     varchar(2048)            NOT NULL,
    "id"              bigint                   NOT NULL,
    "id_certificate"  bigint                   NULL,
    "id_subscription" bigint                   NULL,
    "id_user"         bigint                   NOT NULL,
    "modified_date"   timestamp with time zone NULL,
    "name"            varchar(1049)            NOT NULL,
    "status"          boolean                  NULL,
    "transaction_id"  bigint                   NOT NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "public"."db_option_subscription"
(
    "id"              bigint NOT NULL,
    "id_option"       bigint NOT NULL,
    "id_subscription" bigint NOT NULL,
    PRIMARY KEY ("id")
);

ALTER TABLE "public"."db_issued_certificate"
    ADD FOREIGN KEY ("id_certificate") REFERENCES "public"."db_certificate" ("id");

ALTER TABLE "public".db_payments_recurrent
    ADD FOREIGN KEY ("id_subscription") REFERENCES "public"."db_subscription" ("id");

ALTER TABLE "public"."db_option_certificate"
    ADD FOREIGN KEY ("id_certificate") REFERENCES "public"."db_certificate" ("id");

ALTER TABLE "public"."db_option_certificate"
    ADD FOREIGN KEY ("id_option") REFERENCES "public"."db_options" ("id");

ALTER TABLE "public"."db_payments_cryptogramm"
    ADD FOREIGN KEY ("id_certificate") REFERENCES "public"."db_issued_certificate" ("id");

ALTER TABLE "public"."db_payments_cryptogramm"
    ADD FOREIGN KEY ("id_subscription") REFERENCES "public"."db_subscription" ("id");

ALTER TABLE "public"."db_option_subscription"
    ADD FOREIGN KEY ("id_option") REFERENCES "public"."db_options" ("id");

ALTER TABLE "public"."db_option_subscription"
    ADD FOREIGN KEY ("id_subscription") REFERENCES "public"."db_subscription" ("id");

CREATE SEQUENCE "public"."pk_db_certificate" INCREMENT 1 START 1;
CREATE SEQUENCE "public"."pk_db_issued_certificate" INCREMENT 1 START 1;
CREATE SEQUENCE "public"."pk_db_option_certificate" INCREMENT 1 START 1;
CREATE SEQUENCE "public"."pk_db_option_subscription" INCREMENT 1 START 1;
CREATE SEQUENCE "public"."pk_db_options" INCREMENT 1 START 1;
CREATE SEQUENCE "public"."pk_db_payment_config" INCREMENT 1 START 1;
CREATE SEQUENCE "public"."pk_db_payments_cryptogramm" INCREMENT 1 START 1;
CREATE SEQUENCE "public"."pk_db_payments_recurrent" INCREMENT 1 START 1;
CREATE SEQUENCE "public"."pk_db_subscription" INCREMENT 1 START 1;

