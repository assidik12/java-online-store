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

