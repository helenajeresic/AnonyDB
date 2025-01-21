// Funkcija za resetiranje svih promjena
function resetAllChanges() {
    const userConfirmed = window.confirm("Jeste li sigurni da želite resetirati sve promjene? Ova akcija će vratiti izvorno stanje baze podataka.");

    if (userConfirmed) {
        fetch("/tables/reset", { method: "POST" })
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Greška prilikom resetiranja podataka.");
                }
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
    } else {
        console.log("Korisnik je otkazao resetiranje.");
    }
}

function clearTechniquesLog() {
    const logText = document.getElementById("logText");
    logText.textContent = "";
}
