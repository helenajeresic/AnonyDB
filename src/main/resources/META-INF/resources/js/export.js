// Funkcija koja pokreće proces izvoza svih podataka.
// Nakon uspješnog izvoza, korisnik će biti obaviješten o uspjehu putem poruke.
// Ako dođe do greške, korisnik će biti obaviješten o pogrešci.
function exportData() {
    fetch("/export/all", { method: "POST" })
        .then((response) => {
            if (!response.ok) {
                throw new Error("Error during data export.");
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