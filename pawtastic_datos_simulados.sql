-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 21-05-2026 a las 20:49:25
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";
SET FOREIGN_KEY_CHECKS = 0;

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
  `estado` char(1) NOT NULL,
  `fecha` datetime DEFAULT NULL,
  `fecha_reg` datetime NOT NULL,
  `id_tipo_cita` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_citas`
--

INSERT INTO `tb_citas` (`id`, `id_mascota`, `id_usuario_web`, `id_usuario_movil`, `motivo`, `estado`, `fecha`, `fecha_reg`, `id_tipo_cita`) VALUES
(1, 1, 1, NULL, 'Vacuna anual', '1', '2026-06-10 10:00:00', '2026-05-21 12:31:32', 1),
(2, 3, 2, NULL, 'Problemas digestivos', '1', '2026-06-11 12:00:00', '2026-05-21 12:31:32', 2),
(3, 4, 1, 1, 'Control preventivo', '1', '2026-06-12 09:00:00', '2026-05-21 12:31:32', 3);

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
(1, 'mincho', '1234567890', 'cardel', '2961108090', '1', '2026-05-08 16:03:37'),
(2, 'vetpro', '123456789', 'flores magon norte#66', '2961497729', '1', '2026-05-13 20:52:14');

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
(1, NULL, 1, 850.00, 850.00, 1, 1),
(2, NULL, 2, 180.00, 360.00, 2, 2),
(3, NULL, 2, 90.00, 90.00, 7, 1),
(4, NULL, 3, 120.00, 240.00, 3, 2),
(5, NULL, 4, 250.00, 500.00, 4, 2),
(6, NULL, 4, 95.00, 190.00, 5, 2),
(7, NULL, 5, 650.00, 650.00, 6, 1);

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
(1, 1, 1, 'Mascota en buen estado general.', 'Continuar esquema de vacunación y alimentación balanceada.', '2026-06-10 11:00:00'),
(2, 2, 2, 'Inflamación leve en encías.', 'Limpieza dental y antibiótico por 5 días.', '2026-06-11 13:00:00'),
(3, 3, 2, 'Gastritis leve por cambio de alimento.', 'Dieta blanda y medicamento estomacal.', '2026-06-11 13:30:00'),
(4, 4, 3, 'Conejo con buen estado físico.', 'Aplicación de desparasitante preventivo.', '2026-06-12 10:00:00'),
(5, 5, 1, 'Sobrepeso moderado.', 'Reducir porciones y aumentar actividad física.', '2026-06-13 09:20:00');

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
(1, 1, 'Vacunación completa y desparasitación al día.', '2026-01-15 00:00:00'),
(2, 2, 'Historial de alergias leves y revisión dental.', '2026-02-10 00:00:00'),
(3, 3, 'Problemas digestivos tratados con medicamento.', '2026-03-05 00:00:00'),
(4, 4, 'Conejo sano, revisión general periódica.', '2026-04-20 00:00:00'),
(5, 1, 'Control de peso y alimentación especializada.', '2026-05-12 00:00:00');

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
(1, 1, 'Max', 1, 1, '2022-05-10', '1', '2026-05-21 11:57:21', 'Perro muy juguetón'),
(2, 1, 'Rocky', 1, 2, '2021-03-15', '1', '2026-05-21 11:57:21', 'Perro guardián'),
(3, 2, 'Michi', 2, 3, '2023-01-20', '1', '2026-05-21 11:57:21', 'Gato tranquilo'),
(4, 3, 'Nube', 3, 5, '2024-02-12', '1', '2026-05-21 11:57:21', 'Conejo blanco');

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

INSERT INTO `tb_producto` (`id`, `nombre`, `categoria`, `descripcion`, `codigo`, `precio`, `costo_unitario`, `existencia`, `stock_minimo`, `unidad_medida`, `fecha_caducidad`, `fecha`, `id_State`) VALUES
(1, 'Croquetas Premium', 'Alimento', 'Alimento para perro adulto', 'PROD001', 850.00, 650.00, 20, 5, 'Bolsa', '2027-01-15', '2026-05-21', 4),
(2, 'Arena para Gato', 'Higiene', 'Arena absorbente para gatos', 'PROD002', 180.00, 120.00, 35, 10, 'Bolsa', '2028-03-20', '2026-05-21', 4),
(3, 'Shampoo Antipulgas', 'Higiene', 'Shampoo para perros y gatos', 'PROD003', 120.00, 75.00, 15, 5, 'Botella', '2026-11-10', '2026-05-21', 4),
(4, 'Vitaminas Caninas', 'Medicamento', 'Vitaminas para fortalecer defensas', 'PROD004', 250.00, 180.00, 10, 3, 'Frasco', '2027-07-05', '2026-05-21', 4),
(5, 'Juguete Mordedera', 'Accesorio', 'Juguete resistente para perros', 'PROD005', 95.00, 50.00, 40, 8, 'Pieza', NULL, '2026-05-21', 4),
(6, 'Transportadora', 'Accesorio', 'Caja transportadora para mascotas', 'PROD006', 650.00, 500.00, 8, 2, 'Pieza', NULL, '2026-05-21', 4),
(7, 'Desparasitante', 'Medicamento', 'Tratamiento antiparasitario', 'PROD007', 140.00, 90.00, 25, 5, 'Caja', '2026-12-01', '2026-05-21', 4),
(8, 'Collar Antipulgas', 'Accesorio', 'Collar para control de pulgas', 'PROD008', 210.00, 150.00, 18, 4, 'Pieza', '2027-05-18', '2026-05-21', 4);

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
(1, 1, NULL, 'Carlos', 'Martínez', '2281112233', 'Xalapa, Veracruz', '1', '2026-05-21 11:56:03'),
(2, 2, NULL, 'Fernanda', 'López', '2284445566', 'Coatepec, Veracruz', '1', '2026-05-21 11:56:03'),
(3, NULL, 1, 'Miguel', 'Santos', '2289998877', 'Banderilla, Veracruz', '1', '2026-05-21 11:56:03');

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
(5, 'Mini Lop', 'Conejo doméstico pequeño', 3);

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
(3, 'Desparasitación', 'Control antiparasitario', 13);

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

--
-- Volcado de datos para la tabla `tb_usuario_movil`
--

INSERT INTO `tb_usuario_movil` (`id`, `nombre`, `apellidos`, `telefono`, `email`, `contrasenia`, `created_at`) VALUES
(1, 'Luis ', 'torres', '2961497362', 'luis@gmail.com', '123456', '2026-05-21 12:25:52');

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
(1, 1, 'JUCA', 'juan carlos', 'aguilar camacho', 'veterinario', 'general', NULL, '2961497729', 'juarlos.0630@gmail.com', '12345', '2026-05-08 16:22:36', 0),
(2, 2, 'jucar', 'juan carlos', 'aguilar ramirez', 'administrador', NULL, NULL, '2961108090', 'elsitioparrillada@gmail.com', '1234', '2026-05-13 20:52:14', 0),
(3, 1, 'valag', 'valeria', 'aguilar', 'veterinario', 'cirugias', '21754183818', '2961108090', 'vetval@gmail.com', '1234', '2026-05-13 21:16:47', 0);

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
(1, 1, 1, NULL, 'E', 850.00, '2026-06-10 10:30:00', '2026-05-21 12:36:38', 1),
(2, 2, 2, NULL, 'T', 450.00, '2026-06-11 12:30:00', '2026-05-21 12:36:38', 2),
(3, 3, 1, NULL, 'T', 320.00, '2026-06-12 09:30:00', '2026-05-21 12:36:38', 1),
(4, 1, 3, 1, 'E', 1200.00, '2026-06-13 15:00:00', '2026-05-21 12:36:38', 1),
(5, 2, 1, NULL, 'T', 600.00, '2026-06-14 17:10:00', '2026-05-21 12:36:38', 3);

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `tb_cierre_caja`
--
ALTER TABLE `tb_cierre_caja`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `tb_citas`
--
ALTER TABLE `tb_citas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `tb_clinicas`
--
ALTER TABLE `tb_clinicas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `tb_detalle_entrada`
--
ALTER TABLE `tb_detalle_entrada`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `tb_detalle_venta`
--
ALTER TABLE `tb_detalle_venta`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de la tabla `tb_diagnosticos`
--
ALTER TABLE `tb_diagnosticos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `tb_diagnostico_medicamento`
--
ALTER TABLE `tb_diagnostico_medicamento`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `tb_entrada`
--
ALTER TABLE `tb_entrada`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `tb_especie`
--
ALTER TABLE `tb_especie`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `tb_expediente`
--
ALTER TABLE `tb_expediente`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `tb_mascotas`
--
ALTER TABLE `tb_mascotas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `tb_raza`
--
ALTER TABLE `tb_raza`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `tb_state`
--
ALTER TABLE `tb_state`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT de la tabla `tb_tipo_cita`
--
ALTER TABLE `tb_tipo_cita`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `tb_usuario_movil`
--
ALTER TABLE `tb_usuario_movil`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `tb_usuario_web`
--
ALTER TABLE `tb_usuario_web`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `tb_venta`
--
ALTER TABLE `tb_venta`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

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
  
SET FOREIGN_KEY_CHECKS = 1;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
