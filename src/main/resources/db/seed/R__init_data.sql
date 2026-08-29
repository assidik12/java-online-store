-- ===================================================================
-- 7. Data Awal / Dummy Seed Data untuk Pengujian
-- ===================================================================

-- Sample User (Password default: 'password123' ter-hash dengan BCrypt)
INSERT IGNORE INTO `user` (`username`, `password`, `email`, `role`, `phone_number`, `address`, `pos_code`) VALUES
('admin', '$2a$12$69/r3O8v7wPZ62X0uIskQe6eHwG9cKjJ.n0P4vM6F81b3vFq1vM2S', 'admin@toko.com', 'admin', '08111111111', 'Kantor Pusat Toko Online', '10110'),
('john_doe', '$2a$12$69/r3O8v7wPZ62X0uIskQe6eHwG9cKjJ.n0P4vM6F81b3vFq1vM2S', 'john@example.com', 'user', '08123456789', 'Jl. Merdeka No. 45 Jakarta', '10220');

-- Sample Produk
INSERT IGNORE INTO `product` (`name`, `price`, `stock`) VALUES
('Kopi Arabika Gayo 250g', 75000.0, 50),
('Kopi Robusta Lampung 250g', 45000.0, 40),
('Susu UHT Full Cream 1L', 20000.0, 100),
('Teh Celup Melati 25 sachet', 12500.0, 60),
('Gula Pasir Kristal 1kg', 18000.0, 80);

