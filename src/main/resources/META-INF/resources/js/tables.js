let selectedTable = "";
let currentPage = 1;
let itemsPerPage = 15;
let totalItems = 0;
let tableData = [];
let metaData = [];

// Funkcija koja se poziva nakon učitavanja DOM-a.
// Dohvaća popis tablica sa servera i popunjava dropdown s tim tablicama.
// Ako dođe do greške prilikom dohvaćanja podataka, prijavit će se u konzolu.
document.addEventListener("DOMContentLoaded", function () {
    toggleFormsAndTable(false);

    fetch("/tables")
        .then((response) => {
            if (!response.ok) throw new Error("Error fetching tables.");
            return response.json();
        })
        .then((tables) => populateTablesDropdown(tables))
        .catch((error) => console.error("Error fetching tables:", error));
});

// Event listener za promjenu odabrane tablice.
// Kada korisnik odabere tablicu, poziva se funkcija za dohvat podataka te tablice.
document.getElementById("tables").addEventListener("change", function (event) {
    selectedTable = event.target.value;
    toggleFormsAndTable(selectedTable);
    if (selectedTable) {
        fetchTableData(selectedTable);
    }
});

// Popunjava dropdown s imenima tablica koje su dohvaćene sa servera.
// Ako server ne pošalje valjane tablice, dropdown ostaje prazan ili s osnovnim tekstom.
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

// Prikazuje ili skriva forme i tablicu ovisno o uvjetima.
// Ako je tablica odabrana, forme i tablica će biti vidljive. Inače, bit će skrivene.
function toggleFormsAndTable(isVisible) {
    const displayValue = isVisible ? "block" : "none";

    document.getElementById("tableDataContainer").style.display = displayValue;
    document.getElementById("hashingForm").style.display = displayValue;
    document.getElementById("suppressionForm").style.display = displayValue;
    document.getElementById("noiseAdditionForm").style.display = displayValue;

    const tabLinks = document.getElementsByClassName("tab-link");
    for (let i = 0; i < tabLinks.length; i++) {
        tabLinks[i].style.display = isVisible ? "inline-block" : "none";
    }

    if (isVisible) {
        const hashingTab = document.querySelector(".tab-link");
        openTab({ currentTarget: hashingTab }, 'hashingForm');
    }

    else {
        const tabContents = document.getElementsByClassName("tab-content");
        for (let i = 0; i < tabContents.length; i++) {
            tabContents[i].style.display = "none"; // Skrivanje svih tabova
        }
    }
}

// Dohvaća podatke za odabranu tablicu i njene metapodatke.
// Ako dođe do greške prilikom dohvaćanja, ispisat će se u konzolu.
function fetchTableData(tableName) {
    Promise.all([
        fetch(`/tables/${tableName}`).then((response) => {
            if (!response.ok) throw new Error("Error fetching table data.");
            return response.json();
        }),
        fetch(`/tables/metadata/${tableName}`).then((response) => {
            if (!response.ok) throw new Error("Error fetching table metadata.");
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
        .catch((error) => console.error("Error fetching data or metadata:", error));
}

// Učitaj podatke za trenutnu stranicu tablice.
function loadPageData() {
    const start = (currentPage - 1) * itemsPerPage;
    const end = Math.min(currentPage * itemsPerPage, totalItems);
    const pageData = tableData.slice(start, end);

    displayTableData(pageData);
    fillEmptyRows(pageData.length);
    updatePaginationInfo();
}

// Prikaz podataka u tablici.
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

                if (dateColumns.includes(header) && !isNaN(Date.parse(cellValue))) {
                    cellValue = formatDate(cellValue);
                }

                td.textContent = cellValue;
                tr.appendChild(td);
            });
            tableBody.appendChild(tr);
        });
    } else {
        console.warn("Table is empty.");
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

// Promjena stranice (prethodna ili sljedeća).
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

    let startPage = Math.max(1, currentPage - 2);
    let endPage = Math.min(totalPages, currentPage + 2);

    if (endPage - startPage < 5 && totalPages > 5) {
        if (startPage > 1) {
            startPage = Math.max(1, endPage - 4);
        } else {
            endPage = Math.min(totalPages, startPage + 4);
        }
    }

    for (let i = startPage; i <= endPage; i++) {
        const pageNumber = document.createElement("button");
        pageNumber.textContent = i;
        pageNumber.className = i === currentPage ? "active" : "";
        pageNumber.onclick = () => {
            currentPage = i;
            loadPageData();
        };
        pageNumbers.appendChild(pageNumber);
    }

    if (startPage > 1) {
        const firstPageEllipsis = document.createElement("button");
        firstPageEllipsis.textContent = "...";
        firstPageEllipsis.onclick = () => {
            currentPage = Math.max(1, currentPage - 5);
            loadPageData();
        };
        pageNumbers.insertBefore(firstPageEllipsis, pageNumbers.firstChild);
    }

    if (endPage < totalPages) {
        const lastPageEllipsis = document.createElement("button");
        lastPageEllipsis.textContent = "...";
        lastPageEllipsis.onclick = () => {
            currentPage = Math.min(totalPages, currentPage + 5);
            loadPageData();
        };
        pageNumbers.appendChild(lastPageEllipsis);
    }
}

// Popunjavanje dropdownova za forme (hashiranje, supresija, dodavanje šuma) sa atributima na koje smijemo primjeniti te metode.
function populateForms(data) {
    const suppressionDropdown = document.getElementById("suppressionColumn");
    const noiseDropdown = document.getElementById("noiseColumn");
    const hashingDropdown = document.getElementById("primaryKeyColumn");

    hashingDropdown.innerHTML = `<option value="">-- odaberi atribut --</option>`;
    suppressionDropdown.innerHTML = `<option value="">-- odaberi atribut --</option>`;
    noiseDropdown.innerHTML = `<option value="">-- odaberi atribut --</option>`;

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

// Funkcija koja popunjava prazne redove u tablici ako nema dovoljno podataka za prikaz.
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

// Funkcija za formatiranje datuma u lokalni oblik (DD.MM.YYYY).
function formatDate(isoDate) {
    const date = new Date(isoDate);

    const day = date.getDate().toString().padStart(2, "0");
    const month = (date.getMonth() + 1).toString().padStart(2, "0");
    const year = date.getFullYear();

    return `${day}.${month}.${year}.`;
}
