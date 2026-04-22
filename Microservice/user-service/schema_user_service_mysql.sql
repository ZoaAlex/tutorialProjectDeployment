-- Script MySQL : schema_user_service_mysql.sql
-- Base et jeu de caractères
CREATE DATABASE IF NOT EXISTS `userdb` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `userdb`;

-- Table users (entité User, stratégie JOINED)
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `nom` VARCHAR(100) NOT NULL,
  `prenom` VARCHAR(100) NOT NULL,
  `email` VARCHAR(150) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `must_change_password` TINYINT(1) NOT NULL DEFAULT 1,
  `statut` ENUM('ACTIF','INACTIF','SUSPENDU') NOT NULL DEFAULT 'ACTIF',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_email` (`email`)
) ENGINE=InnoDB;

-- Table roles
CREATE TABLE IF NOT EXISTS `roles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` ENUM('ROLE_ADMIN','ROLE_ENSEIGNANT') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_roles_name` (`name`)
) ENGINE=InnoDB;

-- Table de liaison user_roles (ManyToMany)
CREATE TABLE IF NOT EXISTS `user_roles` (
  `id_user` BIGINT NOT NULL,
  `id_role` BIGINT NOT NULL,
  PRIMARY KEY (`id_user`,`id_role`),
  CONSTRAINT `fk_userroles_user` FOREIGN KEY (`id_user`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_userroles_role` FOREIGN KEY (`id_role`) REFERENCES `roles`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Tables pour les sous-classes (JOINED inheritance)
CREATE TABLE IF NOT EXISTS `administrateurs` (
  `id` BIGINT NOT NULL,
  `matricule` VARCHAR(255),
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_administrateurs_user` FOREIGN KEY (`id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `enseignants` (
  `id` BIGINT NOT NULL,
  `specialite` VARCHAR(150),
  `grade` VARCHAR(100),
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_enseignants_user` FOREIGN KEY (`id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;


-- Indexes supplémentaires
CREATE INDEX `idx_users_email` ON `users` (`email`);
CREATE INDEX `idx_roles_name` ON `roles` (`name`);

-- Seed initial (insérer les rôles définis dans RoleEnum)
INSERT IGNORE INTO `roles` (`name`) VALUES ('ROLE_ADMIN'), ('ROLE_ENSEIGNANT');

-- Fin du script
