package main

import (
	"fmt"
	"log"
	"net/http"
)

const port int = 3010

func main() {
	http.HandleFunc("/log/latest", func(w http.ResponseWriter, r *http.Request) {
		fmt.Println("Request!")
	})

	listenAddress := fmt.Sprintf(":%d", port)
	fmt.Println("aldl-api listening at http://localhost" + listenAddress)
	log.Fatal(http.ListenAndServe(listenAddress, nil))
}
