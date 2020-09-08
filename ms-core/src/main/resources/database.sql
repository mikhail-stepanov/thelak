CREATE TABLE IF NOT EXISTS public.topology_table
(
    id          bigint          NOT NULL,
    id_topology bigint          NOT NULL,
    name        varchar(256)    NULL,
    content     varchar(100000) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.topology_report
(
    id          bigint          NOT NULL,
    id_topology bigint          NOT NULL,
    name        varchar(256)    NULL,
    content     varchar(100000) NOT NULL,
    path        varchar(100000) NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.topology_xml
(
    id          bigint          NOT NULL,
    id_topology bigint          NULL,
    name        varchar(256)    NULL,
    content     varchar(100000) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.topology
(
    id             bigint           NOT NULL,
    args           varchar(256)     NULL,
    description    varchar(100000)  NULL,
    injection_rate double precision NULL,
    columns        integer          NULL,
    nodes          integer          NULL,
    rows           integer          NULL,
    PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS public.pk_topology INCREMENT 1 START 1;

CREATE SEQUENCE IF NOT EXISTS public.pk_topology_report INCREMENT 1 START 1;

CREATE SEQUENCE IF NOT EXISTS public.pk_topology_table INCREMENT 1 START 1;

CREATE SEQUENCE IF NOT EXISTS public.pk_topology_xml INCREMENT 1 START 1;

ALTER TABLE public.topology_xml
    ADD FOREIGN KEY (id_topology) REFERENCES public.topology (id);

ALTER TABLE public.topology_table
    ADD FOREIGN KEY (id_topology) REFERENCES public.topology (id);

ALTER TABLE public.topology_report
    ADD FOREIGN KEY (id_topology) REFERENCES public.topology (id);

