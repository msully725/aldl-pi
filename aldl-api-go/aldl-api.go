package main

import (
	"fmt"
	"log"
	"net/http"
)

func main() {
	http.HandleFunc("/log/latest", func(w http.ResponseWriter, r *http.Request) {
		fmt.Println("Request!")
	})

	fmt.Println("aldl-api listening at http://localhost:3010")

	log.Fatal(http.ListenAndServe(":3010", nil))
}
