// userController.js
const db = require('../db');

exports.movieDetail = async (req, res) => {
    try{
        const movieid = req.body.movieid;
        const [movies] = await db.query('SELECT movieid, title, genres FROM movies WHERE movieid  = ?', [movieid]);
        const limit = 10;

        const movieData = movies.map(movie => [
            movie.title.replace(/\r/g, ''), 
            movie.genres.replace(/\r/g, '')
        ]);

        const [rating] = await db.query('SELECT ROUND(AVG(rating), 1) AS average_rating FROM ratings WHERE movieID = ?', [movieid]);

        const averageRating = rating.length > 0 ? rating[0].average_rating : null;

        const [ratingUser] = await db.query('SELECT u.username, r.rating FROM ratings r JOIN users u ON r.userID = u.userID WHERE r.movieID = ? LIMIT ?;', [movieid, limit]);

        const ratingData = ratingUser.map(rating => [
            rating.username,
            rating.rating
        ]);

        const responseData = {
            movie: movieData,
            average_rating: averageRating,
            rating : ratingData
        };

        res.json(responseData);
    } catch (err){
        res.status(500).json({ message: err.message });
    }
}

exports.getHome = async (req, res) => {
    try {
        const limit = 25;
        const page = parseInt(req.query.page) || 1; // Get the page number from the query parameters, default to 1 if not provided
        const offset = (page - 1) * limit; // Calculate the offset

        // Fetch the movies with pagination
        const [movies] = await db.query('SELECT title, genres FROM movies LIMIT ? OFFSET ?', [limit, offset]);

        // Map the movie data to the required format
        const movieData = movies.map(movie => [
            movie.title.replace(/\r/g, ''), 
            movie.genres.replace(/\r/g, '')
        ]);

        res.json(movieData);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};

exports.loadMore = async (req, res) => {
    try {
        const offset = parseInt(req.query.offset) || 0;
        const limit = 25;
        const [movies] = await db.query('SELECT title, genres FROM movies LIMIT ? OFFSET ?', [limit, offset]);
        const movieData = movies.map(movie => [
            movie.title.replace(/\r/g, ''), 
            movie.genres.replace(/\r/g, '')
        ]);
        res.json(movieData);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};

exports.searchMovie = async (req, res) => {
    try {
        const title = req.body.title || '';
        const page = parseInt(req.body.page) || 1; // Default to page 1 if not provided
        const limit = parseInt(req.body.limit) || 10; // Default to limit 10 if not provided
        const offset = (page - 1) * limit; // Calculate the offset

        const query = 'SELECT title, genres FROM movies WHERE title LIKE ? LIMIT ? OFFSET ?';
        const values = [`%${title}%`, limit, offset];

        const [movies] = await db.query(query, values);
        const movieData = movies.map(movie => ({
            title: movie.title.replace(/\r/g, ''), 
            genres: movie.genres.replace(/\r/g, '')
        }));

        res.json({ page, limit, movies: movieData });
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};





exports.insertRatings = async (req, res) =>{
    try{
        const userid = req.body.userid;
        const movieid = req.body.movieid;
        const rating = req.body.rating;

        const[insertRating] = await db.query('INSERT INTO ratings (userid, movieid, rating, timestamp) VALUES (?, ?, ?, NOW())', [userid, movieid, rating]);

        res.status(500).json({message: 'Succesful'});

    }catch (err){
        res.status(500).json({ message: err.message });
    }
}

