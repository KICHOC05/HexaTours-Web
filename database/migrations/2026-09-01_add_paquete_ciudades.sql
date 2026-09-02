-- Run once before deploying with JPA_DDL_AUTO=validate.
ALTER TABLE paquetes
    ADD COLUMN IF NOT EXISTS ciudades VARCHAR(300) NULL AFTER destino;
