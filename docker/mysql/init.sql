-- Script d'initialisation MySQL
-- Crée toutes les bases de données pour chaque microservice

CREATE DATABASE IF NOT EXISTS userdb          CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS dispodb         CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS sallesdb        CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS coursclassedb   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS specialeventdb  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS planningdb      CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Accorde tous les droits à l'utilisateur app sur toutes les bases
GRANT ALL PRIVILEGES ON userdb.*         TO 'app'@'%';
GRANT ALL PRIVILEGES ON dispodb.*        TO 'app'@'%';
GRANT ALL PRIVILEGES ON sallesdb.*       TO 'app'@'%';
GRANT ALL PRIVILEGES ON coursclassedb.*  TO 'app'@'%';
GRANT ALL PRIVILEGES ON specialeventdb.* TO 'app'@'%';
GRANT ALL PRIVILEGES ON planningdb.*     TO 'app'@'%';

FLUSH PRIVILEGES;
