// Funkcija za resetiranje svih promjena
function resetAllChanges() {
    fetch("/tables/reset", { method: "POST" })
        .then((response) => {
            if (!response.ok) {
                throw new Error("Greška prilikom resetiranja podataka.");
            }
            // Ponovno učitaj sve tablice i podatke
            fetch("/tables")
                .then((response) => {
                    if (!response.ok) throw new Error("Greška u dohvaćanju tablica.");
                    return response.json();
                })
                .then((tables) => {
                    populateTablesDropdown(tables);
                    toggleFormsAndTable(false);
                    clearTechniquesLog();
                })
                .catch((error) => console.error("Greška u dohvaćanju tablica:", error));
        })
        .catch((error) => {
            alert("Došlo je do pogreške prilikom resetiranja.");
            console.error("Greška prilikom resetiranja:", error);
        });
}

function clearTechniquesLog() {
    const logText = document.getElementById("logText");
    logText.textContent = "";
}