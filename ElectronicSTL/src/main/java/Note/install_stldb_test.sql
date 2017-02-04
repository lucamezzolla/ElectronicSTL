USE stldb_test;
-- phpMyAdmin SQL Dump
-- version 4.6.6
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Creato il: Feb 04, 2017 alle 14:47
-- Versione del server: 5.5.47-MariaDB
-- Versione PHP: 5.6.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `stldb_test`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `stl_customers`
--

CREATE TABLE `stl_customers` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `stl_defects`
--

CREATE TABLE `stl_defects` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `simulator_id` int(11) DEFAULT NULL,
  `page_id` int(11) DEFAULT NULL,
  `defect_type_id` int(11) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `corrective_action` varchar(100) NOT NULL DEFAULT ' ',
  `datetime_start` datetime DEFAULT NULL,
  `datetime_end` datetime DEFAULT NULL,
  `ata_syscode` int(11) NOT NULL DEFAULT '0',
  `ata_subcode` int(11) NOT NULL DEFAULT '0',
  `ata_description` varchar(45) NOT NULL DEFAULT ' ',
  `solved` tinyint(4) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `stl_defect_type`
--

CREATE TABLE `stl_defect_type` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `stl_defect_type`
--

INSERT INTO `stl_defect_type` (`id`, `name`) VALUES
(1, 'Discrepancy'),
(2, 'Interruption'),
(3, 'Snag');

-- --------------------------------------------------------

--
-- Struttura della tabella `stl_device_performance`
--

CREATE TABLE `stl_device_performance` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `stl_device_performance`
--

INSERT INTO `stl_device_performance` (`id`, `name`) VALUES
(1, 'Unsatisfactory'),
(2, 'Poor'),
(3, 'Acceptable'),
(4, 'Good'),
(5, 'Excellent');

-- --------------------------------------------------------

--
-- Struttura della tabella `stl_frequent_users`
--

CREATE TABLE `stl_frequent_users` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(70) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `stl_levels`
--

CREATE TABLE `stl_levels` (
  `id` tinyint(4) NOT NULL,
  `name` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `stl_levels`
--

INSERT INTO `stl_levels` (`id`, `name`) VALUES
(1, 'User'),
(2, 'Device User'),
(3, 'Technical'),
(4, 'Administrator'),
(5, 'Super Administrator');

-- --------------------------------------------------------

--
-- Struttura della tabella `stl_maintenance_type`
--

CREATE TABLE `stl_maintenance_type` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `stl_maintenance_type`
--

INSERT INTO `stl_maintenance_type` (`id`, `name`) VALUES
(1, 'Preventive Maintenance'),
(2, 'Scheduled Maintenance'),
(3, 'Unscheduled Maintenance');

-- --------------------------------------------------------

--
-- Struttura della tabella `stl_pages`
--

CREATE TABLE `stl_pages` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `simulator_id` int(11) DEFAULT NULL,
  `date_page` datetime DEFAULT NULL,
  `sched_datetime_start` varchar(30) DEFAULT NULL,
  `sched_datetime_end` varchar(30) DEFAULT NULL,
  `sched_datetime_total` varchar(30) DEFAULT NULL,
  `actual_datetime_start` varchar(30) DEFAULT NULL,
  `actual_datetime_end` varchar(30) DEFAULT NULL,
  `actual_datetime_total` varchar(30) DEFAULT NULL,
  `ttl_start` varchar(30) DEFAULT NULL,
  `ttl_end` varchar(30) DEFAULT NULL,
  `ttl_total` varchar(30) DEFAULT NULL,
  `customer_id` int(11) DEFAULT NULL,
  `training_type_id` int(11) DEFAULT NULL,
  `session_content` varchar(100) DEFAULT NULL,
  `maintenance_type_id` int(11) DEFAULT NULL,
  `device_users` varchar(150) DEFAULT NULL,
  `students` varchar(150) DEFAULT NULL,
  `observers` varchar(150) DEFAULT NULL,
  `maintenance_called` tinyint(4) DEFAULT NULL,
  `training_completed` tinyint(4) DEFAULT NULL,
  `interruptions` int(11) DEFAULT NULL,
  `lost_training_time` int(11) DEFAULT NULL,
  `device_performance_id` int(11) DEFAULT NULL,
  `remarks` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `stl_periodical_test`
--

CREATE TABLE `stl_periodical_test` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `stl_periodical_test`
--

INSERT INTO `stl_periodical_test` (`id`, `name`) VALUES
(1, 'QTG'),
(2, 'Flyout'),
(3, 'NAV DATA Update'),
(4, 'IOS DATA Update'),
(5, 'Video Calibration'),
(6, 'Transport Delay');

-- --------------------------------------------------------

--
-- Struttura della tabella `stl_periodical_test_values`
--

CREATE TABLE `stl_periodical_test_values` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `page_id` int(11) DEFAULT NULL,
  `periodical_test_id` int(11) DEFAULT NULL,
  `number` varchar(15) DEFAULT NULL,
  `year` varchar(15) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `stl_simulators`
--

CREATE TABLE `stl_simulators` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(30) DEFAULT NULL,
  `ttl` tinyint(1) DEFAULT '0',
  `actual_ttl` varchar(30) DEFAULT '0.0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `stl_simulator_status`
--

CREATE TABLE `stl_simulator_status` (
  `id` tinyint(4) NOT NULL,
  `name` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `stl_simulator_status`
--

INSERT INTO `stl_simulator_status` (`id`, `name`) VALUES
(1, 'STD Down'),
(2, 'Available with mitigations'),
(3, 'Available');

-- --------------------------------------------------------

--
-- Struttura della tabella `stl_simulator_status_values`
--

CREATE TABLE `stl_simulator_status_values` (
  `id` bigint(20) NOT NULL,
  `simulator_id` int(11) DEFAULT NULL,
  `simulator_status_id` int(11) DEFAULT NULL,
  `datetime_start` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `stl_technical_items`
--

CREATE TABLE `stl_technical_items` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `technical_item_id` int(11) NOT NULL,
  `simulator_id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `value` mediumblob NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `stl_training_type`
--

CREATE TABLE `stl_training_type` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `stl_training_type`
--

INSERT INTO `stl_training_type` (`id`, `name`) VALUES
(1, 'Training'),
(2, 'Check'),
(3, 'Engineering'),
(4, 'Maintenance'),
(5, 'Regualar Authority'),
(6, 'Other (ex. Demo)'),
(7, 'Compliance Monitoring'),
(8, 'Safety Monitoring'),
(9, 'Periodical Test');

-- --------------------------------------------------------

--
-- Struttura della tabella `stl_users`
--

CREATE TABLE `stl_users` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(40) NOT NULL,
  `lastname` varchar(40) NOT NULL,
  `phone` varchar(40) NOT NULL,
  `email` varchar(40) NOT NULL,
  `password` varchar(40) NOT NULL,
  `level_id` tinyint(4) NOT NULL DEFAULT '1',
  `photo` blob
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `stl_customers`
--
ALTER TABLE `stl_customers`
  ADD UNIQUE KEY `id` (`id`);

--
-- Indici per le tabelle `stl_defects`
--
ALTER TABLE `stl_defects`
  ADD UNIQUE KEY `id` (`id`);

--
-- Indici per le tabelle `stl_defect_type`
--
ALTER TABLE `stl_defect_type`
  ADD UNIQUE KEY `id` (`id`);

--
-- Indici per le tabelle `stl_device_performance`
--
ALTER TABLE `stl_device_performance`
  ADD UNIQUE KEY `id` (`id`);

--
-- Indici per le tabelle `stl_frequent_users`
--
ALTER TABLE `stl_frequent_users`
  ADD UNIQUE KEY `id` (`id`);

--
-- Indici per le tabelle `stl_maintenance_type`
--
ALTER TABLE `stl_maintenance_type`
  ADD UNIQUE KEY `id` (`id`);

--
-- Indici per le tabelle `stl_pages`
--
ALTER TABLE `stl_pages`
  ADD UNIQUE KEY `id` (`id`),
  ADD UNIQUE KEY `datepage_index` (`simulator_id`,`date_page`);

--
-- Indici per le tabelle `stl_periodical_test`
--
ALTER TABLE `stl_periodical_test`
  ADD UNIQUE KEY `id` (`id`);

--
-- Indici per le tabelle `stl_periodical_test_values`
--
ALTER TABLE `stl_periodical_test_values`
  ADD UNIQUE KEY `id` (`id`);

--
-- Indici per le tabelle `stl_simulators`
--
ALTER TABLE `stl_simulators`
  ADD UNIQUE KEY `id` (`id`);

--
-- Indici per le tabelle `stl_simulator_status`
--
ALTER TABLE `stl_simulator_status`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `stl_simulator_status_values`
--
ALTER TABLE `stl_simulator_status_values`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `simulator_status_values_index` (`simulator_id`,`datetime_start`);

--
-- Indici per le tabelle `stl_technical_items`
--
ALTER TABLE `stl_technical_items`
  ADD UNIQUE KEY `id` (`id`);

--
-- Indici per le tabelle `stl_training_type`
--
ALTER TABLE `stl_training_type`
  ADD UNIQUE KEY `id` (`id`);

--
-- Indici per le tabelle `stl_users`
--
ALTER TABLE `stl_users`
  ADD UNIQUE KEY `id` (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `user_index` (`name`,`lastname`);

--
-- AUTO_INCREMENT per le tabelle scaricate
--

--
-- AUTO_INCREMENT per la tabella `stl_customers`
--
ALTER TABLE `stl_customers`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;
--
-- AUTO_INCREMENT per la tabella `stl_defects`
--
ALTER TABLE `stl_defects`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT per la tabella `stl_defect_type`
--
ALTER TABLE `stl_defect_type`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT per la tabella `stl_device_performance`
--
ALTER TABLE `stl_device_performance`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;
--
-- AUTO_INCREMENT per la tabella `stl_frequent_users`
--
ALTER TABLE `stl_frequent_users`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
--
-- AUTO_INCREMENT per la tabella `stl_maintenance_type`
--
ALTER TABLE `stl_maintenance_type`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT per la tabella `stl_pages`
--
ALTER TABLE `stl_pages`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
--
-- AUTO_INCREMENT per la tabella `stl_periodical_test`
--
ALTER TABLE `stl_periodical_test`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
--
-- AUTO_INCREMENT per la tabella `stl_periodical_test_values`
--
ALTER TABLE `stl_periodical_test_values`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT per la tabella `stl_simulators`
--
ALTER TABLE `stl_simulators`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT per la tabella `stl_simulator_status_values`
--
ALTER TABLE `stl_simulator_status_values`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT per la tabella `stl_technical_items`
--
ALTER TABLE `stl_technical_items`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT per la tabella `stl_training_type`
--
ALTER TABLE `stl_training_type`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;
--
-- AUTO_INCREMENT per la tabella `stl_users`
--
ALTER TABLE `stl_users`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
