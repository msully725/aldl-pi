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
      "RPM": csvSplit[0]
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
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          RPM: {state.RPM}
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
  );
}

export default App;
