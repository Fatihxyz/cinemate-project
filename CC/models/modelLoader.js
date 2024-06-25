// const tf = require('@tensorflow/tfjs-node');
// const { downloadModel } = require('../gcsConfig');

// // Load model
// let model;
// (async () => {
//     const modelPath = await downloadModel();
//     model = await tf.loadLayersModel(`file://${modelPath}`);
//     console.log('Model loaded');
// })();

// async function getModel() {
//     if (!model) {
//         const modelPath = await downloadModel();
//         model = await tf.loadLayersModel(`file://${modelPath}`);
//     }
//     return model;
// }

// module.exports = {
//     getModel
// };