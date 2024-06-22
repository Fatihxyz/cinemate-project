// db.js
const mysql = require('mysql2');
const config = require('./config');

const pool = mysql.createPool({
    host: config.database.host,
    user: config.database.user,
    password: config.database.password,
    database: config.database.database
});

module.exports = pool.promise();
