const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');
// const homeController = require('../controllers/homeController');
const theaterController = require('../controllers/theaterController');
const { predict } = require('../controllers/predictController');
const userController = require('../controllers/userController');
const filmController = require('../controllers/filmController');


router.post('/register', authController.register);
router.post('/login', authController.login);
router.post('/logout', authController.logout);

router.get('/home', filmController.getHome);
router.get('/home/load-more', filmController.loadMore);
router.post('/search-film', filmController.searchMovie);
router.get('/search-film', filmController.searchMovie);
router.get('/detail',filmController.movieDetail);

router.get('/theater', theaterController.getTheater)
router.post('/theater', theaterController.getTheater)
router.get('/location', theaterController.getLocation);
router.post('/search-theater', theaterController.searchTheater);

// router.post('/searchtheater', theaterController.searchTheater);
// router.post('/predict', predict);
router.put('/update/:id', userController.updateUser);

router.post('/insert-rating', filmController.insertRatings);
router.post('/buyticket', theaterController.buyTicket);
router.post('/insertscreen', theaterController.insertScreen);


module.exports = router;    