--propietario de la base de datos

CREATE ROLE surferadmin WITH
	LOGIN
	SUPERUSER
	CREATEDB
	CREATEROLE
	INHERIT
	REPLICATION
	CONNECTION LIMIT -1
	PASSWORD 'surferpass';
	
--base de datos

CREATE DATABASE devsurferdb
    WITH 
    OWNER = surferadmin
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;
	
--estructura de la base de datos
	
CREATE TABLE "company" (
  "id" SERIAL PRIMARY KEY,
  "nombre" varchar(100) UNIQUE NOT NULL,
  "direccion" varchar(150),
  "provincia" varchar(50),
  "estado" varchar(50),
  "cpostal" varchar(8),
  "telefono" varchar(15),
  "mail" varchar(100),
  "web" varchar(200)
);

CREATE TABLE "business_unit" (
  "id" SERIAL PRIMARY KEY,
  "company_id" int NOT NULL,
  "nombre" varchar(100) UNIQUE NOT NULL,
  "direccion" varchar(150),
  "provincia" varchar(50),
  "estado" varchar(50),
  "cpostal" varchar(8),
  "telefono" varchar(15),
  "mail" varchar(100),
  "activo" boolean NOT NULL DEFAULT true
);

CREATE TABLE "user" (
  "id" SERIAL PRIMARY KEY,
  "b_unit_id" int NOT NULL,
  "user_type_id" int NOT NULL,
  "user_alias" varchar(20) UNIQUE NOT NULL,
  "nombre" varchar(50) NOT NULL,
  "apellido" varchar(50) NOT NULL,
  "user_password" varchar(50) NOT NULL,
  "activo" boolean NOT NULL DEFAULT true
);

CREATE TABLE "user_type" (
  "id" SERIAL PRIMARY KEY,
  "user_type" varchar(30) UNIQUE NOT NULL
);

CREATE TABLE "event" (
  "id" SERIAL PRIMARY KEY,
  "b_unit_id" int NOT NULL,
  "area_id" int NOT NULL,
  "event_type_id" int NOT NULL,
  "titulo" varchar(200) NOT NULL,
  "descripcion" text NOT NULL,
  "event_state_id" int NOT NULL
);

CREATE TABLE "area" (
  "id" SERIAL PRIMARY KEY,
  "area" varchar(100) UNIQUE NOT NULL,
  "descripcion" varchar(200)
);

CREATE TABLE "b_unit_area" (
  "b_unit_id" int,
  "area_id" int,
  PRIMARY KEY ("b_unit_id", "area_id")
);

CREATE TABLE "event_type" (
  "id" SERIAL PRIMARY KEY,
  "descripcion" varchar(100) UNIQUE NOT NULL
);

CREATE TABLE "event_state" (
  "id" SERIAL PRIMARY KEY,
  "descripcion" varchar(30) UNIQUE NOT NULL
);

CREATE TABLE "event_update" (
  "id" SERIAL PRIMARY KEY,
  "event_id" int NOT NULL,
  "fecha_hora" timestamp NOT NULL DEFAULT (now()),
  "descripcion" text NOT NULL,
  "autor" varchar(50),
  "user_id" int NOT NULL
);

CREATE TABLE "last_modification" (
  "table_name" varchar(30),
  "fecha_hora" timestamp NOT NULL DEFAULT (now())
);

ALTER TABLE "business_unit" ADD FOREIGN KEY ("company_id") REFERENCES "company" ("id");

ALTER TABLE "user" ADD FOREIGN KEY ("b_unit_id") REFERENCES "business_unit" ("id");

ALTER TABLE "user" ADD FOREIGN KEY ("user_type_id") REFERENCES "user_type" ("id");

ALTER TABLE "event" ADD FOREIGN KEY ("b_unit_id") REFERENCES "business_unit" ("id");

ALTER TABLE "b_unit_area" ADD FOREIGN KEY ("b_unit_id") REFERENCES "business_unit" ("id");

ALTER TABLE "b_unit_area" ADD FOREIGN KEY ("area_id") REFERENCES "area" ("id");

ALTER TABLE "event" ADD FOREIGN KEY ("event_type_id") REFERENCES "event_type" ("id");

ALTER TABLE "event" ADD FOREIGN KEY ("event_state_id") REFERENCES "event_state" ("id");

ALTER TABLE "event" ADD FOREIGN KEY ("area_id") REFERENCES "area" ("id");

ALTER TABLE "event_update" ADD FOREIGN KEY ("event_id") REFERENCES "event" ("id");

ALTER TABLE "event_update" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id");

--inserciones iniciales necesarias para el funcionamiento del programa

INSERT INTO user_type (user_type)
VALUES ('ADMIN'), ('MANAGER'), ('USER');

INSERT INTO event_state (descripcion)
VALUES ('ABIERTA'), ('EN CURSO'), ('CERRADA');

INSERT INTO company (nombre)
VALUES ('Nueva empresa');

INSERT INTO business_unit (company_id, nombre, activo)
VALUES (1, 'OFICINA CENTRAL', true);

INSERT INTO "user" (b_unit_id, user_type_id, user_alias, nombre, apellido, user_password, activo)
VALUES (1, 1, 'SURFERADMIN', 'Nombre', 'Apellido', 'surferpass', true);

INSERT INTO last_modification (table_name)
VALUES ('user_type'), ('event_type'), ('event_state'), ('company'),
('business_unit'), ('user'), ('area'), ('event'), ('event_update'),
('b_unit_area');