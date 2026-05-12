-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12
-- Modificado: 11-05-2026 (Refactorización v2 — 14 cambios aplicados)

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `pawtastic`
--

-- --------------------------------------------------------
-- tb_cierre_caja
-- CAMBIO #10: fecha DATE + fecha_hora TIME  →  fecha_registro DATETIME
-- --------------------------------------------------------

CREATE TABLE `tb_cierre_caja` (
  `id`             int(11)        NOT NULL,
  `id_venta`       int(11)        NOT NULL,
  `fecha_registro` datetime       NOT NULL,
  `total_tarjeta`  decimal(10,2)  DEFAULT NULL,
  `total_efectivo` decimal(10,2)  DEFAULT NULL,
  `total_general`  decimal(10,2)  NOT NULL,
  `id_State`       int(11)        NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- tb_citas
-- CAMBIO #2:  id_usuario_web / id_usuario_movil  →  DEFAULT NULL
-- CAMBIO #10: fecha + fecha_hora  →  fecha DATETIME
--             fecha_reg + fecha_hora_reg  →  fecha_reg DATETIME
-- --------------------------------------------------------

CREATE TABLE `tb_citas` (
  `id`               int(11)       NOT NULL,
  `id_mascota`       int(11)       NOT NULL,
  `id_usuario_web`   int(11)       DEFAULT NULL,
  `id_usuario_movil` int(11)       DEFAULT NULL,
  `tipo`             varchar(100)  NOT NULL,
  `motivo`           varchar(100)  DEFAULT NULL,
  `estado`           char(1)       NOT NULL,
  `fecha`            datetime      DEFAULT NULL,
  `fecha_reg`        datetime      NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- tb_clinicas  (sin cambios)
-- --------------------------------------------------------

CREATE TABLE `tb_clinicas` (
  `id`         int(11)       NOT NULL,
  `nombre`     varchar(120)  NOT NULL,
  `rfc`        varchar(20)   NOT NULL,
  `direccion`  varchar(200)  NOT NULL,
  `telefono`   varchar(10)   NOT NULL,
  `estado`     char(1)       NOT NULL,
  `created_at` datetime      NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `tb_clinicas` (`id`, `nombre`, `rfc`, `direccion`, `telefono`, `estado`, `created_at`) VALUES
(1, 'mincho', '1234567890', 'cardel', '2961108090', '1', '2026-05-08 16:03:37');

-- --------------------------------------------------------
-- tb_collar
-- CAMBIO #4:  Eliminados id_especie e id_raza (redundantes — violación 3FN)
-- CAMBIO #7:  codigo_nfc  varchar(10) → varchar(50)  +  UNIQUE
--             Agregados estado y created_at
-- --------------------------------------------------------

CREATE TABLE `tb_collar` (
  `id`          int(11)      NOT NULL,
  `id_mascota`  int(11)      NOT NULL,
  `codigo_nfc`  varchar(50)  NOT NULL,
  `nombre`      varchar(50)  NOT NULL,
  `estado`      char(1)      NOT NULL DEFAULT '1',
  `created_at`  datetime     NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- tb_detalle_entrada  (sin cambios)
-- --------------------------------------------------------

CREATE TABLE `tb_detalle_entrada` (
  `id`              int(11)       NOT NULL,
  `id_entrada`      int(11)       NOT NULL,
  `costo_unitario`  decimal(10,2) NOT NULL,
  `id_producto`     int(11)       NOT NULL,
  `cantidad`        int(11)       NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- tb_detalle_venta
-- CAMBIO #1: id_perdida  NOT NULL  →  DEFAULT NULL
-- --------------------------------------------------------

CREATE TABLE `tb_detalle_venta` (
  `id`              int(11)       NOT NULL,
  `id_perdida`      int(11)       DEFAULT NULL,
  `id_venta`        int(11)       NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `subtotal`        decimal(10,2) NOT NULL,
  `id_producto`     int(11)       NOT NULL,
  `cantidad`        int(11)       NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- tb_diagnosticos
-- CAMBIO #11: Eliminado campo `medicamentos`; tratamiento se mantiene (texto libre para notas)
-- CAMBIO #10: fecha DATE + fecha_hora TIME  →  fecha_registro DATETIME
-- --------------------------------------------------------

CREATE TABLE `tb_diagnosticos` (
  `id`              int(11)       NOT NULL,
  `id_expediente`   int(11)       NOT NULL,
  `id_cita`         int(11)       NOT NULL,
  `descripcion`     varchar(300)  NOT NULL,
  `tratamiento`     varchar(500)  DEFAULT NULL,
  `fecha_registro`  datetime      NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- tb_diagnostico_medicamento  (TABLA NUEVA)
-- CAMBIO #11: Medicamentos por diagnóstico con referencia al inventario
-- --------------------------------------------------------

CREATE TABLE `tb_diagnostico_medicamento` (
  `id`              int(11)              NOT NULL,
  `id_diagnostico`  int(11)              NOT NULL,
  `id_producto`     int(11)              NOT NULL,
  `dosis`           varchar(100)         NOT NULL,
  `frecuencia`      varchar(100)         DEFAULT NULL,
  `duracion_dias`   tinyint(3) UNSIGNED  DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- tb_entrada  (sin cambios)
-- --------------------------------------------------------

CREATE TABLE `tb_entrada` (
  `id`       int(11)       NOT NULL,
  `fecha`    datetime      NOT NULL,
  `total`    decimal(10,2) NOT NULL,
  `id_State` int(11)       NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- tb_especie  (sin cambios)
-- --------------------------------------------------------

CREATE TABLE `tb_especie` (
  `id`          int(11)      NOT NULL,
  `especie`     varchar(50)  NOT NULL,
  `descripcion` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- tb_expediente
-- CAMBIO #13: historial  varchar(300) → text
-- CAMBIO #10: fecha_apertura DATE + fecha_apertura_hora TIME  →  fecha_apertura DATETIME
-- --------------------------------------------------------

CREATE TABLE `tb_expediente` (
  `id`             int(11)  NOT NULL,
  `id_mascota`     int(11)  NOT NULL,
  `historial`      text     NOT NULL,
  `fecha_apertura` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- tb_mascotas
-- CAMBIO #6: edad varchar(50)  →  fecha_nacimiento DATE
-- --------------------------------------------------------

CREATE TABLE `tb_mascotas` (
  `id`               int(11)      NOT NULL,
  `id_propietario`   int(11)      NOT NULL,
  `nombre`           varchar(100) NOT NULL,
  `id_especie`       int(11)      NOT NULL,
  `id_raza`          int(11)      NOT NULL,
  `fecha_nacimiento` date         DEFAULT NULL,
  `estado`           char(1)      NOT NULL,
  `created_at`       datetime     NOT NULL,
  `descripcion`      varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- tb_perdida  (sin cambios)
-- --------------------------------------------------------

CREATE TABLE `tb_perdida` (
  `id`       int(11)       NOT NULL,
  `fecha`    datetime      NOT NULL,
  `id_State` int(11)       NOT NULL,
  `total`    decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- tb_producto
-- CAMBIO #5:  Agregados categoria, descripcion, stock_minimo, unidad_medida, fecha_caducidad
-- CAMBIO #14: codigo  int(11)  →  varchar(30)  con UNIQUE
-- --------------------------------------------------------

CREATE TABLE `tb_producto` (
  `id`              int(11)       NOT NULL,
  `id_clinica`      int(11)       NOT NULL DEFAULT 1,
  `nombre`          varchar(120)  NOT NULL,
  `categoria`       varchar(100)  DEFAULT NULL,
  `descripcion`     varchar(300)  DEFAULT NULL,
  `codigo`          varchar(30)   NOT NULL,
  `precio`          decimal(10,2) NOT NULL,
  `costo_unitario`  decimal(10,2) NOT NULL,
  `existencia`      int(11)       NOT NULL,
  `stock_minimo`    int(11)       NOT NULL DEFAULT 0,
  `unidad_medida`   varchar(50)   DEFAULT NULL,
  `fecha_caducidad` date          DEFAULT NULL,
  `fecha`           date          NOT NULL,
  `id_State`        int(11)       NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- tb_propietarios
-- CAMBIO #2: id_usuario_web / id_usuario_movil  →  DEFAULT NULL
-- CAMBIO #3: nombre, apellidos, telefono  →  DEFAULT NULL
--            (nullable: se usan cuando el propietario no tiene cuenta de usuario)
-- --------------------------------------------------------

CREATE TABLE `tb_propietarios` (
  `id`               int(11)      NOT NULL,
  `id_usuario_web`   int(11)      DEFAULT NULL,
  `id_usuario_movil` int(11)      DEFAULT NULL,
  `nombre`           varchar(100) DEFAULT NULL,
  `apellidos`        varchar(100) DEFAULT NULL,
  `telefono`         varchar(10)  DEFAULT NULL,
  `direccion`        varchar(100) NOT NULL,
  `estado`           char(1)      NOT NULL,
  `created_at`       datetime     NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- tb_raza  (sin cambios)
-- --------------------------------------------------------

CREATE TABLE `tb_raza` (
  `id`          int(11)      NOT NULL,
  `nombre`      varchar(50)  NOT NULL,
  `descripcion` varchar(200) NOT NULL,
  `id_especie`  int(11)      NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- tb_state
-- CAMBIO #9: Agregado campo `tipo` para distinguir estados por entidad
-- --------------------------------------------------------

CREATE TABLE `tb_state` (
  `id`     int(11)      NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `tipo`   varchar(50)  NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- tb_usuario_movil
-- CAMBIO #8: contrasenia varchar(10)  →  varchar(255)  (para almacenar hashes)
-- --------------------------------------------------------

CREATE TABLE `tb_usuario_movil` (
  `id`          int(11)      NOT NULL,
  `nombre`      varchar(100) NOT NULL,
  `apellidos`   varchar(100) DEFAULT NULL,
  `telefono`    varchar(10)  NOT NULL,
  `email`       varchar(100) NOT NULL,
  `contrasenia` varchar(255) NOT NULL,
  `created_at`  datetime     NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- tb_usuario_web
-- CAMBIO #8:  contrasenia varchar(10)  →  varchar(255)
-- CAMBIO #12: tipo_rol varchar(50)  →  enum
-- --------------------------------------------------------

CREATE TABLE `tb_usuario_web` (
  `id`           int(11)      NOT NULL,
  `id_clinica`   int(11)      NOT NULL,
  `usuario`      varchar(10)  NOT NULL,
  `nombre`       varchar(100) NOT NULL,
  `apellidos`    varchar(100) DEFAULT NULL,
  `tipo_rol`     enum('administrador','veterinario','recepcionista','asistente') NOT NULL,
  `especialidad` varchar(100) DEFAULT NULL,
  `cedula`       varchar(100) DEFAULT NULL,
  `telefono`     varchar(10)  NOT NULL,
  `email`        varchar(100) NOT NULL,
  `contrasenia`  varchar(255) NOT NULL,
  `created_at`   datetime     NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `tb_usuario_web` (`id`, `id_clinica`, `usuario`, `nombre`, `apellidos`, `tipo_rol`, `especialidad`, `cedula`, `telefono`, `email`, `contrasenia`, `created_at`) VALUES
(1, 1, 'JUCA', 'juan carlos', 'aguilar camacho', 'veterinario', 'general', NULL, '2961497729', 'juarlos.0630@gmail.com', '12345', '2026-05-08 16:22:36');

-- --------------------------------------------------------
-- tb_venta
-- CAMBIO #2:  id_usuario_web / id_usuario_movil  →  DEFAULT NULL
-- CAMBIO #10: fecha + fecha_hora  →  fecha DATETIME
--             fecha_reg + fecha_hora_reg  →  fecha_reg DATETIME
-- --------------------------------------------------------

CREATE TABLE `tb_venta` (
  `id`               int(11)       NOT NULL,
  `id_cita`          int(11)       NOT NULL,
  `id_usuario_web`   int(11)       DEFAULT NULL,
  `id_usuario_movil` int(11)       DEFAULT NULL,
  `metodo_pago`      char(1)       NOT NULL,
  `total`            decimal(10,2) NOT NULL,
  `fecha`            datetime      NOT NULL,
  `fecha_reg`        datetime      NOT NULL,
  `id_State`         int(11)       NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ========================================================
-- ÍNDICES
-- ========================================================

ALTER TABLE `tb_cierre_caja`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_venta` (`id_venta`),
  ADD KEY `id_State` (`id_State`);

ALTER TABLE `tb_citas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_mascota` (`id_mascota`),
  ADD KEY `id_usuario_web` (`id_usuario_web`),
  ADD KEY `id_usuario_movil` (`id_usuario_movil`);

ALTER TABLE `tb_clinicas`
  ADD PRIMARY KEY (`id`);

-- CAMBIO #4/#7: UNIQUE en codigo_nfc; índice solo en id_mascota
ALTER TABLE `tb_collar`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_codigo_nfc` (`codigo_nfc`),
  ADD KEY `id_mascota` (`id_mascota`);

ALTER TABLE `tb_detalle_entrada`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_entrada` (`id_entrada`),
  ADD KEY `id_producto` (`id_producto`);

ALTER TABLE `tb_detalle_venta`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_perdida` (`id_perdida`),
  ADD KEY `id_venta` (`id_venta`),
  ADD KEY `id_producto` (`id_producto`);

ALTER TABLE `tb_diagnosticos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_expediente` (`id_expediente`),
  ADD KEY `id_cita` (`id_cita`);

-- CAMBIO #11: Índices nueva tabla
ALTER TABLE `tb_diagnostico_medicamento`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_diagnostico` (`id_diagnostico`),
  ADD KEY `id_producto` (`id_producto`);

ALTER TABLE `tb_entrada`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_State` (`id_State`);

ALTER TABLE `tb_especie`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `tb_expediente`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_mascota` (`id_mascota`);

ALTER TABLE `tb_mascotas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_propietario` (`id_propietario`),
  ADD KEY `id_especie` (`id_especie`),
  ADD KEY `id_raza` (`id_raza`);

ALTER TABLE `tb_perdida`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_State` (`id_State`);

-- CAMBIO #14: UNIQUE en codigo
ALTER TABLE `tb_producto`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_codigo` (`codigo`),
  ADD KEY `id_State` (`id_State`),
  ADD KEY `id_clinica` (`id_clinica`);

ALTER TABLE `tb_propietarios`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_usuario_web` (`id_usuario_web`),
  ADD KEY `id_usuario_movil` (`id_usuario_movil`);

ALTER TABLE `tb_raza`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_especie` (`id_especie`);

ALTER TABLE `tb_state`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `tb_usuario_movil`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `tb_usuario_web`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_clinica` (`id_clinica`);

ALTER TABLE `tb_venta`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_cita` (`id_cita`),
  ADD KEY `id_usuario_web` (`id_usuario_web`),
  ADD KEY `id_usuario_movil` (`id_usuario_movil`),
  ADD KEY `id_State` (`id_State`);

-- ========================================================
-- AUTO_INCREMENT
-- ========================================================

ALTER TABLE `tb_cierre_caja`              MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `tb_citas`                    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `tb_clinicas`                 MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
ALTER TABLE `tb_collar`                   MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `tb_detalle_entrada`          MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `tb_detalle_venta`            MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `tb_diagnosticos`             MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `tb_diagnostico_medicamento`  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `tb_entrada`                  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `tb_especie`                  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `tb_expediente`               MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `tb_mascotas`                 MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `tb_perdida`                  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `tb_producto`                 MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `tb_propietarios`             MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `tb_raza`                     MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `tb_state`                    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `tb_usuario_movil`            MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `tb_usuario_web`              MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
ALTER TABLE `tb_venta`                    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

-- ========================================================
-- FOREIGN KEYS
-- ========================================================

ALTER TABLE `tb_cierre_caja`
  ADD CONSTRAINT `tb_cierre_caja_ibfk_1` FOREIGN KEY (`id_venta`) REFERENCES `tb_venta` (`id`),
  ADD CONSTRAINT `tb_cierre_caja_ibfk_2` FOREIGN KEY (`id_State`) REFERENCES `tb_state` (`id`);

ALTER TABLE `tb_citas`
  ADD CONSTRAINT `tb_citas_ibfk_1` FOREIGN KEY (`id_mascota`)       REFERENCES `tb_mascotas`      (`id`),
  ADD CONSTRAINT `tb_citas_ibfk_2` FOREIGN KEY (`id_usuario_web`)   REFERENCES `tb_usuario_web`   (`id`),
  ADD CONSTRAINT `tb_citas_ibfk_3` FOREIGN KEY (`id_usuario_movil`) REFERENCES `tb_usuario_movil` (`id`);

-- CAMBIO #4: FK solo a tb_mascotas; eliminadas FKs de especie y raza
ALTER TABLE `tb_collar`
  ADD CONSTRAINT `tb_collar_ibfk_1` FOREIGN KEY (`id_mascota`) REFERENCES `tb_mascotas` (`id`);

ALTER TABLE `tb_detalle_entrada`
  ADD CONSTRAINT `tb_detalle_entrada_ibfk_1` FOREIGN KEY (`id_entrada`)  REFERENCES `tb_entrada`  (`id`),
  ADD CONSTRAINT `tb_detalle_entrada_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `tb_producto` (`id`);

ALTER TABLE `tb_detalle_venta`
  ADD CONSTRAINT `tb_detalle_venta_ibfk_1` FOREIGN KEY (`id_perdida`)  REFERENCES `tb_perdida`  (`id`),
  ADD CONSTRAINT `tb_detalle_venta_ibfk_2` FOREIGN KEY (`id_venta`)    REFERENCES `tb_venta`    (`id`),
  ADD CONSTRAINT `tb_detalle_venta_ibfk_3` FOREIGN KEY (`id_producto`) REFERENCES `tb_producto` (`id`);

ALTER TABLE `tb_diagnosticos`
  ADD CONSTRAINT `tb_diagnosticos_ibfk_1` FOREIGN KEY (`id_expediente`) REFERENCES `tb_expediente` (`id`),
  ADD CONSTRAINT `tb_diagnosticos_ibfk_2` FOREIGN KEY (`id_cita`)       REFERENCES `tb_citas`      (`id`);

-- CAMBIO #11: FKs para nueva tabla
ALTER TABLE `tb_diagnostico_medicamento`
  ADD CONSTRAINT `tb_diag_med_ibfk_1` FOREIGN KEY (`id_diagnostico`) REFERENCES `tb_diagnosticos` (`id`),
  ADD CONSTRAINT `tb_diag_med_ibfk_2` FOREIGN KEY (`id_producto`)    REFERENCES `tb_producto`     (`id`);

ALTER TABLE `tb_entrada`
  ADD CONSTRAINT `tb_entrada_ibfk_1` FOREIGN KEY (`id_State`) REFERENCES `tb_state` (`id`);

ALTER TABLE `tb_expediente`
  ADD CONSTRAINT `tb_expediente_ibfk_1` FOREIGN KEY (`id_mascota`) REFERENCES `tb_mascotas` (`id`);

ALTER TABLE `tb_mascotas`
  ADD CONSTRAINT `tb_mascotas_ibfk_1` FOREIGN KEY (`id_propietario`) REFERENCES `tb_propietarios` (`id`),
  ADD CONSTRAINT `tb_mascotas_ibfk_2` FOREIGN KEY (`id_especie`)     REFERENCES `tb_especie`      (`id`),
  ADD CONSTRAINT `tb_mascotas_ibfk_3` FOREIGN KEY (`id_raza`)        REFERENCES `tb_raza`         (`id`);

ALTER TABLE `tb_perdida`
  ADD CONSTRAINT `tb_perdida_ibfk_1` FOREIGN KEY (`id_State`) REFERENCES `tb_state` (`id`);

ALTER TABLE `tb_producto`
  ADD CONSTRAINT `tb_producto_ibfk_1` FOREIGN KEY (`id_State`) REFERENCES `tb_state` (`id`),
  ADD CONSTRAINT `tb_producto_ibfk_2` FOREIGN KEY (`id_clinica`) REFERENCES `tb_clinicas` (`id`);

ALTER TABLE `tb_propietarios`
  ADD CONSTRAINT `tb_propietarios_ibfk_1` FOREIGN KEY (`id_usuario_web`)   REFERENCES `tb_usuario_web`   (`id`),
  ADD CONSTRAINT `tb_propietarios_ibfk_2` FOREIGN KEY (`id_usuario_movil`) REFERENCES `tb_usuario_movil` (`id`);

ALTER TABLE `tb_raza`
  ADD CONSTRAINT `tb_raza_ibfk_1` FOREIGN KEY (`id_especie`) REFERENCES `tb_especie` (`id`);

ALTER TABLE `tb_usuario_web`
  ADD CONSTRAINT `tb_usuario_web_ibfk_1` FOREIGN KEY (`id_clinica`) REFERENCES `tb_clinicas` (`id`);

ALTER TABLE `tb_venta`
  ADD CONSTRAINT `tb_venta_ibfk_1` FOREIGN KEY (`id_cita`)          REFERENCES `tb_citas`        (`id`),
  ADD CONSTRAINT `tb_venta_ibfk_2` FOREIGN KEY (`id_usuario_web`)   REFERENCES `tb_usuario_web`  (`id`),
  ADD CONSTRAINT `tb_venta_ibfk_3` FOREIGN KEY (`id_usuario_movil`) REFERENCES `tb_usuario_movil`(`id`),
  ADD CONSTRAINT `tb_venta_ibfk_4` FOREIGN KEY (`id_State`)         REFERENCES `tb_state`        (`id`);

-- ========================================================
-- CAMPO ADICIONAL: nota en tb_venta (POS)
-- ========================================================
ALTER TABLE `tb_venta` ADD COLUMN `nota` varchar(300) DEFAULT NULL;

-- ========================================================
-- TABLA: tb_alerta_stock (generada automáticamente por el POS)
-- ========================================================

CREATE TABLE `tb_alerta_stock` (
  `id`           int(11) NOT NULL AUTO_INCREMENT,
  `id_producto`  int(11) NOT NULL,
  `existencia`   int(11) NOT NULL,
  `stock_minimo` int(11) NOT NULL,
  `fecha`        datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `resuelta`     tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `id_producto` (`id_producto`),
  CONSTRAINT `tb_alerta_stock_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `tb_producto` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ========================================================
-- DATOS INICIALES: tb_state
-- ========================================================
INSERT INTO `tb_state` (`nombre`, `tipo`) VALUES
('Activa',         'venta'),
('Pagada',         'venta'),
('Cancelada',      'venta'),
('Activo',         'producto'),
('Inactivo',       'producto'),
('Descontinuado',  'producto'),
('Abierto',        'caja'),
('Cerrado',        'caja'),
('Registrada',     'entrada'),
('Anulada',        'entrada'),
('Registrada',     'perdida'),
('Resuelta',       'perdida');



COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

