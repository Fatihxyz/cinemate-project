const db = require('../db');

exports.searchTheater = async (req, res) => {
    try {
        const location = req.body.location || 'Jakarta';
        const theatername = req.body.theatername;
        const [theaters] = await db.query('SELECT theatername FROM theater WHERE theatername LIKE ? AND theaterlocation = ?', [[`%${theatername}%`], location]);
        const theaterData = theaters.map(theater => [
            theater.theatername ? theater.theatername.replace(/\r/g, '') : ''
        ]);
        res.json({ location, theaters: theaterData });
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};

exports.getLocation = async (req, res) => {
    try{
        const [theaterlocation] = await db.query(
            'SELECT DISTINCT theaterlocation FROM theater'
        );

        const locationData = theaterlocation.map(location => [
            location.theaterlocation
        ])

        res.status(500).json(locationData);
    }catch (err){
        res.status(500).json({message: err.message});
    }
}

exports.getTheater = async (req, res) => {
    try {
        const location = req.body.location || 'Jakarta';
        const page = parseInt(req.body.page) || 1; // Default to page 1 if not provided
        const limit = 25; // Default limit
        const offset = (page - 1) * limit; // Calculate the offset

        // Fetch the theaters with pagination
        const [theaters] = await db.query(
            'SELECT theatername FROM theater WHERE theaterlocation = ? LIMIT ? OFFSET ?', 
            [location, limit, offset]
        );

        const theaterData = theaters.map(theater => ({
            theatername: theater.theatername.replace(/\r/g, '') // Clean up the theatername
        }));

        res.status(200).json({ location, theaters: theaterData, page });
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};

exports.buyTicket = async (req, res) => {
    try {
        const movieid = req.body.movieid;
        const show_time = req.body.show_time;
        const userid = req.body.userid;
        const seat = req.body.seat;

        const [buyTicket] = await db.query('INSERT INTO tickets (movieid, show_time, purchase_time, userid, seat) VALUES (?, ?, NOW(), ?, ?)', [movieid, show_time, userid, seat]);

        res.status(200).json({message: 'Buy Ticket Successful'});
    } catch (err) {
        res.status(500).json({message: err.message});
    }
}

exports.getPurchasedSeats = async (req, res) => {
    try {
        const { movieid, show_time } = req.query;

        if (!movieid || !show_time) {
            return res.status(400).json({ message: 'movieid and show_time are required' });
        }

        console.log(`Fetching purchased seats for movieid: ${movieid}, show_time: ${show_time}`);

        const [seats] = await db.query(
            'SELECT seat FROM tickets WHERE movieid = ? AND show_time = ?',
            [movieid, show_time]
        );

        console.log(`Purchased seats: ${JSON.stringify(seats)}`);

        const purchasedSeats = seats.map(seat => seat.seat);

        res.status(200).json(purchasedSeats);
    } catch (err) {
        console.error(`Error fetching purchased seats: ${err.message}`);
        res.status(500).json({ message: err.message });
    }
};

exports.getTickets = async (req, res) => {
    try {
        const userId = req.body.userid; // Assuming userid is sent in the request body

        if (!userId) {
            return res.status(400).json({ message: 'User ID is required' });
        }

        const [tickets] = await db.query(
            `SELECT t.ticketid, t.movieid, t.show_time, t.purchase_time, t.userid, m.title AS movie_title 
             FROM tickets t 
             JOIN movies m ON t.movieid = m.movieid 
             WHERE t.userid = ?`, 
            [userId]
        );

        const ticketData = tickets.map(ticket => ({
            ticketid: ticket.ticketid,
            movieid: ticket.movieid,
            movie_title: ticket.movie_title,
            show_time: ticket.show_time,
            purchase_time: ticket.purchase_time,
            userid: ticket.userid
        }));

        res.status(200).json(ticketData);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};




exports.insertScreen = async (req, res) => {
    try{
        const theaterid = req.body.theaterid;
        const screen_time = req.body.screen_time;

        const[insertScreen] = await db.query('INSERT INTO screen_time (theaterid, screen_time) VALUES (?, ?)',[theaterid, screen_time]);

        res.status(500).json({message: 'Insert Screen Time Succesful!'});
    }catch (err){
        res.status(500).json({ message: err.message });
    }
}


exports.getOngoingMovies = async (req, res) => {
    try {
        const page = parseInt(req.query.page) || 1;
        const size = parseInt(req.query.size) || 10;
        const offset = (page - 1) * size;

        const [moviesResult] = await db.query(`
            SELECT 
                movieid, 
                title, 
                genres 
            FROM movies
            WHERE ongoing = 1
            LIMIT ? OFFSET ?
        `, [size, offset]);

        const movies = moviesResult.map(movie => ({
            movieid: movie.movieid,
            title: movie.title.replace(/\r/g, ''),
            genres: movie.genres.replace(/\r/g, '')
        }));

        const responseData = {
            page: page,
            size: size,
            results: movies
        };

        console.log("Response Data:", responseData); // Add this log
        res.json(responseData);
    } catch (err) {
        console.error("Error fetching ongoing movies:", err.message); // Add this log
        res.status(500).json({ message: err.message });
    }
};



