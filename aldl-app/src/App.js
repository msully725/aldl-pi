import logo from './logo.svg';
import './App.css';
import { useEffect, useState, useRef } from 'react';

const latestLogFetchIntervalMs = 125;

function useInterval(callback, delay) {
  const savedCallback = useRef();

  useEffect(() => {
    savedCallback.current = callback;
  }, [callback]);

  useEffect(() => {
    const tick = () => savedCallback.current();

    if (delay !== null) {
      const id = setInterval(tick, delay);

      return () => clearInterval(id);
    }
  }, [callback, delay]);
}

function App() {
  const [state, setState] = useState({});

  const convertLogCsvToObject= (csvRow) => {
    const csvSplit = csvRow.split(',');

    return {
      "RPM": csvSplit[0],
      "TPS": csvSplit[1],
      "MAP": csvSplit[2],
      "SparkAdvance": csvSplit[3],
      "MAF": csvSplit[4],
      "LeftTrim": csvSplit[5],
      "RightTrim": csvSplit[6],
      "RightOxygen": csvSplit[11],
      "LeftOxygen": csvSplit[12],
      "ClosedLoop": csvSplit[13],
      "PowerEnrichment": csvSplit[14],
      "IAC": csvSplit[16],
      "IAT": csvSplit[18],
      "CoolantTemp": csvSplit[19],
      "KnockRetard": csvSplit[20],
      "KnockCount": csvSplit[21],
      "VehicleSpeed": csvSplit[22],
      "Voltage": csvSplit[23],
      "BlockLearnCell": csvSplit[29],
      "BarometricPressure": csvSplit[32],
      "TCC": csvSplit[35],
    };
  }

  function fetchLatestLog() {
    fetch("http://localhost:3010/log/latest")
      .then(response => response.text())
      .then(text => { 
        const logObject = convertLogCsvToObject(text);
        setState(logObject);
      }
    )
  }

  useEffect(fetchLatestLog, []);

  // const interval = useInterval(fetchLatestLog, latestLogFetchIntervalMs);

  return (
    <div className="App">
      {Object.keys(state).map(dataPointKey => 
        <div>
          {dataPointKey}: {state[dataPointKey]}
        </div>
      )}
    </div>
  );
}

export default App;
