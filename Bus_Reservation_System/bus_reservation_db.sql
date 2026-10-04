CREATE DATABASE bus_reservation_db;

USE bus_reservation_db;

CREATE TABLE buses (
    bus_id INT PRIMARY KEY AUTO_INCREMENT,
    bus_number VARCHAR(20) UNIQUE NOT NULL,
    bus_name VARCHAR(100) NOT NULL,
    source VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    total_seats INT NOT NULL,
    available_seats INT NOT NULL
);


CREATE TABLE passengers (
    passenger_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    age INT,
    gender VARCHAR(10),
    phone VARCHAR(15)
);

CREATE TABLE bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    bus_id INT NOT NULL,
    passenger_id INT NOT NULL,
    seat_number INT NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (bus_id)
        REFERENCES buses(bus_id),

    FOREIGN KEY (passenger_id)
        REFERENCES passengers(passenger_id),

    UNIQUE (bus_id, seat_number)
);


INSERT INTO buses
(bus_number, bus_name, source, destination, total_seats, available_seats)
VALUES
('TN01AB1234', 'Chennai Express', 'Chennai', 'Mayiladuthurai', 40, 40),
('TN02CD5678', 'Tamil Nadu Travels', 'Chennai', 'Thanjavur', 40, 40),
('TN03EF9012', 'Delta Express', 'Mayiladuthurai', 'Chennai', 40, 40),
('TN04EF8321', 'kovai Express', 'Coimbatore', 'Chennai', 40, 40);

SELECT * FROM buses;