package main

import (
	"fmt"
	"io"
	"log"
	"net/http"
	"os"
)

const port int = 3010
const logFilePath string = "../aldl-samplelog/sampleOutput.log"

func main() {
	http.HandleFunc("/log/latest", func(w http.ResponseWriter, r *http.Request) {
		lastLogLine := readLatestLogLine()
		fmt.Println("lastLogLine:", lastLogLine)
		w.Write([]byte(lastLogLine))
	})

	listenAddress := fmt.Sprintf(":%d", port)
	fmt.Println("aldl-api listening at http://localhost" + listenAddress)
	log.Fatal(http.ListenAndServe(listenAddress, nil))
}

func readLatestLogLine() string {
	return getLastLineWithSeek(logFilePath)
}

func getLastLineWithSeek(filePath string) string {
	f, err := os.Open(filePath)
	check(err)
	defer f.Close()

	line := ""
	var cursor int64 = 0
	stat, _ := f.Stat()
	size := stat.Size()
	for {
		cursor -= 1
		f.Seek(cursor, io.SeekEnd)

		c := make([]byte, 1)
		f.Read(c)

		var newLine byte = 10
		var carriageReturn byte = 13
		if cursor != -1 && (c[0] == newLine || c[0] == byte(carriageReturn)) {
			break
		}

		line = fmt.Sprintf("%s%s", string(c), line)

		if cursor == -size {
			break
		}
	}

	return line
}

func check(e error) {
	if e != nil {
		panic(e)
	}
}
