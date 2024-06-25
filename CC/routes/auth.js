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
router.post('/detail',filmController.movieDetail);


router.get('/theater', theaterController.getTheater)
router.get('/movies/ongoing', theaterController.getOngoingMovies)
router.post('/theater', theaterController.getTheater)
router.get('/location', theaterController.getLocation);
router.post('/search-theater', theaterController.searchTheater);

// router.post('/searchtheater', theaterController.searchTheater);
// router.post('/predict', predict);
router.put('/update/:id', userController.updateUser);

router.post('/insert-rating', filmController.insertRatings);
router.get('/get-rating/:movieid', filmController.getRatings);
router.get('/average-rating/:movieid', filmController.getAverageRating);
router.post('/buyticket', theaterController.buyTicket);
router.get('/getPurchasedSeats', theaterController.getPurchasedSeats);
router.post('/getTicket', theaterController.getTickets);
router.post('/insertscreen', theaterController.insertScreen);


module.exports = router;    