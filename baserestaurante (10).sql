-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 17-07-2025 a las 01:31:03
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
-- Base de datos: `baserestaurante`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `contadores_pedidos`
--

CREATE TABLE `contadores_pedidos` (
  `id_contador` varchar(25) NOT NULL,
  `serie_actual` char(1) NOT NULL,
  `numero_actual` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `contadores_pedidos`
--

INSERT INTO `contadores_pedidos` (`id_contador`, `serie_actual`, `numero_actual`) VALUES
('PEDIDO_DELIVERY', 'A', 9);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `delivery`
--

CREATE TABLE `delivery` (
  `Pedido Nro` varchar(100) NOT NULL,
  `Nombre de Cliente` varchar(150) NOT NULL,
  `DNI` int(8) NOT NULL,
  `Telefono del Cliente` int(15) NOT NULL,
  `Correo del Cliente` varchar(200) NOT NULL,
  `Direccion de Entrega` varchar(500) NOT NULL,
  `Distrito` varchar(200) NOT NULL,
  `Producto` varchar(500) NOT NULL,
  `Cant.` int(12) NOT NULL,
  `P.Unit.` decimal(10,2) NOT NULL,
  `SubTotal` decimal(10,2) NOT NULL,
  `Costo de Envio` decimal(10,2) NOT NULL,
  `Total a pagar` decimal(10,2) NOT NULL,
  `Metodo de Pago` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalle_pedidos`
--

CREATE TABLE `detalle_pedidos` (
  `id` int(11) NOT NULL,
  `id_pedido` int(11) NOT NULL,
  `id_producto` int(11) NOT NULL,
  `nombre_producto` varchar(255) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `subtotal_item` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `detalle_pedidos`
--

INSERT INTO `detalle_pedidos` (`id`, `id_pedido`, `id_producto`, `nombre_producto`, `cantidad`, `precio_unitario`, `subtotal_item`) VALUES
(1, 1, 10, 'Solterito Arequipeño', 1, 23.00, 23.00),
(2, 1, 24, 'Maracuyá Sour', 1, 24.00, 24.00),
(3, 1, 30, 'Picarones con Miel de Chancaca', 1, 20.00, 20.00),
(4, 1, 33, 'Mazamorra Morada', 5, 15.00, 75.00),
(5, 2, 17, 'Carapulcra con Sopa Seca (Manchapecho)', 1, 42.00, 42.00),
(6, 2, 33, 'Mazamorra Morada', 1, 15.00, 15.00),
(7, 2, 32, 'Torta de Chocolate Húmeda', 1, 22.00, 22.00),
(8, 2, 31, 'Suspiro a la Limeña', 1, 18.00, 18.00),
(9, 2, 30, 'Picarones con Miel de Chancaca', 1, 20.00, 20.00),
(10, 2, 29, 'Emoliente Caliente', 1, 10.00, 10.00),
(11, 2, 28, 'Cerveza Nacional (Botella)', 1, 12.00, 12.00),
(12, 2, 26, 'Inca Kola Personal', 1, 7.00, 7.00),
(13, 2, 27, 'Agua Mineral sin Gas (Botella)', 1, 5.00, 5.00),
(14, 2, 24, 'Maracuyá Sour', 1, 24.00, 24.00),
(15, 2, 14, 'Seco de Res con Frijoles y Arroz', 1, 45.00, 45.00),
(16, 2, 9, 'Ocopa Arequipeña', 2, 22.00, 44.00),
(17, 2, 7, 'Choros a la Chalaca', 1, 24.00, 24.00),
(18, 3, 18, 'Cau Cau Criollo', 1, 35.00, 35.00),
(19, 4, 29, 'Emoliente Caliente', 1, 10.00, 10.00),
(20, 5, 8, 'Tamal Criollo de Pollo o Cerdo', 1, 18.00, 18.00),
(21, 5, 31, 'Suspiro a la Limeña', 1, 18.00, 18.00),
(22, 5, 26, 'Inca Kola Personal', 1, 7.00, 7.00),
(23, 6, 21, 'Juane de Gallina', 1, 36.00, 36.00),
(24, 6, 35, 'Crema Volteada', 1, 17.00, 17.00),
(25, 7, 9, 'Ocopa Arequipeña', 1, 22.00, 22.00),
(26, 7, 21, 'Juane de Gallina', 1, 36.00, 36.00),
(27, 7, 20, 'Rocoto Relleno', 1, 44.00, 44.00),
(28, 8, 13, 'Arroz con Pato a la Chiclayana', 1, 52.00, 52.00),
(29, 8, 22, 'Pachamanca (Porción Personal)', 1, 65.00, 65.00),
(30, 9, 12, 'Ají de Gallina', 1, 40.00, 40.00),
(31, 9, 22, 'Pachamanca (Porción Personal)', 1, 65.00, 65.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedidos`
--

CREATE TABLE `pedidos` (
  `id` int(11) NOT NULL,
  `numero_pedido` varchar(50) NOT NULL,
  `nombre_cliente` varchar(255) NOT NULL,
  `dni_cliente` varchar(20) DEFAULT NULL,
  `telefono_cliente` varchar(20) NOT NULL,
  `correo_cliente` varchar(255) DEFAULT NULL,
  `direccion_envio` varchar(255) NOT NULL,
  `distrito_envio` varchar(100) NOT NULL,
  `ciudad_envio` varchar(100) DEFAULT NULL,
  `referencia_envio` text DEFAULT NULL,
  `telefono_entrega` varchar(20) NOT NULL,
  `costo_envio` decimal(10,2) DEFAULT 0.00,
  `subtotal_pedido` decimal(10,2) NOT NULL,
  `total_pedido` decimal(10,2) NOT NULL,
  `metodo_pago` varchar(50) NOT NULL,
  `estado_pedido` varchar(50) NOT NULL,
  `fecha_pedido` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `pedidos`
--

INSERT INTO `pedidos` (`id`, `numero_pedido`, `nombre_cliente`, `dni_cliente`, `telefono_cliente`, `correo_cliente`, `direccion_envio`, `distrito_envio`, `ciudad_envio`, `referencia_envio`, `telefono_entrega`, `costo_envio`, `subtotal_pedido`, `total_pedido`, `metodo_pago`, `estado_pedido`, `fecha_pedido`) VALUES
(1, 'A001', 'Juan', '444223351', '978471354', 'juan@utp.edu.pe', 'Av. Sin Calle', 'Lince', 'Lima', 'Km 23.4', '978471354', 4.00, 142.00, 146.00, 'Plin', 'Pendiente', '2025-07-11 14:23:41'),
(2, 'A002', 'Cliente Uno', '22222', '999888777', 'admin@restaurante.com', 'chorrillos', 'Surco', 'chorrillos', 'curva', '999888777', 7.00, 288.00, 295.00, 'Yape', 'Pendiente', '2025-07-14 04:13:51'),
(3, 'A003', 'cliente uno', '1212', '999888777', 'admin@restaurante.com', 'asd', 'Surco', 'asd', 'asd', '999888777', 7.00, 35.00, 42.00, 'Efectivo', 'Pendiente', '2025-07-14 04:16:56'),
(4, 'A004', 'cliente', '123', '32', '312', '2321', 'Pueblo Libre', '1231', 'asd', '32', 10.00, 10.00, 20.00, 'Efectivo', 'Pendiente', '2025-07-14 04:23:13'),
(5, 'A005', 'Cliente Uno', '1232321', '987654321', 'cliente@ejemplo.com', 'chorrillos', 'Miraflores', 'chorrillos', 'chorrillos', '987654321', 5.00, 43.00, 48.00, 'Efectivo', 'Pendiente', '2025-07-14 04:29:08'),
(6, 'A006', 'Cliente Uno', '222', '987654321', 'cliente@ejemplo.com', 'sdfd', 'Jesus Maria', 'fsd', 'sdf', '987654321', 10.00, 53.00, 63.00, 'Plin', 'Pendiente', '2025-07-14 23:24:24'),
(7, 'A007', 'Cliente Uno', 'zcxxcz', '987654321', 'cliente@ejemplo.com', 'xczxczx', 'Lince', 'zzz', 'zxczx', '987654321', 4.00, 102.00, 106.00, 'Plin', 'Pendiente', '2025-07-14 23:25:00'),
(8, 'A008', 'Mary', '222', '999888777', 'maryH@correo.com', 'as', 'Pueblo Libre', 'das', 'dasads', '999888777', 10.00, 117.00, 127.00, 'POS', 'Pendiente', '2025-07-14 23:30:43'),
(9, 'A009', 'Cliente Uno', '1111', '987654321', 'cliente@ejemplo.com', 'CHORRILLOS', 'Miraflores', 'CHORRILLOS', 'CHORRILLOS', '987654321', 5.00, 105.00, 110.00, 'Plin', 'Pendiente', '2025-07-16 04:39:55');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `platos`
--

CREATE TABLE `platos` (
  `plato` varchar(100) NOT NULL,
  `descripcion` varchar(150) NOT NULL,
  `precio` decimal(10,2) NOT NULL,
  `tipo` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `platos`
--

INSERT INTO `platos` (`plato`, `descripcion`, `precio`, `tipo`) VALUES
('aji de gallina', 'aji gallina', 25.00, 'General'),
('aji de gallinaaaa', 'aa', 222.00, 'General'),
('ARROZ CON POLLO', 'ARROZ Y POLLO', 22.00, 'General'),
('arroz con pollo ', 'arroz', 22.00, 'General'),
('Ceviche peruano', 'Pescado marinado en limón fresco.', 30.00, 'General'),
('Pollo a la brasa', 'Pollo asado con especias peruanas.', 50.00, 'General'),
('Lomo saltado', 'Carne salteada con papas fritas.', 20.00, 'General'),
('Anticuchos', 'Brochetas de corazón sazonado delicioso.', 25.00, 'General'),
('Rocoto relleno', 'Rocoto picante con carne horneada.', 25.00, 'General'),
('Tacu tacu', 'Frijoles y arroz fritos juntos.', 25.00, 'General'),
('Carapulcra', 'Guiso de papa seca especiada.', 22.00, 'General'),
('arroz con pollo ', 'arroz', 22.00, 'General'),
('Ceviche peruano', 'Pescado marinado en limón fresco.', 30.00, 'General'),
('Pollo a la brasa', 'Pollo asado con especias peruanas.', 50.00, 'General'),
('Lomo saltado', 'Carne salteada con papas fritas.', 20.00, 'General'),
('Anticuchos', 'Brochetas de corazón sazonado delicioso.', 25.00, 'General'),
('Rocoto relleno', 'Rocoto picante con carne horneada.', 25.00, 'General'),
('Tacu tacu', 'Frijoles y arroz fritos juntos.', 25.00, 'General'),
('Carapulcra', 'Guiso de papa seca especiada.', 22.00, 'General'),
('aji de gallina', 'aji gallina', 25.00, 'General'),
('aji de gallinaaaa', 'aa', 222.00, 'General'),
('ARROZ CON POLLO', 'ARROZ Y POLLO', 22.00, 'General'),
('arroz con pollo ', 'arroz', 22.00, 'General'),
('Ceviche peruano', 'Pescado marinado en limón fresco.', 30.00, 'General'),
('Pollo a la brasa', 'Pollo asado con especias peruanas.', 50.00, 'General'),
('Lomo saltado', 'Carne salteada con papas fritas.', 20.00, 'General'),
('Anticuchos', 'Brochetas de corazón sazonado delicioso.', 25.00, 'General'),
('Rocoto relleno', 'Rocoto picante con carne horneada.', 25.00, 'General'),
('Tacu tacu', 'Frijoles y arroz fritos juntos.', 25.00, 'General'),
('Carapulcra', 'Guiso de papa seca especiada.', 22.00, 'General');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `productos`
--

CREATE TABLE `productos` (
  `id` int(11) NOT NULL,
  `tipo` varchar(20) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `precio` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `productos`
--

INSERT INTO `productos` (`id`, `tipo`, `nombre`, `descripcion`, `precio`) VALUES
(1, 'entrada', 'Ceviche mixto', 'Pescado y mariscos frescos', 25.00),
(2, 'bebida', 'Chicha morada', 'Refresco tradicional peruano', 8.00),
(3, 'entrada', 'Ceviche Clásico de Pescado', 'Trozos de pescado fresco marinados en jugo de limón, ají limo y cebolla roja. Acompañado de camote glaseado, choclo y cancha serrana.', 32.00),
(4, 'entrada', 'Papa a la Huancaína', 'Papas amarillas cocidas, bañadas en una cremosa salsa de ají amarillo, queso fresco, leche y galletas. Decorada con huevo duro y aceituna botija.', 20.00),
(5, 'entrada', 'Causa Rellena de Pollo y Palta', 'Suave masa de papa amarilla prensada, sazonada con ají amarillo y limón, rellena de pollo deshilachado con mayonesa y láminas de palta.', 26.00),
(6, 'entrada', 'Anticuchos de Corazón con Choclo', 'Brochetas de corazón de res marinadas en ají panca, vinagre y especias, asadas a la parrilla. Servidas con papa dorada, choclo y salsa de ají.', 28.00),
(7, 'entrada', 'Choros a la Chalaca', 'Mejillones frescos cocidos y servidos en su concha, cubiertos con una salsa picada de cebolla, tomate, ají limo, choclo y culantro.', 24.00),
(8, 'entrada', 'Tamal Criollo de Pollo o Cerdo', 'Masa de maíz sazonada envuelta en hojas de plátano, rellena de pollo o cerdo, aceituna y huevo. Acompañado de salsa criolla.', 18.00),
(9, 'entrada', 'Ocopa Arequipeña', 'Papas cocidas bañadas en una salsa a base de huacatay, ají amarillo, maní y queso fresco, decorada con huevo y aceituna.', 22.00),
(10, 'entrada', 'Solterito Arequipeño', 'Ensalada fresca con habas, choclo, queso fresco, cebolla, tomate, rocoto y aceitunas, aderezada con vinagre y aceite.', 23.00),
(11, 'plato_fuerte', 'Lomo Saltado Clásico', 'Trozos de lomo fino de res salteados al wok con cebolla roja, tomate, ají amarillo y un toque de sillao y vinagre. Acompañado de papas fritas crujientes y arroz blanco.', 48.00),
(12, 'plato_fuerte', 'Ají de Gallina', 'Pechuga de gallina deshilachada en una delicada y cremosa salsa de ají amarillo, pan remojado en leche, y pecanas. Servido con papa amarilla, huevo duro y aceituna.', 40.00),
(13, 'plato_fuerte', 'Arroz con Pato a la Chiclayana', 'Jugoso pato cocinado lentamente en cerveza negra y culantro, servido sobre un sabroso arroz verde con arvejas y pimiento.', 52.00),
(14, 'plato_fuerte', 'Seco de Res con Frijoles y Arroz', 'Tierno asado de tira o carne de res estofado en salsa de culantro y chicha de jora, acompañado de frijoles canario cremosos y arroz blanco.', 45.00),
(15, 'plato_fuerte', 'Tacu Tacu con Lomo al Jugo', 'Crujiente y sabroso tacu tacu de frijoles canario y arroz, montado con un jugoso lomo saltado.', 55.00),
(16, 'plato_fuerte', 'Pescado a lo Macho', 'Filete de pescado frito o a la plancha, cubierto con una abundante salsa de mariscos (camarones, conchas, calamar) en base de ají panca y vino blanco.', 58.00),
(17, 'plato_fuerte', 'Carapulcra con Sopa Seca (Manchapecho)', 'Plato tradicional que combina la carapulcra (guiso de papa seca con cerdo y ají panca) con la sopa seca (tallarines aderezados).', 42.00),
(18, 'plato_fuerte', 'Cau Cau Criollo', 'Guiso de mondongo (callos) cortado en cubos pequeños, cocido con papas, arvejas, zanahoria y hierbabuena en una salsa de ají amarillo y palillo.', 35.00),
(19, 'plato_fuerte', 'Adobo Arequipeño', 'Cerdo marinado y cocido lentamente en chicha de jora, ají panca, vinagre y especias. Servido con pan de tres puntas.', 46.00),
(20, 'plato_fuerte', 'Rocoto Relleno', 'Rocotos horneados rellenos de carne molida, maní, pasas y especias, cubiertos con queso derretido. Acompañado de pastel de papa.', 44.00),
(21, 'plato_fuerte', 'Juane de Gallina', 'Masa de arroz sazonada con especias y gallina, envuelta en hojas de bijao y cocida. Típico de la selva.', 36.00),
(22, 'plato_fuerte', 'Pachamanca (Porción Personal)', 'Selección de carnes (pollo, cerdo, res) y tubérculos (papa, camote, habas, choclo) cocidos bajo tierra con hierbas aromáticas (versión adaptada).', 65.00),
(23, 'bebida', 'Chicha Morada (Jarra 1L)', 'Refresco tradicional a base de maíz morado, piña, manzana, canela y clavo.', 18.00),
(24, 'bebida', 'Maracuyá Sour', 'Cóctel refrescante y emblemático a base de pisco peruano, jugo de maracuyá fresco, jarabe de goma y clara de huevo.', 24.00),
(25, 'bebida', 'Pisco Sour Clásico', 'El cóctel bandera del Perú, preparado con pisco quebranta, jugo de limón fresco, jarabe de goma, clara de huevo y amargo de angostura.', 22.00),
(26, 'bebida', 'Inca Kola Personal', 'Gaseosa nacional de sabor único y dulce.', 7.00),
(27, 'bebida', 'Agua Mineral sin Gas (Botella)', 'Agua de manantial embotellada.', 5.00),
(28, 'bebida', 'Cerveza Nacional (Botella)', 'Selección de cervezas peruanas.', 12.00),
(29, 'bebida', 'Emoliente Caliente', 'Bebida tradicional caliente a base de hierbas, cebada y limón.', 10.00),
(30, 'postre', 'Picarones con Miel de Chancaca', 'Deliciosos buñuelos fritos en forma de anillo, hechos con zapallo y camote, bañados en abundante miel de chancaca aromatizada.', 20.00),
(31, 'postre', 'Suspiro a la Limeña', 'Dulce de manjar blanco cremoso a base de leche evaporada y yemas, coronado con un suave merengue al oporto y canela en polvo.', 18.00),
(32, 'postre', 'Torta de Chocolate Húmeda', 'Clásica torta de chocolate con bizcocho húmedo y generosa capa de fudge casero.', 22.00),
(33, 'postre', 'Mazamorra Morada', 'Postre tradicional a base de maíz morado, frutas secas y frescas, endulzado y aromatizado con canela y clavo.', 15.00),
(34, 'postre', 'Arroz con Leche Clásico', 'Cremoso arroz cocido en leche, endulzado con azúcar y aromatizado con canela y cáscara de limón.', 15.00),
(35, 'postre', 'Crema Volteada', 'Clásico flan peruano con una base de caramelo líquido.', 17.00),
(36, 'postre', 'Alfajores de Maicena', 'Delicadas galletas de maicena unidas con manjar blanco y espolvoreadas con azúcar impalpable.', 14.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reservas`
--

CREATE TABLE `reservas` (
  `id` int(11) NOT NULL,
  `mesa` varchar(50) DEFAULT NULL,
  `fecha` date NOT NULL,
  `hora` time NOT NULL,
  `cliente` varchar(100) NOT NULL,
  `capacidad` int(11) NOT NULL,
  `sala` varchar(50) DEFAULT NULL,
  `codigo_reserva` varchar(20) DEFAULT NULL,
  `metodo_pago` varchar(20) DEFAULT NULL,
  `estado_pago` varchar(20) DEFAULT 'Pendiente',
  `estacionamiento` int(11) DEFAULT 0,
  `Precio` decimal(65,0) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `reservas`
--

INSERT INTO `reservas` (`id`, `mesa`, `fecha`, `hora`, `cliente`, `capacidad`, `sala`, `codigo_reserva`, `metodo_pago`, `estado_pago`, `estacionamiento`, `Precio`) VALUES
(56, 'Mesa P1', '2025-07-16', '01:00:00', 'Cliente Uno', 1, 'sala principal', 'RSV-NIZ-5720', 'Plin', 'Pagado', 0, 54),
(57, 'Mesa P8', '2025-07-16', '02:00:00', 'cliente 1', 3, 'sala principal', 'RSV-LKJ-6085', 'Plin', 'Pagado', 0, 59);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `salas`
--

CREATE TABLE `salas` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `tipo` varchar(50) NOT NULL,
  `capacidad` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `salas`
--

INSERT INTO `salas` (`id`, `nombre`, `tipo`, `capacidad`) VALUES
(1, 'Sala Principal', 'Principal', 40),
(2, 'Terraza', 'Secundaria', 25),
(15, 'Terraza', 'Principal', 19),
(16, 'anthony', 'Secundaria', 11),
(17, 'carlos', 'Principal', 20);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL,
  `nombre_completo` varchar(100) DEFAULT NULL,
  `correo` varchar(100) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  `rol` varchar(20) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `nombre_completo`, `correo`, `contrasena`, `rol`, `telefono`) VALUES
(1, 'Admin Principal', 'admin@restaurante.com', 'admin123', 'admin', '999888777'),
(2, 'Cliente Uno', 'cliente@ejemplo.com', 'cliente123', 'cliente', '987654321'),
(3, 'Carlos Rueda', 'carlosRu@restaurante.com', 'carlos123', 'administrador', NULL),
(4, 'Jonatan Santur', 'jonatanSa@resturante.com', 'jonatan123', 'administrador', NULL),
(5, 'Jhon Martinez', 'jhonMa@restaurante.com', 'jhon123', 'administrador', NULL),
(6, 'Johan Gonzales', 'johanGo@restaurante.com', 'jhan123', 'administrador', NULL),
(7, 'Antony CCenhua', 'antonyCC@restaurante.com', 'antony123', 'administrador', '728388338'),
(8, 'Omar Tito', 'omarTi@restaurante.com', 'omar123', 'administrador', '82838398'),
(9, 'Mary', 'maryH@correo.com', 'mary123', 'cliente', '999888777'),
(10, 'xxxx', 'asdfgfds@correo.com', 'xxx123', 'admin', '999888777'),
(17, 'juan martinez', 'juanmartinez1@utp.edu.pe', 'juanmartinez', 'cliente', '988325633'),
(18, 'pablo adauto', 'pabloadauto@utp.edu.pe', 'pabloadautoutp1', 'cliente', '933665476'),
(19, 'anthony adrian', 'anthonyadrian@utp.edu.pe', 'anthonyadrian32', 'cliente', '966323854'),
(20, 'jonatan meza', 'jonatanmeza@utp.edu.pe', 'jonatanmeza223', 'cliente', '922654326');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `contadores_pedidos`
--
ALTER TABLE `contadores_pedidos`
  ADD PRIMARY KEY (`id_contador`);

--
-- Indices de la tabla `detalle_pedidos`
--
ALTER TABLE `detalle_pedidos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_pedido` (`id_pedido`),
  ADD KEY `id_producto` (`id_producto`);

--
-- Indices de la tabla `pedidos`
--
ALTER TABLE `pedidos`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `numero_pedido` (`numero_pedido`);

--
-- Indices de la tabla `productos`
--
ALTER TABLE `productos`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `reservas`
--
ALTER TABLE `reservas`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `codigo_reserva` (`codigo_reserva`);

--
-- Indices de la tabla `salas`
--
ALTER TABLE `salas`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `correo` (`correo`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `detalle_pedidos`
--
ALTER TABLE `detalle_pedidos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT de la tabla `pedidos`
--
ALTER TABLE `pedidos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de la tabla `productos`
--
ALTER TABLE `productos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT de la tabla `reservas`
--
ALTER TABLE `reservas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=58;

--
-- AUTO_INCREMENT de la tabla `salas`
--
ALTER TABLE `salas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `detalle_pedidos`
--
ALTER TABLE `detalle_pedidos`
  ADD CONSTRAINT `detalle_pedidos_ibfk_1` FOREIGN KEY (`id_pedido`) REFERENCES `pedidos` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `detalle_pedidos_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
