// const tf = require('@tensorflow/tfjs-node');
// const { getModel } = require('./models/modelLoader');

// async function predict(req, res) {
//     const userid = req.body.userid;

//     try {
//         const model = await getModel();
//         const inputTensor = tf.tensor2d([[userid]], [1, 1]); // Assuming model takes userid directly as input
//         const prediction = model.predict(inputTensor).dataSync();
//         res.json({ prediction: Array.from(prediction) });
//     } catch (error) {
//         console.error(error);
//         res.status(500).send('Error occurred while fetching prediction');
//     }
// }

// module.exports = {
//     predict
// };
