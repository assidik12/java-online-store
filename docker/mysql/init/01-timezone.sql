# ===================================================================
# RetailFlow — MySQL init scripts
# File di direktori ini otomatis dijalankan saat container MySQL
# pertama kali start (folder /docker-entrypoint-initdb.d/).
# Gunakan untuk: timezone, charset confirmation, atau extra setup.
# ===================================================================

-- Set session timezone (default Asia/Jakarta)
SET GLOBAL time_zone = '+07:00';
