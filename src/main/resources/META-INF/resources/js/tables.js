let selectedTable = "";
let currentPage = 1;
let itemsPerPage = 15;
let totalItems = 0;
let tableData = [];
let metaData = [];

// Dohvati imena svih tablica u bazi
document.addEventListener("DOMContentLoaded", function () {
    toggleFormsAndTable(false);

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
    dropdown.innerHTML = `<option value="">-- odaberi tablicu --</option>`;
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

function toggleFormsAndTable(isVisible) {
    const displayValue = isVisible ? "block" : "none";

    // Prikazivanje/sklapanje formi
    document.getElementById("tableDataContainer").style.display = displayValue;
    document.getElementById("hashingForm").style.display = displayValue;
    document.getElementById("suppressionForm").style.display = displayValue;
    document.getElementById("noiseAdditionForm").style.display = displayValue;

    // Prikazivanje ili skrivanje tabova
    const tabLinks = document.getElementsByClassName("tab-link");
    for (let i = 0; i < tabLinks.length; i++) {
        tabLinks[i].style.display = isVisible ? "inline-block" : "none";
    }

    // Ako je prikazano, postavi prvi tab (Hashiranje) kao aktivan
    if (isVisible) {
        openTab(event, 'hashingForm'); // Poziva openTab s 'hashingForm' kao početnim tabom
    }
    // Ako nije prikazano, sakrij aktivne tabove
    else {
        const tabContents = document.getElementsByClassName("tab-content");
        for (let i = 0; i < tabContents.length; i++) {
            tabContents[i].style.display = "none"; // Skrivanje svih tabova
        }
    }
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
            totalItems = data.length;
            currentPage = 1;
            metaData = metadata;
            loadPageData();
            populateForms(data);
        })
        .catch((error) => console.error("Greška u dohvaćanju podataka ili metapodataka:", error));
}

// Učitaj podatke za trenutnu stranicu
function loadPageData() {
    const start = (currentPage - 1) * itemsPerPage;
    const end = Math.min(currentPage * itemsPerPage, totalItems);
    const pageData = tableData.slice(start, end);

    displayTableData(pageData);
    fillEmptyRows(pageData.length);
    updatePaginationInfo();
}

// Prikaz podataka u tablici
function displayTableData(data) {
    const tableHeaders = document.getElementById("tableHeaders");
    const tableBody = document.getElementById("tableBody");

    tableHeaders.innerHTML = "";
    tableBody.innerHTML = "";

    const dateColumns = metaData
        .filter((column) => column.dataType === "date")
        .map((column) => column.columnName);

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
                let cellValue = row[header];

                if (dateColumns.includes(header)) {
                    cellValue = formatDate(cellValue);
                }

                td.textContent = cellValue;
                tr.appendChild(td);
            });
            tableBody.appendChild(tr);
        });
    } else {
        console.warn("Tablica je prazna.");
    }
}

// Ažuriranje informacija o paginaciji
function updatePaginationInfo() {
    const start = (currentPage - 1) * itemsPerPage + 1;
    const end = Math.min(currentPage * itemsPerPage, totalItems);
    document.getElementById("tableInfo").textContent = `${start} - ${end} od ${totalItems}`;

    const prevButton = document.getElementById("prevPage");
    const nextButton = document.getElementById("nextPage");

    prevButton.disabled = currentPage === 1;
    prevButton.classList.toggle("disabled", currentPage === 1);

    nextButton.disabled = currentPage * itemsPerPage >= totalItems;
    nextButton.classList.toggle("disabled", currentPage * itemsPerPage >= totalItems);

    updatePageNumbers();
}

// Promjena stranice
function changePage(direction) {
    if (direction === 'prev' && currentPage > 1) {
        currentPage--;
    } else if (direction === 'next' && currentPage * itemsPerPage < totalItems) {
        currentPage++;
    }
    loadPageData();
}

// Ažuriranje brojeva stranica
function updatePageNumbers() {
    const pageNumbers = document.getElementById("pageNumbers");
    pageNumbers.innerHTML = "";
    const totalPages = Math.ceil(totalItems / itemsPerPage);

    for (let i = 1; i <= totalPages; i++) {
        const pageNumber = document.createElement("button");
        pageNumber.textContent = i;
        pageNumber.className = i === currentPage ? "active" : "";
        pageNumber.onclick = () => {
            currentPage = i;
            loadPageData();
        };
        pageNumbers.appendChild(pageNumber);
    }
}

// Popunjavanje dropdownova za forme
function populateForms(data) {
    const suppressionDropdown = document.getElementById("suppressionColumn");
    const noiseDropdown = document.getElementById("noiseColumn");
    const hashingDropdown = document.getElementById("primaryKeyColumn");

    hashingDropdown.innerHTML = `<option value="">-- Odaberite stupac --</option>`;
    suppressionDropdown.innerHTML = `<option value="">-- Odaberite stupac --</option>`;
    noiseDropdown.innerHTML = `<option value="">-- Odaberite stupac --</option>`;

    const numericTypes = ["INT", "BIGINT", "FLOAT", "DOUBLE", "DECIMAL", "INT4", "NUMERIC", "SERIAL"];

    metaData.forEach((column) => {
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

function fillEmptyRows(currentRowCount) {
    const tableBody = document.getElementById("tableBody");
    const totalRows = 15;

    const emptyRowsToAdd = totalRows - currentRowCount;

    for (let i = 0; i < emptyRowsToAdd; i++) {
        const emptyRow = document.createElement("tr");
        emptyRow.classList.add("empty-row");

        const emptyCell = document.createElement("td");
        emptyCell.colSpan = tableBody.rows[0]?.cells.length || 1;
        emptyCell.textContent = "";
        emptyRow.appendChild(emptyCell);

        tableBody.appendChild(emptyRow);
    }
}

function formatDate(isoDate) {
    const date = new Date(isoDate);

    const day = date.getDate().toString().padStart(2, "0");
    const month = (date.getMonth() + 1).toString().padStart(2, "0");
    const year = date.getFullYear();

    return `${day}.${month}.${year}.`;
}
