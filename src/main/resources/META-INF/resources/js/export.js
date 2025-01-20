// Funkcija za izvoz svih promijenjenih tablica u CSV
function exportData() {
    fetch("/export/all", { method: "POST" })
        .then((response) => {
            if (!response.ok) {
                throw new Error("Greška prilikom izvoza podataka.");
            }
            return response.json();
        })
        .then(() => {
            alert("Uspješan izvoz podataka!");
        })
        .catch((error) => {
            alert("Došlo je do pogreške prilikom izvoza.");
        });
}

