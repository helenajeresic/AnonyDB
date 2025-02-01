// Funkcija koja primjenjuje tehniku hashiranja na odabrani atribut u tablici.
// Provjerava je li odabrani atribut postavljen kao primarni ključ i zatim pokreće zahtjev za primjenu hashiranja.
// Ako je tehnika već primijenjena ili ako atribut nije primarni ključ, korisnik će biti obaviješten.
// U slučaju uspješne primjene, ažurira podatke i log.
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
                        alert("Tehnika je već primijenjena na ovaj atribut.");
                    } else if (message.includes("Column is not a primary key.")) {
                        alert("Odabrani atribut nije primarni ključ.");
                    } else {
                        throw new Error("Greška prilikom primjene hashiranja.");
                    }
                });
            }
            fetchTableData(selectedTable);
            updateTechniquesLog(`Tehnika hashiranja primijenjena na atribut "${selectedColumn}" u tablici "${selectedTable}".`);
        })
        .catch((error) => {
            console.error("Greška prilikom hashiranja:", error);
        });
}

// Funkcija koja primjenjuje tehniku supresije na odabrani atribut u tablici.
// Provjerava je li atribut za supresiju ispravno odabran i zatim pokreće zahtjev za primjenu tehnike supresije.
// Ako je tehnika već primijenjena ili ako dođe do pogreške, korisnik će biti obaviješten.
// U slučaju uspješne primjene, ažurira podatke i log.
function applySuppression() {
    const selectedColumn = document.getElementById("suppressionColumn").value;
    if (!selectedColumn) {
        alert("Molimo odaberite atribut za supresiju.");
        return;
    }

    fetch(`/anonymization/suppression/${selectedTable}/${selectedColumn}`, { method: "POST" })
        .then((response) => {
            if (!response.ok) {
                return response.text().then((message) => {
                    if (message.includes("Technique already applied to this column.")) {
                        alert("Tehnika je već primijenjena na ovaj atribut.");
                    } else if (message.includes("Column is a primary key or foreign key.")) {
                        alert("Odabrani atribut ne može se obraditi šumom jer je primarni ili strani ključ.");
                    } else {
                        throw new Error("Greška prilikom primjene supresije.");
                    }
                });
            }
            fetchTableData(selectedTable);
            updateTechniquesLog(`Tehnika supresije primijenjena na atribut "${selectedColumn}" u tablici "${selectedTable}".`);
        })
        .catch((error) => {
            console.error("Greška prilikom primjene supresije:", error);
        });
}

// Funkcija koja primjenjuje tehniku dodavanja šuma na odabrani atribut u tablici.
// Provjerava je li odabran atribut za šum i parametar za šum, te pokreće zahtjev za primjenu tehnike dodavanja šuma.
// Ako je tehnika već primijenjena ili ako dođe do pogreške, korisnik će biti obaviješten.
// U slučaju uspješne primjene, ažurira podatke i log.
function applyNoise() {
    const selectedColumn = document.getElementById("noiseColumn").value;
    const noiseParameter = document.getElementById("noiseParameter").value;

    if (!selectedColumn || !noiseParameter) {
        alert("Molimo odaberite atribut i parametar za šum.");
        return;
    }

    fetch(`/anonymization/noise/${selectedTable}/${selectedColumn}?param=${noiseParameter}`, { method: "POST" })
        .then((response) => {
            if (!response.ok) {
                return response.text().then((message) => {
                    if (message.includes("Technique already applied to this column")) {
                        alert("Tehnika je već primijenjena na ovaj atribut.");
                    } else {
                        throw new Error("Greška prilikom dodavanja šuma.");
                    }
                });
            }
            fetchTableData(selectedTable);
            updateTechniquesLog(`Tehnika dodavanja šuma primijenjena na atribut "${selectedColumn}" u tablici "${selectedTable}" s parametrom ${noiseParameter}.`);
        })
        .catch((error) => {
            console.error("Greška prilikom dodavanja šuma:", error);
        });
}

// Funkcija koja ažurira log s primjenjenim tehnikama anonimizacije.
// Svaka primjena tehnike anonimizacije bilježi se u log sa datumom i vremenom kada je tehnika primijenjena.
// Ovaj zapis pomaže pratiti primijenjene promjene.
function updateTechniquesLog(message) {
    const logText = document.getElementById("logText");

    const now = new Date();
    const dateTime = now.toLocaleString();

    const logEntry = document.createElement("div");

    const dateTimeSpan = document.createElement("span");
    dateTimeSpan.classList.add("date-time");
    dateTimeSpan.textContent = `[${dateTime}]`;

    const messageSpan = document.createElement("span");
    messageSpan.textContent = ` ${message}`;

    logEntry.appendChild(dateTimeSpan);
    logEntry.appendChild(messageSpan);

    logText.appendChild(logEntry);
}

// Funkcija koja otvara određeni tab u sučelju i prikazuje njegov sadržaj.
// Ova funkcija omogućuje navigaciju između različitih tabova na korisničkom sučelju.
// Prikazuje odgovarajući tab, dok sve ostale tabove skriva.
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
