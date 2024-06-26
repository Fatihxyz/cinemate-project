// controllers/authController.js

const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const db = require('../models/userModel');
const config = require('../config');

exports.register = (req, res) => {
  const { username, email, password } = req.body;

  db.query('SELECT * FROM users WHERE username = ? OR email = ?', [username, email], (err, results) => {
    if (err) return res.status(500).json({ msg: 'Server error' });
    if (results.length) return res.status(400).json({ msg: 'Username or email already exists' });

    bcrypt.hash(password, 10, (err, hashedPassword) => {
      if (err) return res.status(500).json({ msg: 'Error hashing password' });

      const sql = 'INSERT INTO users (username, email, password) VALUES (?, ?, ?)';
      db.query(sql, [username, email, hashedPassword], (err, result) => {
        if (err) return res.status(500).json({ msg: 'Server error' });
        res.status(201).json({ msg: 'User created successfully' });
      });
    });
  });
};

exports.login = (req, res) => {
  const { username = "default_username", password = "default_password" } = req.body;

  db.query('SELECT * FROM users WHERE username = ?', [username], (err, results) => {
    if (err) return res.status(500).json({ msg: 'Server error', userId: "default_userId", accessToken: "default_token" });
    if (!results.length) return res.status(401).json({ msg: 'Bad username or password', userId: "default_userId", accessToken: "default_token" });

    const user = results[0] || { id: "default_userId", password: "default_password" };

    bcrypt.compare(password, user.password, (err, isMatch) => {
      if (err) return res.status(500).json({ msg: 'Server error', userId: "default_userId", accessToken: "default_token" });
      if (!isMatch) return res.status(401).json({ msg: 'Bad username or password', userId: "default_userId", accessToken: "default_token" });

      const payload = { id: user.userid };
      const token = jwt.sign(payload, config.secret, { expiresIn: '1h' });

      res.json({ accessToken: token, userId: user.userid || "default_userId" });
    });
  });
};


exports.logout = (req, res) => {
  // Untuk API berbasis token, logout bisa dilakukan di sisi klien dengan menghapus token
  res.json({ msg: 'Logout successful' });
};
