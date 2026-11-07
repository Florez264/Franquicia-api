CREATE TABLE IF NOT EXISTS franquicia (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL
);
CREATE TABLE IF NOT EXISTS sucursal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    franquicia_id BIGINT NOT NULL,
    CONSTRAINT fk_sucursal_franquicia FOREIGN KEY (franquicia_id) REFERENCES franquicia(id),
    INDEX idx_sucursal_franquicia (franquicia_id)
);
CREATE TABLE IF NOT EXISTS producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    stock INT NOT NULL CHECK (stock >= 0),
    sucursal_id BIGINT NOT NULL,
    CONSTRAINT fk_producto_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursal(id),
    INDEX idx_producto_sucursal_stock (sucursal_id, stock, id)
);
