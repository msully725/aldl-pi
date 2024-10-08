import express from 'express';
import readLastLines from 'read-last-lines';
import cors from 'cors';
const app = express();
const port = 3010;

app.use(cors());

const logFilePath = "../aldl-samplelog/sampleOutput.log"

const readLatestLogLine = async () => {
    const lastLogLine = await readLastLines.read(logFilePath, 1);
    return lastLogLine;
}

app.get('/log/latest', async (request, response) => {
    try {
        const latestLogLine = await readLatestLogLine();
        response.send(latestLogLine);
    } catch (error) {
        response.status(500).send(error);
    }
});

app.listen(port, () => {
    console.log(`aldl-webapp listening at http://localhost:${port}`);
});
