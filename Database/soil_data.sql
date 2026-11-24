create Database Soil_data;
Use soil_data;
CREATE TABLE sangur_soil_data (
    Horizon VARCHAR(10),
    Depth VARCHAR(20),
    `O.C.` DECIMAL(4,2),
    pH DECIMAL(3,1),
    EC DECIMAL(6,3),
    Na DECIMAL(5,2),
    K DECIMAL(5,2),
    Ca DECIMAL(5,2),
    Mg DECIMAL(5,2)
);
INSERT INTO sangur_soil_data
(Horizon, Depth, `O.C.`, pH, EC, Na, K, Ca, Mg)
VALUES
('Ap', '0-20', 0.90, 7.0, 0.050, 0.26, 0.21, 3.4, 9.2),
('B11', '20-55', 0.53, 7.5, 0.048, 0.35, 0.18, 4.0, 10.4),
('B12', '55-80', 0.47, 8.0, 0.034, 0.30, 0.15, 3.8, 7.0),
('B13', '80-115', 0.59, 8.0, 0.041, 0.30, 0.15, 3.8, 7.0),
('B14', '115-170', 0.29, 8.0, 0.053, 0.35, 0.23, 5.0, 5.2);
SELECT * FROM sangur_soil_data;


CREATE TABLE gangotali_series (
    Horizon VARCHAR(10),
    Depth VARCHAR(20),
    `O.C.` DECIMAL(4,2),
    pH DECIMAL(3,1),
    EC DECIMAL(6,3),
    Na DECIMAL(5,2),
    K DECIMAL(5,2),
    Ca DECIMAL(5,2),
    Mg DECIMAL(5,2)
);
INSERT INTO gangotali_series
(Horizon, Depth, `O.C.`, pH, EC, Na, K, Ca, Mg)
VALUES
('Ap', '0-19', 1.05, 5.7, 0.028, 0.26, 0.44, 8, 4),
('B11t', '19-70', 0.60, 7.3, 0.021, 0.26, 0.26, 16, 6),
('B12t', '70-140', 0.60, 7.1, 0.020, 0.23, 0.22, 17, 8);
SELECT * FROM gangotali_series;

USE soil_data;
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);