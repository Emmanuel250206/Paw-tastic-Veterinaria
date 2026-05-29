-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 29-05-2026 a las 05:01:06
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `pawtastic_01`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_alerta_stock`
--

CREATE TABLE `tb_alerta_stock` (
  `id` int(11) NOT NULL,
  `id_producto` int(11) NOT NULL,
  `existencia` int(11) NOT NULL,
  `stock_minimo` int(11) NOT NULL,
  `fecha` datetime NOT NULL DEFAULT current_timestamp(),
  `resuelta` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_alerta_stock`
--

INSERT INTO `tb_alerta_stock` (`id`, `id_producto`, `existencia`, `stock_minimo`, `fecha`, `resuelta`) VALUES
(1, 4, 10, 3, '2026-05-20 08:00:00', 0),
(2, 6, 8, 2, '2026-05-20 08:00:00', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_bitacora`
--

CREATE TABLE `tb_bitacora` (
  `id` int(11) NOT NULL,
  `fecha_hora` datetime NOT NULL DEFAULT current_timestamp(),
  `id_usuario_web` int(11) NOT NULL,
  `modulo` varchar(50) NOT NULL,
  `detalle` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_bitacora`
--

INSERT INTO `tb_bitacora` (`id`, `fecha_hora`, `id_usuario_web`, `modulo`, `detalle`) VALUES
(1, '2026-04-10 09:35:00', 8, 'Citas', 'Cita registrada para Nessy - Vacunación'),
(2, '2026-04-12 10:35:00', 8, 'Citas', 'Cita registrada para Max - Vacunación múltiple'),
(3, '2026-04-15 11:35:00', 8, 'Ventas', 'Venta #3 registrada - Shampoo Antipulgas'),
(4, '2026-04-18 09:35:00', 9, 'Citas', 'Cita urgente Rocky - Radiografía cadera'),
(5, '2026-05-05 10:35:00', 8, 'Inventario', 'Entrada de inventario registrada'),
(6, '2026-05-10 11:35:00', 8, 'Ventas', 'Venta #6 registrada - Arena para Gato'),
(7, '2026-05-22 09:00:00', 6, 'Usuarios', 'Usuario luz creado por administrador'),
(8, '2026-05-28 08:00:00', 8, 'Sesión', 'Inicio de sesión dra_sofia'),
(9, '2026-05-28 08:05:00', 10, 'Sesión', 'Inicio de sesión ana_staff'),
(10, '2026-05-28 08:10:00', 9, 'Sesión', 'Inicio de sesión dr_carlos');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_cierre_caja`
--

CREATE TABLE `tb_cierre_caja` (
  `id` int(11) NOT NULL,
  `id_venta` int(11) NOT NULL,
  `fecha_registro` datetime NOT NULL,
  `total_tarjeta` decimal(10,2) DEFAULT NULL,
  `total_efectivo` decimal(10,2) DEFAULT NULL,
  `total_general` decimal(10,2) NOT NULL,
  `id_State` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_cierre_caja`
--

INSERT INTO `tb_cierre_caja` (`id`, `id_venta`, `fecha_registro`, `total_tarjeta`, `total_efectivo`, `total_general`, `id_State`) VALUES
(1, 1, '2026-04-10 18:00:00', 0.00, 250.00, 250.00, 8),
(2, 2, '2026-04-12 18:00:00', 850.00, 0.00, 850.00, 8),
(3, 3, '2026-04-15 18:00:00', 0.00, 120.00, 120.00, 8),
(4, 5, '2026-05-05 18:00:00', 0.00, 140.00, 140.00, 8),
(5, 6, '2026-05-10 18:00:00', 180.00, 0.00, 180.00, 8);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_citas`
--

CREATE TABLE `tb_citas` (
  `id` int(11) NOT NULL,
  `id_mascota` int(11) NOT NULL,
  `id_usuario_web` int(11) DEFAULT NULL,
  `id_usuario_movil` int(11) DEFAULT NULL,
  `motivo` varchar(100) DEFAULT NULL,
  `fecha` datetime DEFAULT NULL,
  `fecha_reg` datetime NOT NULL,
  `id_tipo_cita` int(11) DEFAULT NULL,
  `id_state` int(11) NOT NULL DEFAULT 4
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_citas`
--

INSERT INTO `tb_citas` (`id`, `id_mascota`, `id_usuario_web`, `id_usuario_movil`, `motivo`, `fecha`, `fecha_reg`, `id_tipo_cita`, `id_state`) VALUES
(1, 4, 8, NULL, 'Vacunación anual - refuerzo', '2026-05-28 09:00:00', '2026-05-28 21:00:57', 1, 14),
(2, 5, 8, NULL, 'Control de peso y dieta', '2026-05-28 09:30:00', '2026-05-28 21:00:57', 6, 13),
(3, 6, 8, NULL, 'Baño y corte de pelo', '2026-05-28 10:00:00', '2026-05-28 21:00:57', 4, 15),
(4, 7, 9, NULL, 'Dolor en pata trasera - urgente', '2026-05-28 10:30:00', '2026-05-28 21:00:57', 5, 14),
(5, 8, 8, NULL, 'Desparasitación interna', '2026-05-28 11:00:00', '2026-05-28 21:00:57', 3, 13),
(6, 9, 8, NULL, 'Revisión general semestral', '2026-05-28 11:30:00', '2026-05-28 21:00:57', 6, 13),
(7, 10, 9, NULL, 'Revisión post-operación cadera', '2026-05-28 12:00:00', '2026-05-28 21:00:57', 6, 14),
(8, 11, 8, NULL, 'Primera consulta', '2026-05-28 12:30:00', '2026-05-28 21:00:57', 6, 13),
(9, 12, 8, NULL, 'Suplemento vitamínico seguimiento', '2026-05-28 13:00:00', '2026-05-28 21:00:57', 6, 13),
(10, 13, 9, NULL, 'Revisión dental', '2026-05-28 13:30:00', '2026-05-28 21:00:57', 6, 13),
(11, 4, 8, NULL, 'Primera consulta Nessy', '2026-04-10 09:00:00', '2026-04-10 08:50:00', 6, 16),
(12, 5, 8, NULL, 'Vacunación múltiple', '2026-04-12 10:00:00', '2026-04-12 09:50:00', 1, 16),
(13, 6, 8, NULL, 'Tratamiento dermatitis', '2026-04-15 11:00:00', '2026-04-15 10:50:00', 6, 16),
(14, 7, 9, NULL, 'Radiografía cadera', '2026-04-18 09:00:00', '2026-04-18 08:50:00', 6, 16),
(15, 1, 8, NULL, 'Desparasitación Firulais', '2026-05-05 10:00:00', '2026-05-05 09:50:00', 3, 16),
(16, 2, 8, NULL, 'Revisión Michi', '2026-05-10 11:00:00', '2026-05-10 10:50:00', 6, 16);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_clinicas`
--

CREATE TABLE `tb_clinicas` (
  `id` int(11) NOT NULL,
  `nombre` varchar(120) NOT NULL,
  `rfc` varchar(20) NOT NULL,
  `direccion` varchar(200) NOT NULL,
  `telefono` varchar(10) NOT NULL,
  `estado` char(1) NOT NULL,
  `created_at` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_clinicas`
--

INSERT INTO `tb_clinicas` (`id`, `nombre`, `rfc`, `direccion`, `telefono`, `estado`, `created_at`) VALUES
(4, 'Patitas', '2511456625', 'Paseo Jardines #2 col.marina nacional', '222551566', '1', '2026-05-22 23:21:25');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_detalle_entrada`
--

CREATE TABLE `tb_detalle_entrada` (
  `id` int(11) NOT NULL,
  `id_entrada` int(11) NOT NULL,
  `costo_unitario` decimal(10,2) NOT NULL,
  `id_producto` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_detalle_entrada`
--

INSERT INTO `tb_detalle_entrada` (`id`, `id_entrada`, `costo_unitario`, `id_producto`) VALUES
(1, 1, 650.00, 1),
(2, 1, 120.00, 2),
(3, 1, 75.00, 3),
(4, 1, 180.00, 4),
(5, 2, 90.00, 7),
(6, 2, 150.00, 8);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_detalle_venta`
--

CREATE TABLE `tb_detalle_venta` (
  `id` int(11) NOT NULL,
  `id_perdida` int(11) DEFAULT NULL,
  `id_venta` int(11) NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `id_producto` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_detalle_venta`
--

INSERT INTO `tb_detalle_venta` (`id`, `id_perdida`, `id_venta`, `precio_unitario`, `subtotal`, `id_producto`, `cantidad`) VALUES
(1, NULL, 1, 250.00, 250.00, 4, 1),
(2, NULL, 2, 850.00, 850.00, 1, 1),
(3, NULL, 3, 120.00, 120.00, 3, 1),
(4, NULL, 4, 140.00, 140.00, 7, 1),
(5, NULL, 5, 140.00, 140.00, 7, 1),
(6, NULL, 6, 180.00, 180.00, 2, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_diagnosticos`
--

CREATE TABLE `tb_diagnosticos` (
  `id` int(11) NOT NULL,
  `id_expediente` int(11) NOT NULL,
  `id_cita` int(11) NOT NULL,
  `descripcion` varchar(300) NOT NULL,
  `tratamiento` varchar(500) DEFAULT NULL,
  `fecha_registro` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_diagnosticos`
--

INSERT INTO `tb_diagnosticos` (`id`, `id_expediente`, `id_cita`, `descripcion`, `tratamiento`, `fecha_registro`) VALUES
(1, 4, 11, 'Nessy en perfecto estado. Sin anomalías detectadas.', 'Vacuna antirrábica aplicada. Próxima visita en 12 meses.', '2026-04-10 09:20:00'),
(2, 5, 12, 'Max con sobrepeso de 2 kg. Condición corporal 4/5.', 'Dieta baja en calorías. Reducir premios. Control mensual.', '2026-04-12 10:20:00'),
(3, 6, 13, 'Luna presenta dermatitis alérgica leve en zona dorsal.', 'Shampoo medicado 2 veces/semana. Crema betametasona local.', '2026-04-15 11:20:00'),
(4, 7, 14, 'Rocky con signos de displasia de cadera bilateral grado I.', 'Antiinflamatorio 5 días. Reposo relativo. Radiografía de control.', '2026-04-18 09:20:00'),
(5, 1, 15, 'Firulais sano. Parásitos internos negativos.', 'Desparasitante oral aplicado. Repetir en 6 meses.', '2026-05-05 10:20:00'),
(6, 2, 16, 'Michi con condición corporal ideal. Coat brillante.', 'Continuar dieta actual. Revisión anual.', '2026-05-10 11:20:00');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_diagnostico_medicamento`
--

CREATE TABLE `tb_diagnostico_medicamento` (
  `id` int(11) NOT NULL,
  `id_diagnostico` int(11) NOT NULL,
  `id_producto` int(11) NOT NULL,
  `dosis` varchar(100) NOT NULL,
  `frecuencia` varchar(100) DEFAULT NULL,
  `duracion_dias` tinyint(3) UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_diagnostico_medicamento`
--

INSERT INTO `tb_diagnostico_medicamento` (`id`, `id_diagnostico`, `id_producto`, `dosis`, `frecuencia`, `duracion_dias`) VALUES
(1, 3, 3, '5 ml', 'Cada 3 días en baño', 30),
(2, 4, 4, '1 tab', 'Cada 24 horas', 5),
(3, 5, 7, '1 tab', 'Dosis única', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_entrada`
--

CREATE TABLE `tb_entrada` (
  `id` int(11) NOT NULL,
  `fecha` datetime NOT NULL,
  `total` decimal(10,2) NOT NULL,
  `id_State` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_entrada`
--

INSERT INTO `tb_entrada` (`id`, `fecha`, `total`, `id_State`) VALUES
(1, '2026-04-01 08:00:00', 6500.00, 9),
(2, '2026-05-01 08:00:00', 3200.00, 9);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_especie`
--

CREATE TABLE `tb_especie` (
  `id` int(11) NOT NULL,
  `especie` varchar(50) NOT NULL,
  `descripcion` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_especie`
--

INSERT INTO `tb_especie` (`id`, `especie`, `descripcion`) VALUES
(1, 'Perro', 'Caninos domésticos'),
(2, 'Gato', 'Felinos domésticos'),
(3, 'Conejo', 'Conejos domésticos');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_expediente`
--

CREATE TABLE `tb_expediente` (
  `id` int(11) NOT NULL,
  `id_mascota` int(11) NOT NULL,
  `historial` text NOT NULL,
  `fecha_apertura` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_expediente`
--

INSERT INTO `tb_expediente` (`id`, `id_mascota`, `historial`, `fecha_apertura`) VALUES
(1, 1, 'Consulta general. Todo bien.', '2026-05-28 17:04:52'),
(2, 2, 'Vacunación anual aplicada.', '2026-05-28 17:04:52'),
(3, 3, 'Revisión de orejas. Tratamiento de ácaros en curso.', '2026-05-28 17:04:52'),
(4, 4, 'Primera visita. Nessy en excelente estado. Vacuna antirrábica aplicada.', '2026-04-10 09:15:00'),
(5, 5, 'Max presenta sobrepeso leve. Dieta recomendada.', '2026-04-12 10:45:00'),
(6, 6, 'Luna con dermatitis leve. Tratamiento tópico iniciado.', '2026-04-15 11:15:00'),
(7, 7, 'Rocky con displasia de cadera en observación.', '2026-04-18 09:05:00'),
(8, 8, 'Coco sano. Desparasitación interna aplicada.', '2026-04-20 14:15:00'),
(9, 9, 'Bella con nudo en pelo. Estética y revisión general.', '2026-04-22 16:45:00'),
(10, 10, 'Thor con otitis leve oído izquierdo. Gotas recetadas.', '2026-04-25 09:25:00'),
(11, 11, 'Mochi primera consulta. Todo en orden.', '2026-05-01 10:15:00'),
(12, 12, 'Kira con pelaje opaco. Suplemento vitamínico indicado.', '2026-05-05 11:05:00'),
(13, 13, 'Bono revisión de dientes. Dieta de heno recomendada.', '2026-05-10 12:05:00');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_mascotas`
--

CREATE TABLE `tb_mascotas` (
  `id` int(11) NOT NULL,
  `id_propietario` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `id_especie` int(11) NOT NULL,
  `id_raza` int(11) NOT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `estado` char(1) NOT NULL,
  `created_at` datetime NOT NULL,
  `descripcion` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_mascotas`
--

INSERT INTO `tb_mascotas` (`id`, `id_propietario`, `nombre`, `id_especie`, `id_raza`, `fecha_nacimiento`, `estado`, `created_at`, `descripcion`) VALUES
(1, 1, 'Firulais', 1, 1, '2020-05-15', '1', '2026-05-28 17:04:52', 'Perro juguetón'),
(2, 2, 'Michi', 2, 3, '2021-08-20', '1', '2026-05-28 17:04:52', 'Gato dormilón'),
(3, 3, 'Tambor', 3, 5, '2022-11-10', '1', '2026-05-28 17:04:52', 'Conejo blanco'),
(4, 4, 'Nessy', 1, 6, '2023-03-14', '1', '2026-04-10 09:10:00', 'Chihuahua hembra muy activa y curiosa'),
(5, 5, 'Max', 1, 7, '2021-07-20', '1', '2026-04-12 10:40:00', 'Golden Retriever juguetón'),
(6, 6, 'Luna', 1, 8, '2022-05-05', '1', '2026-04-15 11:10:00', 'Bulldog Francés tranquila'),
(7, 7, 'Rocky', 1, 2, '2020-11-30', '1', '2026-04-18 09:00:00', 'Pastor Alemán guardián'),
(8, 8, 'Coco', 2, 11, '2022-09-18', '1', '2026-04-20 14:10:00', 'Maine Coon muy sociable'),
(9, 9, 'Bella', 1, 10, '2023-01-08', '1', '2026-04-22 16:40:00', 'Shih Tzu de pelo largo'),
(10, 10, 'Thor', 1, 9, '2021-04-22', '1', '2026-04-25 09:20:00', 'Poodle inteligente color beige'),
(11, 11, 'Mochi', 2, 12, '2023-06-11', '1', '2026-05-01 10:10:00', 'Bengalí atigrado muy activo'),
(12, 4, 'Kira', 2, 13, '2022-12-01', '1', '2026-05-05 11:00:00', 'Angora blanca y esponjosa'),
(13, 5, 'Bono', 3, 14, '2024-02-14', '1', '2026-05-10 12:00:00', 'Holland Lop gris adorable');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_perdida`
--

CREATE TABLE `tb_perdida` (
  `id` int(11) NOT NULL,
  `fecha` datetime NOT NULL,
  `id_State` int(11) NOT NULL,
  `total` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_producto`
--

CREATE TABLE `tb_producto` (
  `id` int(11) NOT NULL,
  `id_clinica` int(11) NOT NULL,
  `nombre` varchar(120) NOT NULL,
  `categoria` varchar(100) DEFAULT NULL,
  `descripcion` varchar(300) DEFAULT NULL,
  `codigo` varchar(30) NOT NULL,
  `precio` decimal(10,2) NOT NULL,
  `costo_unitario` decimal(10,2) NOT NULL,
  `existencia` int(11) NOT NULL,
  `stock_minimo` int(11) NOT NULL DEFAULT 0,
  `unidad_medida` varchar(50) DEFAULT NULL,
  `fecha_caducidad` date DEFAULT NULL,
  `fecha` date NOT NULL,
  `id_State` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_producto`
--

INSERT INTO `tb_producto` (`id`, `id_clinica`, `nombre`, `categoria`, `descripcion`, `codigo`, `precio`, `costo_unitario`, `existencia`, `stock_minimo`, `unidad_medida`, `fecha_caducidad`, `fecha`, `id_State`) VALUES
(1, 0, 'Croquetas Premium', 'Alimento', 'Alimento para perro adulto', 'PROD001', 850.00, 650.00, 20, 5, 'Bolsa', '2027-01-15', '2026-05-21', 4),
(2, 0, 'Arena para Gato', 'Higiene', 'Arena absorbente para gatos', 'PROD002', 180.00, 120.00, 35, 10, 'Bolsa', '2028-03-20', '2026-05-21', 4),
(3, 0, 'Shampoo Antipulgas', 'Higiene', 'Shampoo para perros y gatos', 'PROD003', 120.00, 75.00, 15, 5, 'Botella', '2026-11-10', '2026-05-21', 4),
(4, 0, 'Vitaminas Caninas', 'Medicamento', 'Vitaminas para fortalecer defensas', 'PROD004', 250.00, 180.00, 10, 3, 'Frasco', '2027-07-05', '2026-05-21', 4),
(5, 0, 'Juguete Mordedera', 'Accesorio', 'Juguete resistente para perros', 'PROD005', 95.00, 50.00, 40, 8, 'Pieza', NULL, '2026-05-21', 5),
(6, 0, 'Transportadora', 'Accesorio', 'Caja transportadora para mascotas', 'PROD006', 650.00, 500.00, 8, 2, 'Pieza', NULL, '2026-05-21', 5),
(7, 0, 'Desparasitante', 'Medicamento', 'Tratamiento antiparasitario', 'PROD007', 140.00, 90.00, 21, 5, 'Caja', '2026-12-01', '2026-05-21', 4),
(8, 0, 'Collar Antipulgas', 'Accesorio', 'Collar para control de pulgas', 'PROD008', 210.00, 150.00, 18, 4, 'Pieza', '2027-05-18', '2026-05-21', 4);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_propietarios`
--

CREATE TABLE `tb_propietarios` (
  `id` int(11) NOT NULL,
  `id_usuario_web` int(11) DEFAULT NULL,
  `id_usuario_movil` int(11) DEFAULT NULL,
  `nombre` varchar(100) DEFAULT NULL,
  `apellidos` varchar(100) DEFAULT NULL,
  `telefono` varchar(10) DEFAULT NULL,
  `direccion` varchar(100) NOT NULL,
  `estado` char(1) NOT NULL,
  `created_at` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_propietarios`
--

INSERT INTO `tb_propietarios` (`id`, `id_usuario_web`, `id_usuario_movil`, `nombre`, `apellidos`, `telefono`, `direccion`, `estado`, `created_at`) VALUES
(1, NULL, NULL, 'Juan', 'Pérez', '5551234567', 'Calle Falsa 123', '1', '2026-05-28 17:04:52'),
(2, NULL, NULL, 'María', 'López', '5559876543', 'Av. Siempre Viva 742', '1', '2026-05-28 17:04:52'),
(3, NULL, NULL, 'Carlos', 'Gómez', '5551112222', 'Paseo de la Reforma 45', '1', '2026-05-28 17:04:52'),
(4, NULL, NULL, 'Valeria', 'Mendoza', '5554441122', 'Insurgentes Sur 890', '1', '2026-04-10 09:00:00'),
(5, NULL, NULL, 'Roberto', 'Sánchez', '5557778899', 'Av. Universidad 310', '1', '2026-04-12 10:30:00'),
(6, NULL, NULL, 'Ana', 'Flores', '5553334455', 'Calle Morelos 55', '1', '2026-04-15 11:00:00'),
(7, NULL, NULL, 'Diego', 'Hernández', '5556667788', 'Paseo de las Palmas 12', '1', '2026-04-18 08:45:00'),
(8, NULL, NULL, 'Sofía', 'Ramírez', '5551239876', 'Lomas de Chapultepec 403', '1', '2026-04-20 14:00:00'),
(9, NULL, NULL, 'Miguel', 'Torres', '5559990011', 'Calzada del Hueso 78', '1', '2026-04-22 16:30:00'),
(10, NULL, NULL, 'Fernanda', 'Castillo', '5552223344', 'Av. Revolución 1600', '1', '2026-04-25 09:15:00'),
(11, NULL, NULL, 'Andrés', 'Morales', '5558885566', 'Coyoacán Centro 22', '1', '2026-05-01 10:00:00');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_raza`
--

CREATE TABLE `tb_raza` (
  `id` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(200) NOT NULL,
  `id_especie` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_raza`
--

INSERT INTO `tb_raza` (`id`, `nombre`, `descripcion`, `id_especie`) VALUES
(1, 'Labrador', 'Perro grande y amigable', 1),
(2, 'Pastor Alemán', 'Perro guardián', 1),
(3, 'Siamés', 'Gato de ojos azules', 2),
(4, 'Persa', 'Gato de pelo largo', 2),
(5, 'Mini Lop', 'Conejo doméstico pequeño', 3),
(6, 'Chihuahua', 'Perro pequeño muy expresivo', 1),
(7, 'Golden Retriever', 'Perro familiar y cariñoso', 1),
(8, 'Bulldog Francés', 'Perro compacto y juguetón', 1),
(9, 'Poodle', 'Perro inteligente de pelo rizado', 1),
(10, 'Shih Tzu', 'Perro de compañía pelo largo', 1),
(11, 'Maine Coon', 'Gato grande y sociable', 2),
(12, 'Bengalí', 'Gato de pelaje atigrado', 2),
(13, 'Angora', 'Gato de pelo largo y sedoso', 2),
(14, 'Holland Lop', 'Conejo de orejas caídas', 3);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_state`
--

CREATE TABLE `tb_state` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `tipo` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_state`
--

INSERT INTO `tb_state` (`id`, `nombre`, `tipo`) VALUES
(1, 'Activa', 'venta'),
(2, 'Pagada', 'venta'),
(3, 'Cancelada', 'venta'),
(4, 'Activo', 'producto'),
(5, 'Inactivo', 'producto'),
(6, 'Descontinuado', 'producto'),
(7, 'Abierto', 'caja'),
(8, 'Cerrado', 'caja'),
(9, 'Registrada', 'entrada'),
(10, 'Anulada', 'entrada'),
(11, 'Registrada', 'perdida'),
(12, 'Resuelta', 'perdida'),
(13, 'Pendiente', 'cita'),
(14, 'Confirmada', 'cita'),
(15, 'En curso', 'cita'),
(16, 'Completada', 'cita'),
(17, 'Reprogramada', 'cita'),
(18, 'Cancelada', 'cita');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_tipo_cita`
--

CREATE TABLE `tb_tipo_cita` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  `id_State` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_tipo_cita`
--

INSERT INTO `tb_tipo_cita` (`id`, `nombre`, `descripcion`, `id_State`) VALUES
(1, 'Vacunación', 'Aplicación de vacunas', 13),
(2, 'Consulta', 'Consulta médica general', 13),
(3, 'Desparasitación', 'Control antiparasitario', 13),
(4, 'Estética', 'Baño, corte y arreglo', 13),
(5, 'Urgente', 'Atención de emergencia inmediata', 13),
(6, 'Revisión', 'Chequeo médico preventivo', 13);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_usuario_movil`
--

CREATE TABLE `tb_usuario_movil` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellidos` varchar(100) DEFAULT NULL,
  `telefono` varchar(10) NOT NULL,
  `email` varchar(100) NOT NULL,
  `contrasenia` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_usuario_web`
--

CREATE TABLE `tb_usuario_web` (
  `id` int(11) NOT NULL,
  `id_clinica` int(11) NOT NULL,
  `usuario` varchar(10) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellidos` varchar(100) DEFAULT NULL,
  `tipo_rol` enum('administrador','veterinario','recepcionista','asistente') NOT NULL,
  `especialidad` varchar(100) DEFAULT NULL,
  `cedula` varchar(100) DEFAULT NULL,
  `telefono` varchar(10) NOT NULL,
  `email` varchar(100) NOT NULL,
  `contrasenia` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `activo` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_usuario_web`
--

INSERT INTO `tb_usuario_web` (`id`, `id_clinica`, `usuario`, `nombre`, `apellidos`, `tipo_rol`, `especialidad`, `cedula`, `telefono`, `email`, `contrasenia`, `created_at`, `activo`) VALUES
(6, 4, 'emma', 'Emmanuel', 'Hernandez', 'administrador', NULL, NULL, '225551662', 'emma@gmail.com', '1010', '2026-05-22 23:21:25', 0),
(7, 4, 'luz', 'Luz', 'Lopez', 'recepcionista', NULL, NULL, '2286045398', 'luz@gmail.com', '2020', '2026-05-28 14:56:52', 1),
(8, 4, 'dra_sofia', 'Sofía', 'Aguilar', 'veterinario', 'Medicina general', 'VET-2281', '5550001111', 'sofia@pawtastic.mx', '1234', '2026-04-01 08:00:00', 1),
(9, 4, 'dr_carlos', 'Carlos', 'Mendoza', 'veterinario', 'Cirugía veterinaria', 'VET-4492', '5550002222', 'carlos@pawtastic.mx', '1234', '2026-04-01 08:00:00', 1),
(10, 4, 'ana_staff', 'Ana', 'Gutiérrez', 'recepcionista', NULL, NULL, '5550003333', 'ana@pawtastic.mx', '1234', '2026-04-01 08:00:00', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_venta`
--

CREATE TABLE `tb_venta` (
  `id` int(11) NOT NULL,
  `id_cita` int(11) NOT NULL,
  `id_usuario_web` int(11) DEFAULT NULL,
  `id_usuario_movil` int(11) DEFAULT NULL,
  `metodo_pago` char(1) NOT NULL,
  `total` decimal(10,2) NOT NULL,
  `fecha` datetime NOT NULL,
  `fecha_reg` datetime NOT NULL,
  `id_State` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_venta`
--

INSERT INTO `tb_venta` (`id`, `id_cita`, `id_usuario_web`, `id_usuario_movil`, `metodo_pago`, `total`, `fecha`, `fecha_reg`, `id_State`) VALUES
(1, 11, 8, NULL, 'E', 250.00, '2026-04-10 09:30:00', '2026-04-10 09:30:00', 2),
(2, 12, 8, NULL, 'T', 850.00, '2026-04-12 10:30:00', '2026-04-12 10:30:00', 2),
(3, 13, 8, NULL, 'E', 120.00, '2026-04-15 11:30:00', '2026-04-15 11:30:00', 2),
(4, 14, 9, NULL, 'T', 140.00, '2026-04-18 09:30:00', '2026-04-18 09:30:00', 2),
(5, 15, 8, NULL, 'E', 140.00, '2026-05-05 10:30:00', '2026-05-05 10:30:00', 2),
(6, 16, 8, NULL, 'T', 180.00, '2026-05-10 11:30:00', '2026-05-10 11:30:00', 2);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `tb_alerta_stock`
--
ALTER TABLE `tb_alerta_stock`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_producto` (`id_producto`);

--
-- Indices de la tabla `tb_bitacora`
--
ALTER TABLE `tb_bitacora`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_usuario_web` (`id_usuario_web`);

--
-- Indices de la tabla `tb_cierre_caja`
--
ALTER TABLE `tb_cierre_caja`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_venta` (`id_venta`),
  ADD KEY `id_State` (`id_State`);

--
-- Indices de la tabla `tb_citas`
--
ALTER TABLE `tb_citas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_mascota` (`id_mascota`),
  ADD KEY `id_usuario_web` (`id_usuario_web`),
  ADD KEY `id_usuario_movil` (`id_usuario_movil`),
  ADD KEY `fk_tipo` (`id_tipo_cita`);

--
-- Indices de la tabla `tb_clinicas`
--
ALTER TABLE `tb_clinicas`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `tb_detalle_entrada`
--
ALTER TABLE `tb_detalle_entrada`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_entrada` (`id_entrada`),
  ADD KEY `id_producto` (`id_producto`);

--
-- Indices de la tabla `tb_detalle_venta`
--
ALTER TABLE `tb_detalle_venta`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_perdida` (`id_perdida`),
  ADD KEY `id_venta` (`id_venta`),
  ADD KEY `id_producto` (`id_producto`);

--
-- Indices de la tabla `tb_diagnosticos`
--
ALTER TABLE `tb_diagnosticos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_expediente` (`id_expediente`),
  ADD KEY `id_cita` (`id_cita`);

--
-- Indices de la tabla `tb_diagnostico_medicamento`
--
ALTER TABLE `tb_diagnostico_medicamento`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_diagnostico` (`id_diagnostico`),
  ADD KEY `id_producto` (`id_producto`);

--
-- Indices de la tabla `tb_entrada`
--
ALTER TABLE `tb_entrada`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_State` (`id_State`);

--
-- Indices de la tabla `tb_especie`
--
ALTER TABLE `tb_especie`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `tb_expediente`
--
ALTER TABLE `tb_expediente`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_mascota` (`id_mascota`);

--
-- Indices de la tabla `tb_mascotas`
--
ALTER TABLE `tb_mascotas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_propietario` (`id_propietario`),
  ADD KEY `id_especie` (`id_especie`),
  ADD KEY `id_raza` (`id_raza`);

--
-- Indices de la tabla `tb_perdida`
--
ALTER TABLE `tb_perdida`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_State` (`id_State`);

--
-- Indices de la tabla `tb_producto`
--
ALTER TABLE `tb_producto`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_codigo` (`codigo`),
  ADD KEY `id_State` (`id_State`);

--
-- Indices de la tabla `tb_propietarios`
--
ALTER TABLE `tb_propietarios`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_usuario_web` (`id_usuario_web`),
  ADD KEY `id_usuario_movil` (`id_usuario_movil`);

--
-- Indices de la tabla `tb_raza`
--
ALTER TABLE `tb_raza`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_especie` (`id_especie`);

--
-- Indices de la tabla `tb_state`
--
ALTER TABLE `tb_state`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `tb_tipo_cita`
--
ALTER TABLE `tb_tipo_cita`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_State` (`id_State`);

--
-- Indices de la tabla `tb_usuario_movil`
--
ALTER TABLE `tb_usuario_movil`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `tb_usuario_web`
--
ALTER TABLE `tb_usuario_web`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_clinica` (`id_clinica`);

--
-- Indices de la tabla `tb_venta`
--
ALTER TABLE `tb_venta`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_cita` (`id_cita`),
  ADD KEY `id_usuario_web` (`id_usuario_web`),
  ADD KEY `id_usuario_movil` (`id_usuario_movil`),
  ADD KEY `id_State` (`id_State`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `tb_bitacora`
--
ALTER TABLE `tb_bitacora`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `tb_cierre_caja`
--
ALTER TABLE `tb_cierre_caja`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `tb_citas`
--
ALTER TABLE `tb_citas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT de la tabla `tb_clinicas`
--
ALTER TABLE `tb_clinicas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `tb_detalle_entrada`
--
ALTER TABLE `tb_detalle_entrada`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `tb_detalle_venta`
--
ALTER TABLE `tb_detalle_venta`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `tb_diagnosticos`
--
ALTER TABLE `tb_diagnosticos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `tb_diagnostico_medicamento`
--
ALTER TABLE `tb_diagnostico_medicamento`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `tb_entrada`
--
ALTER TABLE `tb_entrada`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `tb_especie`
--
ALTER TABLE `tb_especie`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `tb_expediente`
--
ALTER TABLE `tb_expediente`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT de la tabla `tb_mascotas`
--
ALTER TABLE `tb_mascotas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT de la tabla `tb_perdida`
--
ALTER TABLE `tb_perdida`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `tb_producto`
--
ALTER TABLE `tb_producto`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `tb_propietarios`
--
ALTER TABLE `tb_propietarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT de la tabla `tb_raza`
--
ALTER TABLE `tb_raza`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT de la tabla `tb_state`
--
ALTER TABLE `tb_state`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT de la tabla `tb_tipo_cita`
--
ALTER TABLE `tb_tipo_cita`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `tb_usuario_movil`
--
ALTER TABLE `tb_usuario_movil`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `tb_usuario_web`
--
ALTER TABLE `tb_usuario_web`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `tb_venta`
--
ALTER TABLE `tb_venta`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `tb_alerta_stock`
--
ALTER TABLE `tb_alerta_stock`
  ADD CONSTRAINT `tb_alerta_stock_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `tb_producto` (`id`);

--
-- Filtros para la tabla `tb_bitacora`
--
ALTER TABLE `tb_bitacora`
  ADD CONSTRAINT `tb_bitacora_ibfk_1` FOREIGN KEY (`id_usuario_web`) REFERENCES `tb_usuario_web` (`id`);

--
-- Filtros para la tabla `tb_cierre_caja`
--
ALTER TABLE `tb_cierre_caja`
  ADD CONSTRAINT `tb_cierre_caja_ibfk_1` FOREIGN KEY (`id_venta`) REFERENCES `tb_venta` (`id`),
  ADD CONSTRAINT `tb_cierre_caja_ibfk_2` FOREIGN KEY (`id_State`) REFERENCES `tb_state` (`id`);

--
-- Filtros para la tabla `tb_citas`
--
ALTER TABLE `tb_citas`
  ADD CONSTRAINT `fk_tipo` FOREIGN KEY (`id_tipo_cita`) REFERENCES `tb_tipo_cita` (`id`),
  ADD CONSTRAINT `tb_citas_ibfk_1` FOREIGN KEY (`id_mascota`) REFERENCES `tb_mascotas` (`id`),
  ADD CONSTRAINT `tb_citas_ibfk_2` FOREIGN KEY (`id_usuario_web`) REFERENCES `tb_usuario_web` (`id`),
  ADD CONSTRAINT `tb_citas_ibfk_3` FOREIGN KEY (`id_usuario_movil`) REFERENCES `tb_usuario_movil` (`id`);

--
-- Filtros para la tabla `tb_detalle_entrada`
--
ALTER TABLE `tb_detalle_entrada`
  ADD CONSTRAINT `tb_detalle_entrada_ibfk_1` FOREIGN KEY (`id_entrada`) REFERENCES `tb_entrada` (`id`),
  ADD CONSTRAINT `tb_detalle_entrada_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `tb_producto` (`id`);

--
-- Filtros para la tabla `tb_detalle_venta`
--
ALTER TABLE `tb_detalle_venta`
  ADD CONSTRAINT `tb_detalle_venta_ibfk_1` FOREIGN KEY (`id_perdida`) REFERENCES `tb_perdida` (`id`),
  ADD CONSTRAINT `tb_detalle_venta_ibfk_2` FOREIGN KEY (`id_venta`) REFERENCES `tb_venta` (`id`),
  ADD CONSTRAINT `tb_detalle_venta_ibfk_3` FOREIGN KEY (`id_producto`) REFERENCES `tb_producto` (`id`);

--
-- Filtros para la tabla `tb_diagnosticos`
--
ALTER TABLE `tb_diagnosticos`
  ADD CONSTRAINT `tb_diagnosticos_ibfk_1` FOREIGN KEY (`id_expediente`) REFERENCES `tb_expediente` (`id`),
  ADD CONSTRAINT `tb_diagnosticos_ibfk_2` FOREIGN KEY (`id_cita`) REFERENCES `tb_citas` (`id`);

--
-- Filtros para la tabla `tb_diagnostico_medicamento`
--
ALTER TABLE `tb_diagnostico_medicamento`
  ADD CONSTRAINT `tb_diag_med_ibfk_1` FOREIGN KEY (`id_diagnostico`) REFERENCES `tb_diagnosticos` (`id`),
  ADD CONSTRAINT `tb_diag_med_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `tb_producto` (`id`);

--
-- Filtros para la tabla `tb_entrada`
--
ALTER TABLE `tb_entrada`
  ADD CONSTRAINT `tb_entrada_ibfk_1` FOREIGN KEY (`id_State`) REFERENCES `tb_state` (`id`);

--
-- Filtros para la tabla `tb_expediente`
--
ALTER TABLE `tb_expediente`
  ADD CONSTRAINT `tb_expediente_ibfk_1` FOREIGN KEY (`id_mascota`) REFERENCES `tb_mascotas` (`id`);

--
-- Filtros para la tabla `tb_mascotas`
--
ALTER TABLE `tb_mascotas`
  ADD CONSTRAINT `tb_mascotas_ibfk_1` FOREIGN KEY (`id_propietario`) REFERENCES `tb_propietarios` (`id`),
  ADD CONSTRAINT `tb_mascotas_ibfk_2` FOREIGN KEY (`id_especie`) REFERENCES `tb_especie` (`id`),
  ADD CONSTRAINT `tb_mascotas_ibfk_3` FOREIGN KEY (`id_raza`) REFERENCES `tb_raza` (`id`);

--
-- Filtros para la tabla `tb_perdida`
--
ALTER TABLE `tb_perdida`
  ADD CONSTRAINT `tb_perdida_ibfk_1` FOREIGN KEY (`id_State`) REFERENCES `tb_state` (`id`);

--
-- Filtros para la tabla `tb_producto`
--
ALTER TABLE `tb_producto`
  ADD CONSTRAINT `tb_producto_ibfk_1` FOREIGN KEY (`id_State`) REFERENCES `tb_state` (`id`);

--
-- Filtros para la tabla `tb_propietarios`
--
ALTER TABLE `tb_propietarios`
  ADD CONSTRAINT `tb_propietarios_ibfk_1` FOREIGN KEY (`id_usuario_web`) REFERENCES `tb_usuario_web` (`id`),
  ADD CONSTRAINT `tb_propietarios_ibfk_2` FOREIGN KEY (`id_usuario_movil`) REFERENCES `tb_usuario_movil` (`id`);

--
-- Filtros para la tabla `tb_raza`
--
ALTER TABLE `tb_raza`
  ADD CONSTRAINT `tb_raza_ibfk_1` FOREIGN KEY (`id_especie`) REFERENCES `tb_especie` (`id`);

--
-- Filtros para la tabla `tb_tipo_cita`
--
ALTER TABLE `tb_tipo_cita`
  ADD CONSTRAINT `tb_tipo_cita_ibfk_1` FOREIGN KEY (`id_State`) REFERENCES `tb_state` (`id`);

--
-- Filtros para la tabla `tb_usuario_web`
--
ALTER TABLE `tb_usuario_web`
  ADD CONSTRAINT `tb_usuario_web_ibfk_1` FOREIGN KEY (`id_clinica`) REFERENCES `tb_clinicas` (`id`);

--
-- Filtros para la tabla `tb_venta`
--
ALTER TABLE `tb_venta`
  ADD CONSTRAINT `tb_venta_ibfk_1` FOREIGN KEY (`id_cita`) REFERENCES `tb_citas` (`id`),
  ADD CONSTRAINT `tb_venta_ibfk_2` FOREIGN KEY (`id_usuario_web`) REFERENCES `tb_usuario_web` (`id`),
  ADD CONSTRAINT `tb_venta_ibfk_3` FOREIGN KEY (`id_usuario_movil`) REFERENCES `tb_usuario_movil` (`id`),
  ADD CONSTRAINT `tb_venta_ibfk_4` FOREIGN KEY (`id_State`) REFERENCES `tb_state` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
