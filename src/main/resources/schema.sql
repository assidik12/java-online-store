-- ===================================================================
-- Skrip Inisialisasi Database Toko Online (MySQL)
-- ===================================================================

-- 1. Buat Database
CREATE DATABASE IF NOT EXISTS `java_toko_online`
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE `java_toko_online`;

-- 2. Hapus Tabel Lama Jika Ada (Urutan Drop memperhitungkan Foreign Key)
DROP TABLE IF EXISTS `transactions_details`;
DROP TABLE IF EXISTS `transactions`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `user`;

-- 3. Tabel Pengguna (User)
CREATE TABLE `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `role` VARCHAR(20) DEFAULT 'user',
    `phone_number` VARCHAR(20),
    `address` TEXT,
    `pos_code` VARCHAR(10),
    INDEX `idx_user_email` (`email`),
    INDEX `idx_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Tabel Produk (Product)
CREATE TABLE `product` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `price` DOUBLE NOT NULL,
    `stock` INT NOT NULL DEFAULT 0,
    INDEX `idx_product_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Tabel Header Transaksi (Transactions)
CREATE TABLE `transactions` (
    `id_transaction` VARCHAR(64) PRIMARY KEY,
    `user_email` VARCHAR(100) NOT NULL,
    `total_price_amount` INT NOT NULL,
    `status` BOOLEAN DEFAULT TRUE,
    `date` DATE NOT NULL,
    `payment_method` VARCHAR(50) NOT NULL,
    INDEX `idx_tx_user_email` (`user_email`),
    INDEX `idx_tx_date` (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Tabel Detail Transaksi (Transactions Details)
CREATE TABLE `transactions_details` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `transaction_id` VARCHAR(64) NOT NULL,
    `product_id` INT NOT NULL,
    `total_price_product` INT NOT NULL,
    `quantity` INT NOT NULL,
    CONSTRAINT `fk_tx_details_transaction` 
        FOREIGN KEY (`transaction_id`) REFERENCES `transactions`(`id_transaction`) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_tx_details_product` 
        FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) 
        ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX `idx_tx_details_tx_id` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================================
-- 7. Data Awal / Dummy Seed Data untuk Pengujian
-- ===================================================================

-- Sample User (Password default: 'password123' ter-hash dengan BCrypt)
INSERT INTO `user` (`username`, `password`, `email`, `role`, `phone_number`, `address`, `pos_code`) VALUES
('admin', '$2a$12$69/r3O8v7wPZ62X0uIskQe6eHwG9cKjJ.n0P4vM6F81b3vFq1vM2S', 'admin@toko.com', 'admin', '08111111111', 'Kantor Pusat Toko Online', '10110'),
('john_doe', '$2a$12$69/r3O8v7wPZ62X0uIskQe6eHwG9cKjJ.n0P4vM6F81b3vFq1vM2S', 'john@example.com', 'user', '08123456789', 'Jl. Merdeka No. 45 Jakarta', '10220');

-- Sample Produk
INSERT INTO `product` (`name`, `price`, `stock`) VALUES
('Kopi Arabika Gayo 250g', 75000.0, 50),
('Kopi Robusta Lampung 250g', 45000.0, 40),
('Susu UHT Full Cream 1L', 20000.0, 100),
('Teh Celup Melati 25 sachet', 12500.0, 60),
('Gula Pasir Kristal 1kg', 18000.0, 80);
