const fs = require('fs').promises;
const outputRatePerSecond = 10;
const sampleLogPath = 'sampleInputLog.csv';

const sleep = ms => {
    return new Promise(resolve => setTimeout(resolve, ms));
}

const readSampleLog = async () => {
    const fileData = await fs.readFile(sampleLogPath, 'utf8');
    let fileDataArray = fileData.split('\n');
    // sampleLog.csv has a header, and also has an unexplained blank line at the end?
    fileDataArray = fileDataArray.slice(1, fileDataArray.length - 1);

    return fileDataArray;
}

const outputAtInterval = async(sampleData, seconds) => {
   const getNextRandomIndex = () => {
       return Math.floor(Math.random() * sampleData.length);
   };

   while(true) {
       const currentIndex = getNextRandomIndex();
       console.log(sampleData[currentIndex]);

       await sleep(1000 * seconds);
   }
}

const main = async () => {
    const sampleData = await readSampleLog();
    outputAtInterval(sampleData, 1);
}

main();
