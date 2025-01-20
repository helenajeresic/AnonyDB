// Funkcija za hashiranje primarnog ključa
function applyHashing() {
    const selectedColumn = document.getElementById("primaryKeyColumn").value;
    if (!selectedColumn) {
        alert("Molimo odaberite primarni ključ.");
        return;
    }

    fetch(`/anonymization/hash/${selectedTable}/${selectedColumn}`, { method: "POST" })
        .then((response) => {
            if (!response.ok) {
                return response.text().then((message) => {
                    if (message.includes("Technique already applied to this column")) {
                        alert("Tehnika je već primijenjena na ovaj stupac.");
                    } else {
                        throw new Error("Greška pri hashiranju.");
                    }
                });
            }
            fetchTableData(selectedTable);
            updateTechniquesLog(`Tablici "${selectedTable}" na stupac "${selectedColumn}" primijenjena je tehnika hashiranja.`);
        })
        .catch((error) => {
            console.error("Greška pri hashiranju:", error);
        });
}

// Funkcija za supresiju
function applySuppression() {
    const selectedColumn = document.getElementById("suppressionColumn").value;
    if (!selectedColumn) {
        alert("Molimo odaberite stupac za supresiju.");
        return;
    }

    fetch(`/anonymization/suppression/${selectedTable}/${selectedColumn}`, { method: "POST" })
        .then((response) => {
            if (!response.ok) {
                return response.text().then((message) => {
                    if (message.includes("Technique already applied to this column")) {
                        alert("Tehnika je već primijenjena na ovaj stupac.");
                    } else {
                        throw new Error("Greška pri supresiji.");
                    }
                });
            }
            fetchTableData(selectedTable);
            updateTechniquesLog(`Tablici "${selectedTable}" na stupac "${selectedColumn}" primijenjena je tehnika supresije.`);
        })
        .catch((error) => {
            console.error("Greška pri supresiji:", error);
        });
}

// Funkcija za dodavanje šuma
function applyNoise() {
    const selectedColumn = document.getElementById("noiseColumn").value;
    const noiseParameter = document.getElementById("noiseParameter").value;

    if (!selectedColumn || !noiseParameter) {
        alert("Molimo odaberite stupac i parametar za šum.");
        return;
    }

    fetch(`/anonymization/noise/${selectedTable}/${selectedColumn}?param=${noiseParameter}`, { method: "POST" })
        .then((response) => {
            if (!response.ok) {
                return response.text().then((message) => {
                    if (message.includes("Technique already applied to this column")) {
                        alert("Tehnika je već primijenjena na ovaj stupac.");
                    } else {
                        throw new Error("Greška pri dodavanju šuma.");
                    }
                });
            }
            fetchTableData(selectedTable);
            updateTechniquesLog(`Tablici "${selectedTable}" na stupac "${selectedColumn}" primijenjena je tehnika dodavanja šuma s parametrom ${noiseParameter}.`);
        })
        .catch((error) => {
            console.error("Greška pri dodavanju šuma:", error);
        });
}

// Funkcija za ažuriranje loga tehnika
function updateTechniquesLog(message) {
    const logText = document.getElementById("logText");
    logText.textContent += `\n${message}`;
}
