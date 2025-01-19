let selectedTable = "";
let tableData = [];

// Dohvati imena svih tablica u bazi
document.addEventListener("DOMContentLoaded", function () {
    fetch("/tables")
        .then((response) => {
            if (!response.ok) throw new Error("Greška u dohvaćanju tablica.");
            return response.json();
        })
        .then((tables) => populateTablesDropdown(tables))
        .catch((error) => console.error("Greška u dohvaćanju tablica:", error));
});

// Popunjavanje dropdowna s imenima tablica
function populateTablesDropdown(tables) {
    const dropdown = document.getElementById("tables");
    dropdown.innerHTML = `<option value="">-- Odaberite tablicu --</option>`; // Dodaj zadani odabir
    tables.forEach((table) => {
        const option = document.createElement("option");
        option.value = table;
        option.textContent = table;
        dropdown.appendChild(option);
    });
}

// Event listener za promjenu odabrane tablice
document.getElementById("tables").addEventListener("change", function (event) {
    selectedTable = event.target.value;
    toggleFormsAndTable(selectedTable);
    if (selectedTable) {
        fetchTableData(selectedTable);
    }
});


// Prikaz/skrivanje formi i podataka
function toggleFormsAndTable(isVisible) {
    const displayValue = isVisible ? "block" : "none";
    document.getElementById("tableDataContainer").style.display = displayValue;
    document.getElementById("hashingForm").style.display = displayValue;
    document.getElementById("suppressionForm").style.display = displayValue;
    document.getElementById("noiseAdditionForm").style.display = displayValue;
}


// Dohvaćanje podataka za odabranu tablicu
function fetchTableData(tableName) {
    Promise.all([
        fetch(`/tables/${tableName}`).then((response) => {
            if (!response.ok) throw new Error("Greška u dohvaćanju podataka tablice.");
            return response.json();
        }),
        fetch(`/tables/metadata/${tableName}`).then((response) => {
            if (!response.ok) throw new Error("Greška u dohvaćanju metapodataka tablice.");
            return response.json();
        }),
    ])
        .then(([data, metadata]) => {
            tableData = data;
            displayTableData(data);
            populateForms(data, metadata);
        })
        .catch((error) => console.error("Greška u dohvaćanju podataka ili metapodataka:", error));
}

// Prikaz podataka u tablici
function displayTableData(data) {
    const tableHeaders = document.getElementById("tableHeaders");
    const tableBody = document.getElementById("tableBody");

    tableHeaders.innerHTML = "";
    tableBody.innerHTML = "";

    if (data.length > 0) {
        const headers = Object.keys(data[0]);
        headers.forEach((header) => {
            const th = document.createElement("th");
            th.textContent = header;
            tableHeaders.appendChild(th);
        });

        data.forEach((row) => {
            const tr = document.createElement("tr");
            headers.forEach((header) => {
                const td = document.createElement("td");
                td.textContent = row[header];
                tr.appendChild(td);
            });
            tableBody.appendChild(tr);
        });
    } else {
        console.warn("Tablica je prazna.");
    }
}

// Popunjavanje dropdownova za forme
function populateForms(data, metadata) {
    const suppressionDropdown = document.getElementById("suppressionColumn");
    const noiseDropdown = document.getElementById("noiseColumn");
    const hashingDropdown = document.getElementById("primaryKeyColumn");

    hashingDropdown.innerHTML = `<option value="">-- Odaberite stupac --</option>`;
    suppressionDropdown.innerHTML = `<option value="">-- Odaberite stupac --</option>`;
    noiseDropdown.innerHTML = `<option value="">-- Odaberite stupac --</option>`;

    const numericTypes = ["INT", "BIGINT", "FLOAT", "DOUBLE", "DECIMAL", "INT4", "NUMERIC", "SERIAL"];

    metadata.forEach((column) => {
        const { columnName, dataType, isPrimaryKey, isForeignKey } = column;

        if (isPrimaryKey) {
            const hashOption = document.createElement("option");
            hashOption.value = columnName;
            hashOption.textContent = columnName;
            hashingDropdown.appendChild(hashOption);
        }

        if (!isPrimaryKey && !isForeignKey) {
            const suppressionOption = document.createElement("option");
            suppressionOption.value = columnName;
            suppressionOption.textContent = columnName;
            suppressionDropdown.appendChild(suppressionOption);
        }

        if (!isPrimaryKey && !isForeignKey && numericTypes.includes(dataType.toUpperCase())) {
            const noiseOption = document.createElement("option");
            noiseOption.value = columnName;
            noiseOption.textContent = columnName;
            noiseDropdown.appendChild(noiseOption);
        }
    });
}

// Funkcija za hashiranje primarnog ključa
function applyHashing() {
    const selectedColumn = document.getElementById("primaryKeyColumn").value;
    if (!selectedColumn) {
        alert("Molimo odaberite primarni ključ.");
        return;
    }

    fetch(`/anonymization/hash/${selectedTable}/${selectedColumn}`, { method: "POST" })
        .then((response) => {
            if (!response.ok) throw new Error("Greška pri hashiranju.");
            alert(`Hashiranje primijenjeno na primarni ključ: ${selectedColumn}`);
            fetchTableData(selectedTable);
        })
        .catch((error) => console.error("Greška pri hashiranju:", error));
}

// Funkcija za supresiju
function applySuppression() {
    const selectedColumn = document.getElementById("suppressionColumn").value;
    if (!selectedColumn) {
        alert("Molimo odaberite stupac za supresiju.");
        return;
    }

    fetch(`/anonymization/supression/${selectedTable}/${selectedColumn}`, { method: "POST" })
        .then((response) => {
            if (!response.ok) throw new Error("Greška pri supresiji.");
            alert(`Supresija primijenjena na stupac: ${selectedColumn}`);
            fetchTableData(selectedTable);
        })
        .catch((error) => console.error("Greška pri supresiji:", error));
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
            if (!response.ok) throw new Error("Greška pri dodavanju šuma.");
            alert(`Šum primijenjen na stupac: ${selectedColumn}`);
            fetchTableData(selectedTable);
        })
        .catch((error) => console.error("Greška pri dodavanju šuma:", error));
}

// Funkcija za izvoz svih promijenjenih tablica u CSV
function exportData() {
    fetch("/export/all", { method: "POST" })
        .then((response) => {
            if (!response.ok) {
                throw new Error("Greška prilikom izvoza podataka.");
            }
            return response.json();
        })
        .then((data) => {
            alert("Uspješan izvoz podataka!");
        })
        .catch((error) => {
            alert("Došlo je do pogreške prilikom izvoza.");
        });
}
