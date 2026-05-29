-- ============================================================
--  PAWTASTIC — DATOS SIMULADOS PARA DEMO / COMERCIAL
--  Insertar DESPUÉS de correr pawtastic_01.sql
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ────────────────────────────────────────────────────────────
-- tb_raza  (ampliar catálogo)
-- ────────────────────────────────────────────────────────────
INSERT INTO `tb_raza` (`id`, `nombre`, `descripcion`, `id_especie`) VALUES
(6,  'Chihuahua',       'Perro pequeño muy expresivo',         1),
(7,  'Golden Retriever','Perro familiar y cariñoso',           1),
(8,  'Bulldog Francés', 'Perro compacto y juguetón',           1),
(9,  'Poodle',          'Perro inteligente de pelo rizado',    1),
(10, 'Shih Tzu',        'Perro de compañía pelo largo',        1),
(11, 'Maine Coon',      'Gato grande y sociable',              2),
(12, 'Bengalí',         'Gato de pelaje atigrado',             2),
(13, 'Angora',          'Gato de pelo largo y sedoso',         2),
(14, 'Holland Lop',     'Conejo de orejas caídas',             3);

-- ────────────────────────────────────────────────────────────
-- tb_tipo_cita  (ampliar)
-- ────────────────────────────────────────────────────────────
INSERT INTO `tb_tipo_cita` (`id`, `nombre`, `descripcion`, `id_State`) VALUES
(4, 'Estética',  'Baño, corte y arreglo',                  13),
(5, 'Urgente',   'Atención de emergencia inmediata',        13),
(6, 'Revisión',  'Chequeo médico preventivo',               13);

-- ────────────────────────────────────────────────────────────
-- tb_propietarios  (agregar más dueños)
-- ────────────────────────────────────────────────────────────
INSERT INTO `tb_propietarios` (`id`, `id_usuario_web`, `id_usuario_movil`, `nombre`, `apellidos`, `telefono`, `direccion`, `estado`, `created_at`) VALUES
(4,  NULL, NULL, 'Valeria',   'Mendoza',    '5554441122', 'Insurgentes Sur 890',          '1', '2026-04-10 09:00:00'),
(5,  NULL, NULL, 'Roberto',   'Sánchez',    '5557778899', 'Av. Universidad 310',          '1', '2026-04-12 10:30:00'),
(6,  NULL, NULL, 'Ana',       'Flores',     '5553334455', 'Calle Morelos 55',             '1', '2026-04-15 11:00:00'),
(7,  NULL, NULL, 'Diego',     'Hernández',  '5556667788', 'Paseo de las Palmas 12',       '1', '2026-04-18 08:45:00'),
(8,  NULL, NULL, 'Sofía',     'Ramírez',    '5551239876', 'Lomas de Chapultepec 403',     '1', '2026-04-20 14:00:00'),
(9,  NULL, NULL, 'Miguel',    'Torres',     '5559990011', 'Calzada del Hueso 78',         '1', '2026-04-22 16:30:00'),
(10, NULL, NULL, 'Fernanda',  'Castillo',   '5552223344', 'Av. Revolución 1600',          '1', '2026-04-25 09:15:00'),
(11, NULL, NULL, 'Andrés',    'Morales',    '5558885566', 'Coyoacán Centro 22',           '1', '2026-05-01 10:00:00');

-- ────────────────────────────────────────────────────────────
-- tb_mascotas  (agregar más mascotas, incluyendo Nessy)
-- ────────────────────────────────────────────────────────────
INSERT INTO `tb_mascotas` (`id`, `id_propietario`, `nombre`, `id_especie`, `id_raza`, `fecha_nacimiento`, `estado`, `created_at`, `descripcion`) VALUES
(4,  4,  'Nessy',   1, 6,  '2023-03-14', '1', '2026-04-10 09:10:00', 'Chihuahua hembra muy activa y curiosa'),
(5,  5,  'Max',     1, 7,  '2021-07-20', '1', '2026-04-12 10:40:00', 'Golden Retriever juguetón'),
(6,  6,  'Luna',    1, 8,  '2022-05-05', '1', '2026-04-15 11:10:00', 'Bulldog Francés tranquila'),
(7,  7,  'Rocky',   1, 2,  '2020-11-30', '1', '2026-04-18 09:00:00', 'Pastor Alemán guardián'),
(8,  8,  'Coco',    2, 11, '2022-09-18', '1', '2026-04-20 14:10:00', 'Maine Coon muy sociable'),
(9,  9,  'Bella',   1, 10, '2023-01-08', '1', '2026-04-22 16:40:00', 'Shih Tzu de pelo largo'),
(10, 10, 'Thor',    1, 9,  '2021-04-22', '1', '2026-04-25 09:20:00', 'Poodle inteligente color beige'),
(11, 11, 'Mochi',   2, 12, '2023-06-11', '1', '2026-05-01 10:10:00', 'Bengalí atigrado muy activo'),
(12, 4,  'Kira',    2, 13, '2022-12-01', '1', '2026-05-05 11:00:00', 'Angora blanca y esponjosa'),
(13, 5,  'Bono',    3, 14, '2024-02-14', '1', '2026-05-10 12:00:00', 'Holland Lop gris adorable');

-- ────────────────────────────────────────────────────────────
-- tb_expediente
-- ────────────────────────────────────────────────────────────
INSERT INTO `tb_expediente` (`id`, `id_mascota`, `historial`, `fecha_apertura`) VALUES
(4,  4,  'Primera visita. Nessy en excelente estado. Vacuna antirrábica aplicada.',            '2026-04-10 09:15:00'),
(5,  5,  'Max presenta sobrepeso leve. Dieta recomendada.',                                    '2026-04-12 10:45:00'),
(6,  6,  'Luna con dermatitis leve. Tratamiento tópico iniciado.',                             '2026-04-15 11:15:00'),
(7,  7,  'Rocky con displasia de cadera en observación.',                                      '2026-04-18 09:05:00'),
(8,  8,  'Coco sano. Desparasitación interna aplicada.',                                       '2026-04-20 14:15:00'),
(9,  9,  'Bella con nudo en pelo. Estética y revisión general.',                               '2026-04-22 16:45:00'),
(10, 10, 'Thor con otitis leve oído izquierdo. Gotas recetadas.',                              '2026-04-25 09:25:00'),
(11, 11, 'Mochi primera consulta. Todo en orden.',                                             '2026-05-01 10:15:00'),
(12, 12, 'Kira con pelaje opaco. Suplemento vitamínico indicado.',                             '2026-05-05 11:05:00'),
(13, 13, 'Bono revisión de dientes. Dieta de heno recomendada.',                              '2026-05-10 12:05:00');

-- ────────────────────────────────────────────────────────────
-- tb_usuario_web  (agregar veterinarios y staff)
-- ────────────────────────────────────────────────────────────
INSERT INTO `tb_usuario_web` (`id`, `id_clinica`, `usuario`, `nombre`, `apellidos`, `tipo_rol`, `especialidad`, `cedula`, `telefono`, `email`, `contrasenia`, `created_at`, `activo`) VALUES
(8,  4, 'dra_sofia',  'Sofía',    'Aguilar',   'veterinario',    'Medicina general',    'VET-2281', '5550001111', 'sofia@pawtastic.mx',   '1234', '2026-04-01 08:00:00', 1),
(9,  4, 'dr_carlos',  'Carlos',   'Mendoza',   'veterinario',    'Cirugía veterinaria', 'VET-4492', '5550002222', 'carlos@pawtastic.mx',  '1234', '2026-04-01 08:00:00', 1),
(10, 4, 'ana_staff',  'Ana',      'Gutiérrez', 'recepcionista',  NULL,                  NULL,       '5550003333', 'ana@pawtastic.mx',     '1234', '2026-04-01 08:00:00', 1);

-- ────────────────────────────────────────────────────────────
-- tb_citas  (citas de hoy + historial reciente)
-- Las citas de HOY usan NOW() para que siempre aparezcan
-- ────────────────────────────────────────────────────────────
INSERT INTO `tb_citas` (`id`, `id_mascota`, `id_usuario_web`, `id_usuario_movil`, `motivo`, `fecha`, `fecha_reg`, `id_tipo_cita`, `id_state`) VALUES
-- Citas de HOY
(1,  4,  8,  NULL, 'Vacunación anual - refuerzo',          DATE_FORMAT(NOW(), '%Y-%m-%d 09:00:00'), NOW(), 1,  14),
(2,  5,  8,  NULL, 'Control de peso y dieta',              DATE_FORMAT(NOW(), '%Y-%m-%d 09:30:00'), NOW(), 6,  13),
(3,  6,  8,  NULL, 'Baño y corte de pelo',                 DATE_FORMAT(NOW(), '%Y-%m-%d 10:00:00'), NOW(), 4,  15),
(4,  7,  9,  NULL, 'Dolor en pata trasera - urgente',      DATE_FORMAT(NOW(), '%Y-%m-%d 10:30:00'), NOW(), 5,  14),
(5,  8,  8,  NULL, 'Desparasitación interna',              DATE_FORMAT(NOW(), '%Y-%m-%d 11:00:00'), NOW(), 3,  13),
(6,  9,  8,  NULL, 'Revisión general semestral',           DATE_FORMAT(NOW(), '%Y-%m-%d 11:30:00'), NOW(), 6,  13),
(7,  10, 9,  NULL, 'Revisión post-operación cadera',       DATE_FORMAT(NOW(), '%Y-%m-%d 12:00:00'), NOW(), 6,  14),
(8,  11, 8,  NULL, 'Primera consulta',                     DATE_FORMAT(NOW(), '%Y-%m-%d 12:30:00'), NOW(), 6,  13),
(9,  12, 8,  NULL, 'Suplemento vitamínico seguimiento',    DATE_FORMAT(NOW(), '%Y-%m-%d 13:00:00'), NOW(), 6,  13),
(10, 13, 9,  NULL, 'Revisión dental',                      DATE_FORMAT(NOW(), '%Y-%m-%d 13:30:00'), NOW(), 6,  13),
-- Citas pasadas (historial)
(11, 4,  8,  NULL, 'Primera consulta Nessy',               '2026-04-10 09:00:00', '2026-04-10 08:50:00', 6,  16),
(12, 5,  8,  NULL, 'Vacunación múltiple',                  '2026-04-12 10:00:00', '2026-04-12 09:50:00', 1,  16),
(13, 6,  8,  NULL, 'Tratamiento dermatitis',               '2026-04-15 11:00:00', '2026-04-15 10:50:00', 6,  16),
(14, 7,  9,  NULL, 'Radiografía cadera',                   '2026-04-18 09:00:00', '2026-04-18 08:50:00', 6,  16),
(15, 1,  8,  NULL, 'Desparasitación Firulais',             '2026-05-05 10:00:00', '2026-05-05 09:50:00', 3,  16),
(16, 2,  8,  NULL, 'Revisión Michi',                       '2026-05-10 11:00:00', '2026-05-10 10:50:00', 6,  16);

-- ────────────────────────────────────────────────────────────
-- tb_diagnosticos
-- ────────────────────────────────────────────────────────────
INSERT INTO `tb_diagnosticos` (`id`, `id_expediente`, `id_cita`, `descripcion`, `tratamiento`, `fecha_registro`) VALUES
(1, 4,  11, 'Nessy en perfecto estado. Sin anomalías detectadas.',                    'Vacuna antirrábica aplicada. Próxima visita en 12 meses.',              '2026-04-10 09:20:00'),
(2, 5,  12, 'Max con sobrepeso de 2 kg. Condición corporal 4/5.',                    'Dieta baja en calorías. Reducir premios. Control mensual.',             '2026-04-12 10:20:00'),
(3, 6,  13, 'Luna presenta dermatitis alérgica leve en zona dorsal.',                'Shampoo medicado 2 veces/semana. Crema betametasona local.',            '2026-04-15 11:20:00'),
(4, 7,  14, 'Rocky con signos de displasia de cadera bilateral grado I.',            'Antiinflamatorio 5 días. Reposo relativo. Radiografía de control.',    '2026-04-18 09:20:00'),
(5, 1,  15, 'Firulais sano. Parásitos internos negativos.',                          'Desparasitante oral aplicado. Repetir en 6 meses.',                    '2026-05-05 10:20:00'),
(6, 2,  16, 'Michi con condición corporal ideal. Coat brillante.',                   'Continuar dieta actual. Revisión anual.',                               '2026-05-10 11:20:00');

-- ────────────────────────────────────────────────────────────
-- tb_diagnostico_medicamento
-- ────────────────────────────────────────────────────────────
INSERT INTO `tb_diagnostico_medicamento` (`id`, `id_diagnostico`, `id_producto`, `dosis`, `frecuencia`, `duracion_dias`) VALUES
(1, 3, 3, '5 ml',  'Cada 3 días en baño',  30),
(2, 4, 4, '1 tab', 'Cada 24 horas',         5),
(3, 5, 7, '1 tab', 'Dosis única',            1);

-- ────────────────────────────────────────────────────────────
-- tb_venta  (ventas pasadas)
-- ────────────────────────────────────────────────────────────
INSERT INTO `tb_venta` (`id`, `id_cita`, `id_usuario_web`, `id_usuario_movil`, `metodo_pago`, `total`, `fecha`, `fecha_reg`, `id_State`) VALUES
(1, 11, 8,  NULL, 'E', 250.00, '2026-04-10 09:30:00', '2026-04-10 09:30:00', 2),
(2, 12, 8,  NULL, 'T', 850.00, '2026-04-12 10:30:00', '2026-04-12 10:30:00', 2),
(3, 13, 8,  NULL, 'E', 120.00, '2026-04-15 11:30:00', '2026-04-15 11:30:00', 2),
(4, 14, 9,  NULL, 'T', 140.00, '2026-04-18 09:30:00', '2026-04-18 09:30:00', 2),
(5, 15, 8,  NULL, 'E', 140.00, '2026-05-05 10:30:00', '2026-05-05 10:30:00', 2),
(6, 16, 8,  NULL, 'T', 180.00, '2026-05-10 11:30:00', '2026-05-10 11:30:00', 2);

-- ────────────────────────────────────────────────────────────
-- tb_detalle_venta
-- ────────────────────────────────────────────────────────────
INSERT INTO `tb_detalle_venta` (`id`, `id_perdida`, `id_venta`, `precio_unitario`, `subtotal`, `id_producto`, `cantidad`) VALUES
(1, NULL, 1, 250.00, 250.00, 4, 1),
(2, NULL, 2, 850.00, 850.00, 1, 1),
(3, NULL, 3, 120.00, 120.00, 3, 1),
(4, NULL, 4, 140.00, 140.00, 7, 1),
(5, NULL, 5, 140.00, 140.00, 7, 1),
(6, NULL, 6, 180.00, 180.00, 2, 1);

-- ────────────────────────────────────────────────────────────
-- tb_entrada  (entradas de inventario)
-- ────────────────────────────────────────────────────────────
INSERT INTO `tb_entrada` (`id`, `fecha`, `total`, `id_State`) VALUES
(1, '2026-04-01 08:00:00', 6500.00, 9),
(2, '2026-05-01 08:00:00', 3200.00, 9);

-- ────────────────────────────────────────────────────────────
-- tb_detalle_entrada
-- ────────────────────────────────────────────────────────────
INSERT INTO `tb_detalle_entrada` (`id`, `id_entrada`, `costo_unitario`, `id_producto`) VALUES
(1, 1, 650.00, 1),
(2, 1, 120.00, 2),
(3, 1,  75.00, 3),
(4, 1, 180.00, 4),
(5, 2,  90.00, 7),
(6, 2, 150.00, 8);

-- ────────────────────────────────────────────────────────────
-- tb_cierre_caja
-- ────────────────────────────────────────────────────────────
INSERT INTO `tb_cierre_caja` (`id`, `id_venta`, `fecha_registro`, `total_tarjeta`, `total_efectivo`, `total_general`, `id_State`) VALUES
(1, 1, '2026-04-10 18:00:00', 0.00,   250.00, 250.00, 8),
(2, 2, '2026-04-12 18:00:00', 850.00,   0.00, 850.00, 8),
(3, 3, '2026-04-15 18:00:00', 0.00,   120.00, 120.00, 8),
(4, 5, '2026-05-05 18:00:00', 0.00,   140.00, 140.00, 8),
(5, 6, '2026-05-10 18:00:00', 180.00,   0.00, 180.00, 8);

-- ────────────────────────────────────────────────────────────
-- tb_alerta_stock  (productos cerca del mínimo)
-- ────────────────────────────────────────────────────────────
INSERT INTO `tb_alerta_stock` (`id`, `id_producto`, `existencia`, `stock_minimo`, `fecha`, `resuelta`) VALUES
(1, 4, 10, 3, '2026-05-20 08:00:00', 0),
(2, 6,  8, 2, '2026-05-20 08:00:00', 0);

-- ────────────────────────────────────────────────────────────
-- tb_bitacora  (actividad reciente del sistema)
-- ────────────────────────────────────────────────────────────
INSERT INTO `tb_bitacora` (`id`, `fecha_hora`, `id_usuario_web`, `modulo`, `detalle`) VALUES
(1,  '2026-04-10 09:35:00', 8,  'Citas',      'Cita registrada para Nessy - Vacunación'),
(2,  '2026-04-12 10:35:00', 8,  'Citas',      'Cita registrada para Max - Vacunación múltiple'),
(3,  '2026-04-15 11:35:00', 8,  'Ventas',     'Venta #3 registrada - Shampoo Antipulgas'),
(4,  '2026-04-18 09:35:00', 9,  'Citas',      'Cita urgente Rocky - Radiografía cadera'),
(5,  '2026-05-05 10:35:00', 8,  'Inventario', 'Entrada de inventario registrada'),
(6,  '2026-05-10 11:35:00', 8,  'Ventas',     'Venta #6 registrada - Arena para Gato'),
(7,  '2026-05-22 09:00:00', 6,  'Usuarios',   'Usuario luz creado por administrador'),
(8,  '2026-05-28 08:00:00', 8,  'Sesión',     'Inicio de sesión dra_sofia'),
(9,  '2026-05-28 08:05:00', 10, 'Sesión',     'Inicio de sesión ana_staff'),
(10, '2026-05-28 08:10:00', 9,  'Sesión',     'Inicio de sesión dr_carlos');

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- FIN DEL SCRIPT
-- Nessy (Chihuahua, id=4) tiene cita HOY a las 09:00
-- Usuarios nuevos: dra_sofia / 1234 | dr_carlos / 1234
-- ============================================================
