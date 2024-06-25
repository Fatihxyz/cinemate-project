// userController.js
const db = require('../db');

exports.movieDetail = async (req, res) => {
    try {
        // Retrieve the movie ID from the request body, or use a default value
        const movieid = req.body.movieid || 2;
        
        // Fetch movie details and average rating in a single query
        const [movieResult] = await db.query(`
            SELECT 
                m.movieid, 
                m.title, 
                m.genres, 
                ROUND(mr.average_rating, 1) AS average_rating 
            FROM movies m
            LEFT JOIN movie_average_ratings mr ON m.movieid = mr.movieid
            WHERE m.movieid = ?
        `, [movieid]);

        // Check if the movie exists
        if (movieResult.length === 0) {
            return res.status(404).json({ message: 'Movie not found' });
        }

        // Extract movie details and average rating
        const movie = movieResult[0];
        const movieData = [movie.movieid, movie.title.replace(/\r/g, ''), movie.genres.replace(/\r/g, '')];
        const averageRating = movie.average_rating;

        // Fetch user ratings with a limit
        const [ratingUser] = await db.query(`
            SELECT 
                u.username, 
                r.rating 
            FROM ratings r 
            JOIN users u ON r.userID = u.userID 
            WHERE r.movieID = ? 
            LIMIT 10
        `, [movieid]);

        // Format user ratings
        const ratingData = ratingUser.map(rating => [rating.username, rating.rating.toString()]);

        // Construct the response data
        const responseData = {
            movie: [movieData],
            average_rating: averageRating,
            rating: ratingData
        };

        // Send the response
        res.json(responseData);
    } catch (err) {
        // Handle errors
        res.status(500).json({ message: err.message });
    }
};

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
        const page = parseInt(req.query.page) || 1;
        const limit = parseInt(req.query.limit) || 10;
        const offset = (page - 1) * limit;

        console.log(`Title: ${title}`);
        console.log(`Page: ${page}, Limit: ${limit}, Offset: ${offset}`);

        // Ensure the query is optimized with proper indexing on `title` column
        const query = 'SELECT movieid, title, genres FROM movies WHERE title LIKE ? LIMIT ? OFFSET ?';
        const values = [`%${title}%`, limit, offset];

        const [movies] = await db.query(query, values);
        const movieData = movies.map(movie => ({
            movieid: movie.movieid,
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

exports.getRatings = async (req, res) => {
    try {
        let { movieid } = req.params;

        // Set a default movie ID if not provided
        const DEFAULT_MOVIE_ID = 1; // Replace with your actual default movie ID
        movieid = movieid || DEFAULT_MOVIE_ID;

        console.log(`Fetching ratings for movie ID: ${movieid}`);

        const [ratings] = await db.query(
            'SELECT r.rating, UNIX_TIMESTAMP(r.timestamp) * 1000 AS timestamp, u.username ' +
            'FROM ratings r ' +
            'JOIN users u ON r.userid = u.userid ' +
            'WHERE r.movieid = ?',
            [movieid]
        );

        console.log(`Ratings retrieved: ${JSON.stringify(ratings)}`);

        if (ratings.length === 0) {
            res.status(200).json({ message: 'No ratings found for this movie.' });
        } else {
            res.status(200).json(ratings);
        }
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};

exports.getAverageRating = async (req, res) => {
    try {
        let { movieid } = req.params;

        // Set a default movie ID if not provided
        const DEFAULT_MOVIE_ID = 1; // Replace with your actual default movie ID
        movieid = movieid || DEFAULT_MOVIE_ID;

        console.log(`Fetching average rating for movie ID: ${movieid}`);

        const [averageRating] = await db.query(
            'SELECT AVG(r.rating) as average_rating ' +
            'FROM ratings r ' +
            'JOIN users u ON r.userid = u.userid ' +
            'WHERE r.movieid = ?',
            [movieid]
        );

        console.log(`Average rating retrieved: ${JSON.stringify(averageRating)}`);

        if (averageRating.length === 0) {
            res.status(200).json({ message: 'No ratings found for this movie.' });
        } else {
            res.status(200).json(averageRating[0]);
        }
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};




