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
                    if (message.includes("Technique already applied to this column.")) {
                        alert("Tehnika je već primijenjena na ovaj stupac.");
                    } else if (message.includes("Column is not a primary key.")) {
                        alert("Odabrani stupac nije primarni ključ.");
                    } else {
                        throw new Error("Greška prilikom primjene hashiranja.");
                    }
                });
            }
            fetchTableData(selectedTable);
            updateTechniquesLog(`Tehnika hashiranja primijenjena na stupac "${selectedColumn}" u tablici "${selectedTable}".`);
        })
        .catch((error) => {
            console.error("Greška prilikom hashiranja:", error);
        });
}

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
                    if (message.includes("Technique already applied to this column.")) {
                        alert("Tehnika je već primijenjena na ovaj stupac.");
                    } else if (message.includes("Column is a primary key or foreign key.")) {
                        alert("Odabrani stupac ne može se obraditi šumom jer je primarni ili strani ključ.");
                    } else {
                        throw new Error("Greška prilikom primjene supresije.");
                    }
                });
            }
            fetchTableData(selectedTable);
            updateTechniquesLog(`Tehnika supresije primijenjena na stupac "${selectedColumn}" u tablici "${selectedTable}".`);
        })
        .catch((error) => {
            console.error("Greška prilikom primjene supresije:", error);
        });
}

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
                        throw new Error("Greška prilikom dodavanja šuma.");
                    }
                });
            }
            fetchTableData(selectedTable);
            updateTechniquesLog(`Tehnika dodavanja šuma primijenjena na stupac "${selectedColumn}" u tablici "${selectedTable}" s parametrom ${noiseParameter}.`);
        })
        .catch((error) => {
            console.error("Greška prilikom dodavanja šuma:", error);
        });
}

function updateTechniquesLog(message) {
    const logText = document.getElementById("logText");
    logText.textContent += `\n${message}`;
}

function openTab(event, formName) {
    var i, tabContent, tabLinks;

    tabContent = document.getElementsByClassName("tab-content");
    for (i = 0; i < tabContent.length; i++) {
        tabContent[i].style.display = "none";
    }

    tabLinks = document.getElementsByClassName("tab-link");
    for (i = 0; i < tabLinks.length; i++) {
        tabLinks[i].classList.remove("active");
    }

    document.getElementById(formName).style.display = "block";
    event.currentTarget.classList.add("active");
}
