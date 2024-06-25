const { Storage } = require('@google-cloud/storage');
const path = require('path');
const fs = require('fs');

// Konfigurasi Google Cloud Storage
const storage = new Storage();
const bucketName = 'cinemate-project';
const modelFileName = 'Recomandation_System.ipynb';

// Function to download the model from GCS
async function downloadModel() {
    const destFilename = path.join(__dirname, modelFileName);

    await storage.bucket(bucketName).file(modelFileName).download({ destination: destFilename });

    console.log(`Model downloaded to ${destFilename}`);
    return destFilename;
}

module.exports = {
    downloadModel,
    bucketName,
    modelFileName
};
