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

exports.buyTicket = async (req, res) =>{
    try{
        const movieid = req.body.movieid;
        const show_time = req.body.show_time;
        const userid = req.body.userid;

        const [buyTicket] = await db.query('INSERT INTO tickets (movieid, show_time, purchase_time, userid) VALUES (?, ?, NOW(), ?)', [movieid, show_time, userid]);

        res.status(500).json({message: 'Buy Ticket Succesful'});
    }catch (err){
        res.status(500).json({message: err.message});
    }
}

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