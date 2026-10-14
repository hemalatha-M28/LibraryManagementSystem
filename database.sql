CREATE DATABASE IF NOT EXISTS librarydb;

USE librarydb;

CREATE TABLE IF NOT EXISTS book (
    book_id VARCHAR(20) PRIMARY KEY,
    book_title VARCHAR(100),
    author_name VARCHAR(100),
    status VARCHAR(20)
);

INSERT IGNORE INTO book VALUES
('B101', 'Java Basics', 'James', 'Available'),
('B102', 'C Programming', 'Dennis', 'Issued'),
('B103', 'Data Structures', 'Mark', 'Available'),
('B104', 'Operating Systems', 'Galvin', 'Reserved'),
('B105', 'Database Management', 'Korth', 'Lost');
