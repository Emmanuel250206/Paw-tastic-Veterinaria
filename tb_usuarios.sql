-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 26-04-2026 a las 10:24:43
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
-- Base de datos: `pawtastic`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_usuarios`
--

CREATE TABLE `tb_usuarios` (
  `id` int(11) NOT NULL,
  `usuario` varchar(50) DEFAULT NULL,
  `nombre` varchar(50) DEFAULT NULL,
  `apellidos` varchar(100) DEFAULT NULL,
  `tipo_rol` varchar(50) DEFAULT NULL,
  `cedula` varchar(20) DEFAULT NULL,
  `especialidad` varchar(100) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `contrasenia` varchar(10) DEFAULT NULL,
  `cambio_usuario` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tb_usuarios`
--

INSERT INTO `tb_usuarios` (`id`, `usuario`, `nombre`, `apellidos`, `tipo_rol`, `cedula`, `especialidad`, `telefono`, `email`, `contrasenia`, `cambio_usuario`) VALUES
(1, 'ehernandez', 'Emmanuel', 'Hernandez', 'Veterinario', '151512121', 'General', '12345', 'emma@gmail.com', '1010', 1),
(2, 'jpancho', 'Juan', 'Aguilar', 'veterinario', NULL, 'General', '12345', 'nuevo@vet.com', '3006', 1),
(4, 'vguzman', 'Valeria', 'Guzman', 'Staff', NULL, 'General', '12345', 'valeria@gmail.com', '2020', 1),
(7, 'llopez', 'Luz', 'Lopez', 'Veterinario', '123255', 'Cirujana', '225156262', 'lolool@gmail.com', '2018', 1),
(8, 'lfalfan', 'Luisa', 'Falfan Lopez', 'Staff', '', 'Limpieza', '202021515', 'falfan@gmail.com', '3030', 0),
(9, 'ksanchez', 'Kaito', 'Sanchez Perez', 'Veterinario', '125156262', 'Revisor de Patas', '22255122322', 'kaito@gmail.com', '5050', 0),
(10, 'rpancho', 'robot', 'pancho', 'veterinario', '115152', 'sepalabola', '2252525', 'pancjho@gmail.com', '7777', 0);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `tb_usuarios`
--
ALTER TABLE `tb_usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `usuario` (`usuario`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `tb_usuarios`
--
ALTER TABLE `tb_usuarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
