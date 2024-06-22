// userController.js
const db = require('../db');

exports.updateUser = async (req, res) => {
    try{
        const userId = req.params.id;
        const { username, email, password } = req.body;

        const [update] = await db.query('UPDATE users SET username = ?, email = ?, password = ? WHERE userid = ?', [username, email, password, userId]);

        if (update.affectedRows === 0) {
            return res.status(404).send({ message: 'User not found' });
        }

        res.status(500).send({message: 'Update Succesful!'});

    }catch(err){
        res.status(500).json({message: err.message});
    }
}

// const updateUser = (req, res) => {
//     const userId = req.params.id;
//     const { username, email, password } = req.body;

//     const query = 'UPDATE users SET username = ?, email = ?, password = ? WHERE userid = ?';
//     const values = [username, email, password, userId];

//     db.query(query, values, (err, results) => {
//     if (err) {
//         return res.status(500).send({ message: 'Error updating user', error: err });
//     }

//     if (results.affectedRows === 0) {
//         return res.status(404).send({ message: 'User not found' });
//     }

//     res.send({ message: 'User updated successfully' });
//     });
// };

// module.exports = {
//     updateUser
// };
