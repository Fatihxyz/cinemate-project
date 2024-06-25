const mysql = require('mysql2');
const config = require('../config');

const connection = mysql.createConnection(config.database);

connection.connect(err => {
  if (err) throw err;
  console.log('Database connected!');
});

// Membuat tabel pengguna jika belum ada
// const createUserTable = `
//   CREATE TABLE IF NOT EXISTS users (
//     userId INT AUTO_INCREMENT PRIMARY KEY,
//     username VARCHAR(150) NOT NULL UNIQUE,
//     email VARCHAR(150) NOT NULL UNIQUE,
//     password VARCHAR(128) NOT NULL
//   )
// `;

// connection.query(createUserTable, (err, results, fields) => {
//   if (err) throw err;
//   console.log('User table created or already exists.');
// });

module.exports = connection;
